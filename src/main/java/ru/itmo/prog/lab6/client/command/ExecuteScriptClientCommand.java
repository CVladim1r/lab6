package ru.itmo.prog.lab6.client.command;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ExecuteScriptClientCommand implements ClientCommand {
    private static final int MAX_DEPTH = 6;

    private final ClientCommandRegistry registry;
    private final PrintStream err;
    private int depth = 0;

    public ExecuteScriptClientCommand(ClientCommandRegistry registry, PrintStream err) {
        this.registry = registry;
        this.err = err;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        String tail = CommandUtils.argTail(line, "execute_script");
        if (tail == null || tail.isEmpty()) {
            err.println("Использование: execute_script <file>");
            return true;
        }
        if (depth >= MAX_DEPTH) {
            err.println("Слишком глубокая вложенность скриптов.");
            return true;
        }
        depth++;
        try (Scanner sc = new Scanner(new FileInputStream(unquote(tail)), StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                if (!registry.dispatch(sc.nextLine(), sc)) return false;
            }
        } catch (IOException e) {
            err.println("Скрипт: " + e.getMessage());
        } finally {
            depth--;
        }
        return true;
    }

    private static String unquote(String s) {
        String t = s.strip();
        if (t.length() >= 2 && t.charAt(0) == '"' && t.charAt(t.length() - 1) == '"')
            return t.substring(1, t.length() - 1);
        return t;
    }

    @Override
    public String description() {
        return "execute_script <file> - выполнить скрипт";
    }
}
