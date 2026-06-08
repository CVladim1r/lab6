package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.network.CommandType;

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class ClientCommandRegistry {
    private final Map<String, ClientCommand> registry = new LinkedHashMap<>();
    private final PrintStream out;

    public ClientCommandRegistry(UdpClient client, PrintStream out, PrintStream err) {
        this.out = out;

        ExecuteScriptClientCommand scriptCmd = new ExecuteScriptClientCommand(this, err);

        registry.put("help", new HelpClientCommand(this::all, out));
        registry.put("info", new SimpleRequestCommand(CommandType.INFO, "тип, дата, размер коллекции", client, out));
        registry.put("show", new SimpleRequestCommand(CommandType.SHOW, "все элементы", client, out));
        registry.put("insert", new InsertClientCommand(client, out, err));
        registry.put("update", new UpdateClientCommand(client, out, err));
        registry.put("remove_key", new IntArgRequestCommand("remove_key", CommandType.REMOVE_KEY, "remove_key <key> - удалить по ключу", client, out));
        registry.put("remove_lower", new RemoveLowerClientCommand(client, out, err));
        registry.put("remove_greater_key", new IntArgRequestCommand("remove_greater_key", CommandType.REMOVE_GREATER_KEY, "remove_greater_key <key> - удалить с ключом > key", client, out));
        registry.put("remove_lower_key", new IntArgRequestCommand("remove_lower_key", CommandType.REMOVE_LOWER_KEY, "remove_lower_key <key> - удалить с ключом < key", client, out));
        registry.put("clear", new SimpleRequestCommand(CommandType.CLEAR, "очистить коллекцию", client, out));
        registry.put("group_counting_by_name", new SimpleRequestCommand(CommandType.GROUP_COUNTING_BY_NAME, "количество элементов по имени", client, out));
        registry.put("print_field_descending_mood", new SimpleRequestCommand(CommandType.PRINT_FIELD_DESCENDING_MOOD, "поле mood по убыванию", client, out));
        registry.put("count_by_weapon_type", new CountByWeaponClientCommand(client, out));
        registry.put("execute_script", scriptCmd);
        registry.put("exit", new ExitClientCommand());
        registry.put("quit", new ExitClientCommand());
    }

    public boolean dispatch(String line, Scanner in) {
        String stripped = line.strip();
        if (stripped.isEmpty() || stripped.startsWith("#")) return true;
        String name = firstToken(stripped);
        if (name == null) return true;
        ClientCommand cmd = registry.get(name);
        if (cmd == null) {
            out.println("Неизвестная команда: " + name + ". Введите help.");
            return true;
        }
        return cmd.execute(stripped, in);
    }

    public Map<String, ClientCommand> all() {
        return Collections.unmodifiableMap(registry);
    }

    private static String firstToken(String line) {
        String t = line.strip();
        if (t.isEmpty()) return null;
        int w = 0;
        while (w < t.length() && !Character.isWhitespace(t.charAt(w))) w++;
        return t.substring(0, w).toLowerCase(Locale.ROOT);
    }
}
