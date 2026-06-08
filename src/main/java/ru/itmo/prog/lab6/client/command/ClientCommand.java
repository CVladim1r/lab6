package ru.itmo.prog.lab6.client.command;

import java.util.Scanner;

public interface ClientCommand {

    boolean execute(String line, Scanner in);

    String description();
}
