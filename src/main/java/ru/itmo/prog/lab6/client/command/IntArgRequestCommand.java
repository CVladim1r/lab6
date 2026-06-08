package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.network.CommandType;
import ru.itmo.prog.lab6.common.network.Request;

import java.io.PrintStream;
import java.util.Scanner;

public class IntArgRequestCommand implements ClientCommand {
    private final String cmdName;
    private final CommandType type;
    private final String desc;
    private final UdpClient client;
    private final PrintStream out;

    public IntArgRequestCommand(String cmdName, CommandType type, String desc, UdpClient client, PrintStream out) {
        this.cmdName = cmdName;
        this.type = type;
        this.desc = desc;
        this.client = client;
        this.out = out;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        String tail = CommandUtils.argTail(line, cmdName);
        if (tail == null || tail.isEmpty()) {
            out.println("Использование: " + cmdName + " <ключ>");
            return true;
        }
        try {
            int key = Integer.parseInt(tail.split("\\s+")[0]);
            CommandUtils.printResponse(
                client.send(new Request(type, new String[]{String.valueOf(key)})), out);
        } catch (NumberFormatException e) {
            out.println("Ошибка: ожидается целое число.");
        }
        return true;
    }

    @Override
    public String description() {
        return desc;
    }
}
