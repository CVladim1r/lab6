package ru.itmo.prog.lab6.server.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.prog.lab6.common.network.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class ResponseSender {
    private static final Logger log = LoggerFactory.getLogger(ResponseSender.class);

    private final DatagramSocket socket;

    public ResponseSender(DatagramSocket socket) {
        this.socket = socket;
    }

    public void send(SocketAddress address, Response response) throws IOException {
        byte[] data = serialize(response);
        DatagramPacket packet = new DatagramPacket(data, data.length, address);
        socket.send(packet);
        log.debug("Отправлен ответ {} байт на {}", data.length, address);
    }

    private byte[] serialize(Response response) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
        }
        return baos.toByteArray();
    }
}
