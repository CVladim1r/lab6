package ru.itmo.prog.lab6.client;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.util.ConsoleInterrupt;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public final class Main {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 8080;

    private Main() {}

    public static void main(String[] args) {
        Locale.setDefault(Locale.forLanguageTag("ru"));
        ConsoleInterrupt.install(() -> System.out.println("\nCtrl+C отключен. Для выхода введите exit."));

        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) {
            if (isInteger(args[0])) {
                port = parsePort(args[0], DEFAULT_PORT);
            } else {
                host = args[0];
            }
        }
        if (args.length >= 2) {
            port = parsePort(args[1], DEFAULT_PORT);
        }

        try (UdpClient udpClient = new UdpClient(host, port);
             Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8)) {
            ClientApp app = new ClientApp(udpClient, System.out, System.err);
            app.run(sc);
        }
    }

    private static boolean isInteger(String value) {
        if (value == null || value.isEmpty()) return false;
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) return false;
        }
        return true;
    }

    private static int parsePort(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Неверный порт: " + value + ". Используется " + fallback);
            return fallback;
        }
    }
}
