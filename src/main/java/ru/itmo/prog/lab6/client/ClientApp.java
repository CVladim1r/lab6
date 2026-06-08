package ru.itmo.prog.lab6.client;

import ru.itmo.prog.lab6.client.command.ClientCommandRegistry;
import ru.itmo.prog.lab6.client.network.UdpClient;

import java.io.PrintStream;
import java.util.Scanner;

public class ClientApp {
    private final ClientCommandRegistry registry;
    private final PrintStream out;

    public ClientApp(UdpClient udpClient, PrintStream out, PrintStream err) {
        this.out = out;
        this.registry = new ClientCommandRegistry(udpClient, out, err);
    }

    public void run(Scanner in) {
        out.println("Клиент запущен. help - справка. exit - выход.");
        boolean active = true;
        while (active && in.hasNextLine()) {
            out.print("> ");
            active = registry.dispatch(in.nextLine(), in);
        }
    }
}
