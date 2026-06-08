package ru.itmo.prog.lab6.client.network;

import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class UdpClient implements Closeable {
    private static final int BUFFER_SIZE = 1 << 20;
    private static final int TIMEOUT_MS = 5000;
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 2000;

    private final String host;
    private final int port;
    private DatagramChannel channel;
    private Selector selector;

    public UdpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        if (channel != null && channel.isOpen()) return;
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
    }

    public Response send(Request request) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                connect();
                byte[] data = serialize(request);
                channel.write(ByteBuffer.wrap(data));

                int ready = selector.select(TIMEOUT_MS);
                if (ready == 0) {
                    System.out.println("Сервер временно недоступен (попытка " + attempt + "/" + MAX_RETRIES + ").");
                    resetChannel();
                    waitBeforeRetry(attempt);
                    continue;
                }
                selector.selectedKeys().clear();
                ByteBuffer buf = ByteBuffer.allocate(BUFFER_SIZE);
                channel.read(buf);
                buf.flip();
                byte[] respData = new byte[buf.remaining()];
                buf.get(respData);
                return deserialize(respData);

            } catch (IOException e) {
                System.out.println("Сервер временно недоступен (попытка " + attempt + "/" + MAX_RETRIES + ").");
                resetChannel();
                waitBeforeRetry(attempt);
            } catch (ClassNotFoundException e) {
                System.out.println("Ошибка десериализации ответа: " + e.getMessage());
                return null;
            }
        }
        System.out.println("Сервер временно недоступен. Повторите попытку позже.");
        return null;
    }

    private void waitBeforeRetry(int attempt) {
        if (attempt >= MAX_RETRIES) return;
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void resetChannel() {
        try {
            if (channel != null) channel.close();
            if (selector != null) selector.close();
            channel = null;
            selector = null;
        } catch (IOException ignored) {}
    }

    private byte[] serialize(Request request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private Response deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (Response) ois.readObject();
        }
    }

    @Override
    public void close() {
        resetChannel();
    }
}
