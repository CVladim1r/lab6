package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.network.CommandType;

import java.io.PrintStream;
import java.util.Scanner;

public class SimpleRequestCommand implements ClientCommand {
    private final CommandType type;
    private final String desc;
    private final UdpClient client;
    private final PrintStream out;

    public SimpleRequestCommand(CommandType type, String desc, UdpClient client, PrintStream out) {
        this.type = type;
        this.desc = desc;
        this.client = client;
        this.out = out;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        CommandUtils.printResponse(CommandUtils.send(client, type), out);
        return true;
    }

    @Override
    public String description() {
        return desc;
    }
}
