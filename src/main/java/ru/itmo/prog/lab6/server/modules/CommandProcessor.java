package ru.itmo.prog.lab6.server.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.command.CommandRegistry;
import ru.itmo.prog.lab6.server.command.ServerCommand;

import java.util.Optional;

public class CommandProcessor {
    private static final Logger log = LoggerFactory.getLogger(CommandProcessor.class);

    private final CommandRegistry registry;

    public CommandProcessor(CommandRegistry registry) {
        this.registry = registry;
    }

    public Response process(Request request) {
        log.info("Обработка команды: {}", request.getCommandType());
        Optional<ServerCommand> command = registry.find(request.getCommandType());
        if (!command.isPresent()) {
            return new Response(false, "Неизвестная команда: " + request.getCommandType());
        }
        try {
            return command.get().execute(request);
        } catch (Exception e) {
            log.error("Ошибка выполнения команды {}: {}", request.getCommandType(), e.getMessage());
            return new Response(false, "Ошибка сервера: " + e.getMessage());
        }
    }
}
