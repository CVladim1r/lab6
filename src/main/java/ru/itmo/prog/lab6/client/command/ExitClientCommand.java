package ru.itmo.prog.lab6.client.command;

import java.util.Scanner;

public class ExitClientCommand implements ClientCommand {
    @Override
    public boolean execute(String line, Scanner in) {
        return false;
    }

    @Override
    public String description() {
        return "завершить клиент";
    }
}
