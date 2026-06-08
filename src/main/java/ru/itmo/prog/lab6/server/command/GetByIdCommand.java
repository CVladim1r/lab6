package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

import java.util.Optional;

public class GetByIdCommand implements ServerCommand {
    private final CollectionManager manager;

    public GetByIdCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs() == null || request.getArgs().length == 0) {
            return new Response(false, "get_by_id: ожидается id.");
        }
        try {
            int id = Integer.parseInt(request.getArgs()[0]);
            Optional<HumanBeing> hb = manager.getById(id);
            return hb.isPresent()
                    ? new Response(true, "ok", hb.get())
                    : new Response(false, "Элемент с id=" + id + " не найден.");
        } catch (NumberFormatException e) {
            return new Response(false, "get_by_id: id должен быть числом.");
        }
    }
}
