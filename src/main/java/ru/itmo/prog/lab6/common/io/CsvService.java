package ru.itmo.prog.lab6.common.io;

import ru.itmo.prog.lab6.common.input.BooleanInput;
import ru.itmo.prog.lab6.common.input.DecimalParse;
import ru.itmo.prog.lab6.common.input.EnumInput;
import ru.itmo.prog.lab6.common.model.Car;
import ru.itmo.prog.lab6.common.model.Coordinates;
import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.model.Mood;
import ru.itmo.prog.lab6.common.model.WeaponType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public final class CsvService extends AbstractCsvService {
    private static final String[] HEADER = {
        "key","id","name","x","y","creationDate","realHero","hasToothpick",
        "impactSpeed","weaponType","mood","carName","carCool"
    };
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private CsvService() {}

    public static Map<Integer, HumanBeing> load(String path, CsvMessageConsumer onWarning) throws IOException {
        try (InputStream in = new FileInputStream(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine();
            if (headerLine == null) return new TreeMap<>();
            List<String> firstRow = splitCsvLine(headerLine);
            if (firstRow.size() != HEADER.length) {
                onWarning.accept("Предупреждение: ожидаемый формат: " + String.join(",", HEADER));
            }
            Map<Integer, HumanBeing> result = new TreeMap<>();
            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                try {
                    List<String> cols = splitCsvLine(line);
                    if (cols.size() < HEADER.length) {
                        onWarning.accept("Строка " + lineNo + ": недостаточно полей, пропуск.");
                        continue;
                    }
                    int key = Integer.parseInt(cols.get(0).trim());
                    HumanBeing hb = parseHuman(cols);
                    if (containsId(result, hb.getId())) {
                        onWarning.accept("Строка " + lineNo + ": дубликат id " + hb.getId() + ", пропуск.");
                        continue;
                    }
                    if (result.containsKey(key)) {
                        onWarning.accept("Строка " + lineNo + ": дублирующийся key " + key + ", пропуск.");
                        continue;
                    }
                    result.put(key, hb);
                } catch (Exception e) {
                    onWarning.accept("Строка " + lineNo + ": " + e.getMessage() + " - пропуск.");
                }
            }
            return result;
        }
    }

    private static boolean containsId(Map<Integer, HumanBeing> m, int id) {
        return m.values().stream().anyMatch(h -> h.getId() == id);
    }

    private static HumanBeing parseHuman(List<String> cols) {
        int id = parseInt(cols.get(1), "id");
        if (id <= 0) throw new IllegalArgumentException("id должен быть > 0");
        String name = nullableString(cols.get(2));
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("name не может быть пустым");
        Float x = parseFloat(cols.get(3), "x", true);
        Double y = parseDouble(cols.get(4), "y", true);
        Coordinates c = new Coordinates(x, y);
        String cd = cols.get(5).trim();
        if (cd.isEmpty()) throw new IllegalArgumentException("creationDate пуста");
        LocalDateTime creation;
        try { creation = LocalDateTime.parse(cd, ISO); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("неверный формат даты: " + cd); }
        boolean real = parseBooleanRequired(cols.get(6), "realHero");
        Boolean pick = parseBooleanNullable(cols.get(7));
        Integer imp = parseIntObject(cols.get(8), "impactSpeed", true);
        WeaponType wt = parseWeapon(cols.get(9));
        Mood m = parseMood(cols.get(10));
        Car car = parseCar(cols.get(11), cols.get(12));
        HumanBeing hb = new HumanBeing();
        hb.setId(id);
        hb.setName(name);
        hb.setCoordinates(c);
        hb.setCreationDate(creation);
        hb.setRealHero(real);
        hb.setHasToothpick(pick);
        hb.setImpactSpeed(imp);
        hb.setWeaponType(wt);
        hb.setMood(m);
        hb.setCar(car);
        return hb;
    }

    private static Car parseCar(String nameField, String coolField) {
        String n = nullableString(nameField);
        if (n == null || n.isEmpty()) {
            if (!coolField.trim().isEmpty()) throw new IllegalArgumentException("указан carCool без carName");
            return null;
        }
        return new Car(n, parseBooleanNullable(coolField));
    }

    private static WeaponType parseWeapon(String s) {
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return EnumInput.parseNullable(t, WeaponType.class); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("неверный weaponType: " + t + " - " + e.getMessage()); }
    }

    private static Mood parseMood(String s) {
        String t = s.trim();
        if (t.isEmpty()) return null;
        try { return EnumInput.parseNullable(t, Mood.class); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("неверный mood: " + t + " - " + e.getMessage()); }
    }

    private static int parseInt(String s, String field) {
        String t = s.trim();
        if (t.isEmpty()) throw new IllegalArgumentException(field + " пусто");
        return Integer.parseInt(t);
    }

    private static Integer parseIntObject(String s, String field, boolean required) {
        String t = s.trim();
        if (t.isEmpty()) {
            if (required) throw new IllegalArgumentException(field + " пусто");
            return null;
        }
        return Integer.parseInt(t);
    }

    private static Float parseFloat(String s, String field, boolean required) {
        String t = s.trim();
        if (t.isEmpty()) {
            if (required) throw new IllegalArgumentException(field + " пусто");
            return null;
        }
        return DecimalParse.parseFloat(t);
    }

    private static Double parseDouble(String s, String field, boolean required) {
        String t = s.trim();
        if (t.isEmpty()) {
            if (required) throw new IllegalArgumentException(field + " пусто");
            return null;
        }
        return DecimalParse.parseDouble(t);
    }

    private static boolean parseBooleanRequired(String s, String field) {
        String t = s.trim();
        if (BooleanInput.isTrue(t)) return true;
        if (BooleanInput.isFalse(t)) return false;
        throw new IllegalArgumentException(field + ": ожидается t/f/true/false; получено: " + t);
    }

    private static Boolean parseBooleanNullable(String s) {
        String t = s.trim();
        if (t.isEmpty()) return null;
        if (BooleanInput.isTrue(t)) return true;
        if (BooleanInput.isFalse(t)) return false;
        throw new IllegalArgumentException("hasToothpick: ожидается t/f/true/false или пусто");
    }

    private static String nullableString(String s) {
        return s == null ? null : s.trim();
    }

    public static void save(String path, Map<Integer, HumanBeing> map) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8))) {
            w.write(joinLine(Arrays.asList(HEADER)));
            w.newLine();
            for (Map.Entry<Integer, HumanBeing> e : map.entrySet()) {
                w.write(joinLine(toRow(e.getKey(), e.getValue())));
                w.newLine();
            }
        }
    }

    private static List<String> toRow(int key, HumanBeing hb) {
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(key));
        row.add(String.valueOf(hb.getId()));
        row.add(hb.getName());
        row.add(hb.getCoordinates().getX().toString());
        row.add(hb.getCoordinates().getY().toString());
        row.add(ISO.format(hb.getCreationDate()));
        row.add(hb.isRealHero() ? "true" : "false");
        row.add(hb.getHasToothpick().map(v -> v ? "true" : "false").orElse(""));
        row.add(hb.getImpactSpeed().toString());
        row.add(hb.getWeaponType().map(Enum::name).orElse(""));
        row.add(hb.getMood().map(Enum::name).orElse(""));
        if (hb.getCar().isEmpty()) { row.add(""); row.add(""); }
        else {
            var car = hb.getCar().get();
            row.add(car.getName());
            row.add(car.getCool() == null ? "" : (car.getCool() ? "true" : "false"));
        }
        return row;
    }
}
