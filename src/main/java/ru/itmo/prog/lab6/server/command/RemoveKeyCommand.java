package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

import java.util.Optional;

public class RemoveKeyCommand implements ServerCommand {
    private final CollectionManager manager;

    public RemoveKeyCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs() == null || request.getArgs().length == 0) {
            return new Response(false, "remove_key: ожидается ключ.");
        }
        try {
            int key = Integer.parseInt(request.getArgs()[0]);
            Optional<String> err = manager.removeKey(key);
            return err.isPresent() ? new Response(false, err.get()) : new Response(true, "Удалено.");
        } catch (NumberFormatException e) {
            return new Response(false, "remove_key: ключ должен быть числом.");
        }
    }
}
