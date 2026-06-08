package ru.itmo.prog.lab6;

import java.util.Arrays;
import java.util.Locale;

public final class Main {
    private static final String DEFAULT_FILE = "data.csv";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "8080";

    private Main() {}

    public static void main(String[] args) {
        if (args.length == 0) {
            runServer(new String[0]);
            return;
        }

        String mode = args[0].toLowerCase(Locale.ROOT);
        if (mode.equals("server")) {
            runServer(Arrays.copyOfRange(args, 1, args.length));
        } else if (mode.equals("client")) {
            runClient(Arrays.copyOfRange(args, 1, args.length));
        } else if (mode.equals("help")) {
            printUsage();
        } else {
            runServer(args);
        }
    }

    private static void runServer(String[] args) {
        ru.itmo.prog.lab6.server.Main.main(serverArgs(args));
    }

    private static void runClient(String[] args) {
        ru.itmo.prog.lab6.client.Main.main(clientArgs(args));
    }

    private static String[] serverArgs(String[] args) {
        if (args.length == 0) return new String[]{DEFAULT_FILE, DEFAULT_PORT};
        if (args.length == 1) {
            if (isInteger(args[0])) return new String[]{DEFAULT_FILE, args[0]};
            return new String[]{args[0], DEFAULT_PORT};
        }
        return new String[]{args[0], args[1]};
    }

    private static String[] clientArgs(String[] args) {
        if (args.length == 0) return new String[]{DEFAULT_HOST, DEFAULT_PORT};
        if (args.length == 1) {
            if (isInteger(args[0])) return new String[]{DEFAULT_HOST, args[0]};
            return new String[]{args[0], DEFAULT_PORT};
        }
        return new String[]{args[0], args[1]};
    }

    private static boolean isInteger(String value) {
        if (value == null || value.isEmpty()) return false;
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) return false;
        }
        return true;
    }

    private static void printUsage() {
        System.out.println("java -jar target/lab6.jar");
        System.out.println("java -jar target/lab6.jar <port>");
        System.out.println("java -jar target/lab6.jar <file.csv> <port>");
        System.out.println("java -jar target/lab6.jar server [file.csv] [port]");
        System.out.println("java -jar target/lab6.jar client [host] [port]");
    }
}
