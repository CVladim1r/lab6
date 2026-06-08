package ru.itmo.prog.lab6.common.input;

public final class DecimalString {
    private DecimalString() {}

    public static String forFloatParsing(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return t;
        int lastDot = t.lastIndexOf('.');
        int lastComma = t.lastIndexOf(',');
        if (lastComma < 0 && lastDot < 0) return t;
        if (lastComma >= 0 && lastDot < 0) {
            if (t.indexOf(',') == t.lastIndexOf(',')) return t.replace(',', '.');
            return t.replace(",", "");
        }
        if (lastDot >= 0 && lastComma < 0) return t;
        if (lastComma > lastDot) return t.replace(".", "").replace(',', '.');
        return t.replace(",", "");
    }
}
