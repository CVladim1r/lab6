package ru.itmo.prog.lab6.common.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class EnumInput {
    private static final int MAX_TYPO_AUTO = 2;

    private EnumInput() {}

    public static <E extends Enum<E>> E parseNullable(String raw, Class<E> type) {
        if (raw == null || raw.isBlank()) return null;
        String s = raw.strip();
        E[] all = type.getEnumConstants();
        Locale loc = Locale.ROOT;
        String sl = s.toLowerCase(loc);

        for (E e : all) {
            if (e.name().equalsIgnoreCase(s)) return e;
        }

        List<E> byPrefix = new ArrayList<>();
        for (E e : all) {
            if (e.name().toLowerCase(loc).startsWith(sl)) byPrefix.add(e);
        }
        if (byPrefix.size() == 1) return byPrefix.get(0);
        if (byPrefix.size() > 1) {
            throw new IllegalArgumentException(
                    "Нескольким подходит начало '" + s + "': " + formatNames(byPrefix) + " - введите длиннее.");
        }

        if (s.length() >= 2) {
            List<E> byContains = new ArrayList<>();
            for (E e : all) {
                if (e.name().toLowerCase(loc).contains(sl)) byContains.add(e);
            }
            if (byContains.size() == 1) return byContains.get(0);
            if (byContains.size() > 1) {
                throw new IllegalArgumentException(
                        "Нескольким подходит вхождение '" + s + "': " + formatNames(byContains) + " - уточните.");
            }
        }

        int min = Arrays.stream(all).mapToInt(e -> levenshtein(sl, e.name().toLowerCase(loc))).min().orElse(0);
        List<E> atMin = new ArrayList<>();
        for (E e : all) {
            if (levenshtein(sl, e.name().toLowerCase(loc)) == min) atMin.add(e);
        }
        if (min <= MAX_TYPO_AUTO && atMin.size() == 1) return atMin.get(0);

        throw new IllegalArgumentException("Неизвестное значение: '" + s + "'." + buildHint(atMin, all));
    }

    private static <E extends Enum<E>> String buildHint(List<E> atMin, E[] all) {
        String allNames = formatNames(Arrays.asList(all));
        if (atMin.isEmpty()) {
            return " Доступны: " + allNames + ".";
        } else if (atMin.size() == 1) {
            return " Возможно, имелось в виду: " + atMin.get(0).name() + "? Доступны: " + allNames + ".";
        } else {
            return " Наиболее похожие: " + formatNames(atMin) + ". Доступны: " + allNames + ".";
        }
    }

    private static <E extends Enum<E>> String formatNames(List<E> list) {
        return list.stream().map(Enum::name).collect(Collectors.joining(", "));
    }

    static int levenshtein(String a, String b) {
        if (a.length() < b.length()) return levenshtein(b, a);
        if (a.isEmpty()) return b.length();
        int[] prev = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            int[] cur = new int[b.length() + 1];
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int sub = prev[j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1);
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), sub);
            }
            prev = cur;
        }
        return prev[b.length()];
    }
}
