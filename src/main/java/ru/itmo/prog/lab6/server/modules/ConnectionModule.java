package ru.itmo.prog.lab6.server.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;

public class ConnectionModule {
    private static final Logger log = LoggerFactory.getLogger(ConnectionModule.class);

    private final DatagramSocket socket;
    private final int bufferSize;

    public ConnectionModule(DatagramSocket socket, int bufferSize) {
        this.socket = socket;
        this.bufferSize = bufferSize;
    }

    public static final class Packet {
        private final byte[] data;
        private final SocketAddress address;

        public Packet(byte[] data, SocketAddress address) {
            this.data = data;
            this.address = address;
        }

        public byte[] data() { return data; }
        public SocketAddress address() { return address; }
    }

    public Packet receive() throws IOException {
        byte[] buffer = new byte[bufferSize];
        DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
        socket.receive(datagram);

        byte[] data = Arrays.copyOf(datagram.getData(), datagram.getLength());
        SocketAddress addr = datagram.getSocketAddress();
        log.debug("Получен пакет {} байт от {}", data.length, addr);
        return new Packet(data, addr);
    }
}
