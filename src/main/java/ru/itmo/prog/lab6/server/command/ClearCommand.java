package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

public class ClearCommand implements ServerCommand {
    private final CollectionManager manager;

    public ClearCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        manager.clear();
        return new Response(true, "Коллекция очищена.");
    }
}
