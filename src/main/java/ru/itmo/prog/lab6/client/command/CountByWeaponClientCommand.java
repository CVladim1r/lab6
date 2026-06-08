package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.network.CommandType;
import ru.itmo.prog.lab6.common.network.Request;

import java.io.PrintStream;
import java.util.Scanner;

public class CountByWeaponClientCommand implements ClientCommand {
    private final UdpClient client;
    private final PrintStream out;

    public CountByWeaponClientCommand(UdpClient client, PrintStream out) {
        this.client = client;
        this.out = out;
    }

    @Override
    public boolean execute(String line, Scanner in) {
        String tail = CommandUtils.argTail(line, "count_by_weapon_type");
        String arg = (tail != null && !tail.isEmpty()) ? tail.split("\\s+")[0] : "";
        String[] args = arg.isEmpty() ? new String[0] : new String[]{arg};
        CommandUtils.printResponse(client.send(new Request(CommandType.COUNT_BY_WEAPON_TYPE, args)), out);
        return true;
    }

    @Override
    public String description() {
        return "count_by_weapon_type [type] - счёт по типу оружия";
    }
}
