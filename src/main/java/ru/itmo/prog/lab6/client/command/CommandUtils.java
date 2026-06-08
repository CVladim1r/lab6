package ru.itmo.prog.lab6.client.command;

import ru.itmo.prog.lab6.client.network.UdpClient;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.common.network.CommandType;

import java.io.PrintStream;
import java.util.Locale;

final class CommandUtils {
    private CommandUtils() {}

    static String argTail(String line, String cmdName) {
        String t = line.strip();
        if (!t.toLowerCase(Locale.ROOT).startsWith(cmdName.toLowerCase(Locale.ROOT))) return null;
        return t.substring(cmdName.length()).strip();
    }

    static String firstToken(String line) {
        String t = line.strip();
        if (t.isEmpty()) return null;
        int w = 0;
        while (w < t.length() && !Character.isWhitespace(t.charAt(w))) w++;
        return t.substring(0, w).toLowerCase(Locale.ROOT);
    }

    static void printResponse(Response r, PrintStream out) {
        if (r != null && r.getMessage() != null && !r.getMessage().isEmpty())
            out.println(r.getMessage());
    }

    static Response send(UdpClient client, CommandType type) {
        return client.send(new Request(type));
    }
}
