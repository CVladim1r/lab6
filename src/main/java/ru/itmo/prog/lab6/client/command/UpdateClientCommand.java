package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.input.HumanBeingInputReader;
import ru.itmo.prog.lab6.common.input.InputMode;
import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.network.CommandType;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;

import java.io.PrintStream;
import java.util.Scanner;

public class UpdateClientCommand implements ClientCommand {
    private final UdpClient client;
    private final PrintStream out;
    private final PrintStream err;

    public UpdateClientCommand(UdpClient client, PrintStream out, PrintStream err) {
        this.client = client;
        this.out = out;
        this.err = err;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        String tail = CommandUtils.argTail(line, "update");
        if (tail == null || tail.isEmpty()) {
            out.println("Использование: update <id>");
            return true;
        }
        int id;
        try {
            id = Integer.parseInt(tail.split("\\s+")[0]);
        } catch (NumberFormatException e) {
            out.println("Ошибка: id должен быть числом.");
            return true;
        }
        Response getResp = client.send(new Request(CommandType.GET_BY_ID, new String[]{String.valueOf(id)}));
        if (getResp == null) return true;
        if (!getResp.isSuccess()) {
            out.println(getResp.getMessage());
            return true;
        }
        try {
            HumanBeing hb = new HumanBeingInputReader(in, out, "обновления")
                    .read(getResp.getHumanBeing(), null, InputMode.UPDATE);
            CommandUtils.printResponse(
                client.send(new Request(CommandType.UPDATE, new String[]{String.valueOf(id)}, hb)), out);
        } catch (IllegalStateException | IllegalArgumentException e) {
            err.println("Ошибка ввода: " + e.getMessage());
        }
        return true;
    }

    @Override
    public String description() {
        return "update <id> - обновить элемент";
    }
}
