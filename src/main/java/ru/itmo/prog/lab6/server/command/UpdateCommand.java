package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

import java.util.Optional;

public class UpdateCommand implements ServerCommand {
    private final CollectionManager manager;

    public UpdateCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs() == null || request.getArgs().length == 0) {
            return new Response(false, "update: ожидается id.");
        }
        int id;
        try {
            id = Integer.parseInt(request.getArgs()[0]);
        } catch (NumberFormatException e) {
            return new Response(false, "update: id должен быть числом.");
        }
        HumanBeing hb = request.getHumanBeing();
        if (hb == null) return new Response(false, "update: отсутствует объект HumanBeing.");

        Optional<String> err = manager.updateByHumanId(id, hb);
        return err.isPresent() ? new Response(false, err.get()) : new Response(true, "Обновлено.");
    }
}
