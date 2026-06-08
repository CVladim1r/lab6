package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

public class RemoveGreaterKeyCommand implements ServerCommand {
    private final CollectionManager manager;

    public RemoveGreaterKeyCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs() == null || request.getArgs().length == 0) {
            return new Response(false, "remove_greater_key: ожидается ключ.");
        }
        try {
            int n = manager.removeGreaterKey(Integer.parseInt(request.getArgs()[0]));
            return new Response(true, "Удалено элементов: " + n);
        } catch (NumberFormatException e) {
            return new Response(false, "remove_greater_key: ключ должен быть числом.");
        }
    }
}
