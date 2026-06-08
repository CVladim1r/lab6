package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.input.EnumInput;
import ru.itmo.prog.lab6.common.model.WeaponType;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

public class CountByWeaponTypeCommand implements ServerCommand {
    private final CollectionManager manager;

    public CountByWeaponTypeCommand(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        String arg = (request.getArgs() != null && request.getArgs().length > 0)
                ? request.getArgs()[0] : "";
        WeaponType wt = null;
        if (!arg.isEmpty()) {
            try {
                wt = EnumInput.parseNullable(arg, WeaponType.class);
            } catch (IllegalArgumentException e) {
                return new Response(false, "Ошибка: " + e.getMessage());
            }
        }
        return new Response(true, "Количество: " + manager.countByWeaponType(wt));
    }
}
