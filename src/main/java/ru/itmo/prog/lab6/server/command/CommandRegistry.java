package ru.itmo.prog.lab6.server.command;

import ru.itmo.prog.lab6.common.network.CommandType;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

import java.util.EnumMap;
import java.util.Optional;

public class CommandRegistry {
    private final EnumMap<CommandType, ServerCommand> registry = new EnumMap<>(CommandType.class);

    public CommandRegistry(CollectionManager manager) {
        register(CommandType.HELP, new HelpCommand());
        register(CommandType.INFO, new InfoCommand(manager));
        register(CommandType.SHOW, new ShowCommand(manager));
        register(CommandType.CLEAR, new ClearCommand(manager));
        register(CommandType.INSERT, new InsertCommand(manager));
        register(CommandType.UPDATE, new UpdateCommand(manager));
        register(CommandType.REMOVE_KEY, new RemoveKeyCommand(manager));
        register(CommandType.REMOVE_LOWER, new RemoveLowerCommand(manager));
        register(CommandType.REMOVE_GREATER_KEY, new RemoveGreaterKeyCommand(manager));
        register(CommandType.REMOVE_LOWER_KEY, new RemoveLowerKeyCommand(manager));
        register(CommandType.COUNT_BY_WEAPON_TYPE, new CountByWeaponTypeCommand(manager));
        register(CommandType.GROUP_COUNTING_BY_NAME, new GroupCountingByNameCommand(manager));
        register(CommandType.PRINT_FIELD_DESCENDING_MOOD, new PrintFieldDescendingMoodCommand(manager));
        register(CommandType.GET_BY_ID, new GetByIdCommand(manager));
    }

    private void register(CommandType type, ServerCommand command) {
        registry.put(type, command);
    }

    public Optional<ServerCommand> find(CommandType type) {
        return Optional.ofNullable(registry.get(type));
    }
}
