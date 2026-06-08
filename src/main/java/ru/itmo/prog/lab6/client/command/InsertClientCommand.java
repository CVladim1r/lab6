package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.input.HumanBeingInputReader;
import ru.itmo.prog.lab6.common.input.InputMode;
import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.CommandType;
import ru.itmo.prog.lab6.common.network.Request;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Scanner;

public class InsertClientCommand implements ClientCommand {
    private final UdpClient client;
    private final PrintStream out;
    private final PrintStream err;

    public InsertClientCommand(UdpClient client, PrintStream out, PrintStream err) {
        this.client = client;
        this.out = out;
        this.err = err;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        String tail = CommandUtils.argTail(line, "insert");
        if (tail == null || tail.isEmpty()) {
            out.println("Использование: insert <key>");
            return true;
        }
        int key;
        try {
            key = Integer.parseInt(tail.split("\\s+")[0]);
        } catch (NumberFormatException e) {
            out.println("Ошибка: ключ должен быть числом.");
            return true;
        }
        try {
            HumanBeing hb = new HumanBeingInputReader(in, out, "нового элемента")
                    .read(null, LocalDateTime.now(), InputMode.NEW);
            CommandUtils.printResponse(
                client.send(new Request(CommandType.INSERT, new String[]{String.valueOf(key)}, hb)), out);
        } catch (IllegalStateException | IllegalArgumentException e) {
            err.println("Ошибка ввода: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String description() {
        return "insert <key> - добавить элемент";
    }
}
