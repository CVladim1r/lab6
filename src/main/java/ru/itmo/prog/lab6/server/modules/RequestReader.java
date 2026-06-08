package ru.itmo.prog.lab6.server.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.prog.lab6.common.network.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class RequestReader {
    private static final Logger log = LoggerFactory.getLogger(RequestReader.class);

    public Request deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Request req = (Request) ois.readObject();
            log.debug("Десериализован запрос: {}", req.getCommandType());
            return req;
        }
    }
}
