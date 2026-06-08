package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

import java.time.LocalDateTime;
import java.util.Optional;

public class InsertCommand implements ServerCommand {
    private final CollectionManager manager;

    public InsertCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs() == null || request.getArgs().length == 0) {
            return new Response(false, "insert: ожидается ключ.");
        }
        int key;
        try {
            key = Integer.parseInt(request.getArgs()[0]);
        } catch (NumberFormatException e) {
            return new Response(false, "insert: ключ должен быть числом.");
        }
        HumanBeing hb = request.getHumanBeing();
        if (hb == null) return new Response(false, "insert: отсутствует объект HumanBeing.");

        hb.setId(manager.getNextId());
        hb.setCreationDate(LocalDateTime.now());

        Optional<String> err = manager.insert(key, hb);
        return err.isPresent() ? new Response(false, err.get()) : new Response(true, "Добавлено: id=" + hb.getId() + ", key=" + key + ".");
    }
}
