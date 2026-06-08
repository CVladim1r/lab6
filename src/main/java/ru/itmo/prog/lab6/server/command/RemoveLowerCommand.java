package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

public class RemoveLowerCommand implements ServerCommand {
    private final CollectionManager manager;

    public RemoveLowerCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        HumanBeing threshold = request.getHumanBeing();
        if (threshold == null) return new Response(false, "remove_lower: отсутствует объект-порог.");
        int n = manager.removeLower(threshold);
        return new Response(true, "Удалено элементов: " + n);
    }
}
