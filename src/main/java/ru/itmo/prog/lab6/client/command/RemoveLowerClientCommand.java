package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.input.HumanBeingInputReader;
import ru.itmo.prog.lab6.common.input.InputMode;
import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.CommandType;
import ru.itmo.prog.lab6.common.network.Request;

import java.io.PrintStream;
import java.util.Scanner;

public class RemoveLowerClientCommand implements ClientCommand {
    private final UdpClient client;
    private final PrintStream out;
    private final PrintStream err;

    public RemoveLowerClientCommand(UdpClient client, PrintStream out, PrintStream err) {
        this.client = client;
        this.out = out;
        this.err = err;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        try {
            HumanBeing threshold = new HumanBeingInputReader(in, out, "порога")
                    .read(null, null, InputMode.THRESHOLD);
            CommandUtils.printResponse(
                client.send(new Request(CommandType.REMOVE_LOWER, new String[0], threshold)), out);
        } catch (IllegalStateException | IllegalArgumentException e) {
            err.println("Ошибка ввода: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String description() {
        return "remove_lower - удалить меньше порога";
    }
}
