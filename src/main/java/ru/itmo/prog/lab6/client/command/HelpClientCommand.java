package ru.itmo.prog.lab6.client.command;

import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;

public class HelpClientCommand implements ClientCommand {
    private final Supplier<Map<String, ClientCommand>> commandsSupplier;
    private final PrintStream out;

    public HelpClientCommand(Supplier<Map<String, ClientCommand>> commandsSupplier, PrintStream out) {
        this.commandsSupplier = commandsSupplier;
        this.out = out;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        out.println();
        commandsSupplier.get().forEach((name, cmd) -> {
            if (!name.equals("quit"))
                out.printf("  %-35s  %s%n", name, cmd.description());
        });
        out.println();
        return true;
    }

    @Override
    public String description() {
        return "справка";
    }
}
