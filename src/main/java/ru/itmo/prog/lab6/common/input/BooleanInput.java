package ru.itmo.prog.lab6.common.input;

public final class BooleanInput {
    private BooleanInput() {}

    public static boolean isTrue(String t) {
        if (t == null || t.isEmpty()) return false;
        String s = t.strip();
        return s.equalsIgnoreCase("true") || s.equals("1") || s.equalsIgnoreCase("t") || s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("да");
    }

    public static boolean isFalse(String t) {
        if (t == null || t.isEmpty()) return false;
        String s = t.strip();
        return s.equalsIgnoreCase("false") || s.equals("0") || s.equalsIgnoreCase("f") || s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("нет");
    }
}
