package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

public class GroupCountingByNameCommand implements ServerCommand {
    private final CollectionManager manager;

    public GroupCountingByNameCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        return new Response(true, manager.buildGroupCountingByName());
    }
}
