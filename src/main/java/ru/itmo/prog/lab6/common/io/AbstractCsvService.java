package ru.itmo.prog.lab6.common.io;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCsvService {
    protected AbstractCsvService() {}

    public static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }

    public static String joinLine(List<String> cells) {
        List<String> out = new ArrayList<>();
        for (String c : cells) out.add(escape(c));
        return String.join(",", out);
    }

    public static List<String> splitCsvLine(String line) {
        List<String> r = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQ = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQ) {
                if (ch == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') { cur.append('"'); i++; }
                    else inQ = false;
                } else cur.append(ch);
            } else {
                if (ch == '"') inQ = true;
                else if (ch == ',') { r.add(cur.toString()); cur.setLength(0); }
                else cur.append(ch);
            }
        }
        r.add(cur.toString());
        return r;
    }

    @FunctionalInterface
    public interface CsvMessageConsumer {
        void accept(String message);
    }
}
