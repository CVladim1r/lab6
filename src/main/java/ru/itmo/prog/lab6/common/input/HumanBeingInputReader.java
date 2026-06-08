package ru.itmo.prog.lab6.common.input;

import ru.itmo.prog.lab6.common.model.Car;
import ru.itmo.prog.lab6.common.model.Coordinates;
import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.model.Mood;
import ru.itmo.prog.lab6.common.model.WeaponType;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class HumanBeingInputReader {
    private static final String LINE = "---------------------------";
    private final Scanner scanner;
    private final PrintStream out;
    private final String utfPromptName;

    public HumanBeingInputReader(Scanner scanner, PrintStream out, String utfPromptName) {
        this.scanner = scanner;
        this.out = out;
        this.utfPromptName = utfPromptName;
    }

    public HumanBeing read(HumanBeing base, LocalDateTime now, InputMode mode) {
        HumanBeing hb = (base != null) ? copyShell(base) : new HumanBeing();
        if (mode == InputMode.NEW) {
            hb.setCreationDate(Objects.requireNonNull(now, "creation time for NEW"));
        } else if (mode == InputMode.THRESHOLD) {
            hb.setId(0);
            hb.setCreationDate(LocalDateTime.MIN);
        }
        out.println(LINE);
        if (mode == InputMode.UPDATE) {
            out.println("Обновление полей (id=" + hb.getId() + " и дата создания не меняются).");
        } else if (mode == InputMode.NEW) {
            out.println("Ввод " + utfPromptName + " (id и дата создаются автоматически).");
        } else {
            out.println("Ввод " + utfPromptName + " (поля id и дата в объекте не участвуют в сравнении для команды).");
        }
        readName(hb);
        readCoordinates(hb);
        hb.setRealHero(readBooleanRequired("realHero (y/n, yes/no, t/f, 0/1): "));
        hb.setHasToothpick(readBooleanNullable("hasToothpick (пусто = null; y/n, yes/no, t/f, 0/1): "));
        readImpact(hb);
        readWeapon(hb);
        readMood(hb);
        readCar(hb, mode);
        out.println(LINE);
        return hb;
    }

    private void readName(HumanBeing hb) {
        hb.setName(readNonEmpty("name: "));
    }

    private void readCoordinates(HumanBeing hb) {
        Float x = readFloat("coordinates.x: ", true);
        Double y = readDouble("coordinates.y: ", true);
        hb.setCoordinates(new Coordinates(x, y));
    }

    private void readImpact(HumanBeing hb) {
        hb.setImpactSpeed(readIntNonNull("impactSpeed: "));
    }

    private void readWeapon(HumanBeing hb) {
        out.println("weaponType: можно префикс, любой регистр или пусто = null. Константы: "
                + listEnumNames(WeaponType.class));
        hb.setWeaponType(readEnumNullable(WeaponType.class, "weaponType: "));
    }

    private void readMood(HumanBeing hb) {
        out.println("mood: можно префикс, любой регистр или пусто = null. Константы: "
                + listEnumNames(Mood.class));
        hb.setMood(readEnumNullable(Mood.class, "mood: "));
    }

    private void readCar(HumanBeing hb, InputMode mode) {
        if (mode == InputMode.UPDATE) {
            readCarBlock(hb, "имя car (пусто = null): ");
        } else {
            readCarBlock(hb, "имя car (пусто = нет автомобиля): ");
        }
    }

    private void readCarBlock(HumanBeing hb, String namePrompt) {
        while (true) {
            out.print(namePrompt);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String n = scanner.nextLine().strip();
            if (n.isEmpty()) { hb.setCar(null); return; }
            out.print("car.cool (пусто = null; y/n, yes/no, t/f, 0/1): ");
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String c = scanner.nextLine().strip();
            try {
                Boolean cool;
                if (c.isEmpty()) cool = null;
                else if (BooleanInput.isTrue(c)) cool = true;
                else if (BooleanInput.isFalse(c)) cool = false;
                else { out.println("Ошибка: y/n, yes/no, t/f, 0/1 или пусто."); continue; }
                hb.setCar(new Car(n, cool));
                return;
            } catch (IllegalArgumentException e) {
                out.println("Ошибка: " + e.getMessage() + " Повторите ввод car.");
            }
        }
    }

    private String readNonEmpty(String p) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            if (!s.isEmpty()) return s;
            out.println("Ошибка: строка не может быть пустой. Повторите ввод.");
        }
    }

    private Float readFloat(String p, boolean required) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            if (s.isEmpty() && !required) return null;
            try { return DecimalParse.parseFloat(s); }
            catch (NumberFormatException e) { out.println("Ошибка: ожидается число (float). Повторите ввод."); }
        }
    }

    private Double readDouble(String p, boolean required) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            if (s.isEmpty() && !required) return null;
            try { return DecimalParse.parseDouble(s); }
            catch (NumberFormatException e) { out.println("Ошибка: ожидается число (double). Повторите ввод."); }
        }
    }

    private int readIntNonNull(String p) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            if (s.isEmpty()) { out.println("Ошибка: impactSpeed не может быть null. Повторите ввод."); continue; }
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { out.println("Ошибка: ожидается целое число. Повторите ввод."); }
        }
    }

    private boolean readBooleanRequired(String p) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            if (BooleanInput.isTrue(s)) return true;
            if (BooleanInput.isFalse(s)) return false;
            out.println("Ошибка: y/n, yes/no, t/f, 0/1, true/false. Повторите ввод.");
        }
    }

    private Boolean readBooleanNullable(String p) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            if (s.isEmpty()) return null;
            if (BooleanInput.isTrue(s)) return true;
            if (BooleanInput.isFalse(s)) return false;
            out.println("Ошибка: y/n, yes/no, t/f, 0/1, true/false или пусто. Повторите ввод.");
        }
    }

    private <E extends Enum<E>> E readEnumNullable(Class<E> type, String p) {
        while (true) {
            out.print(p);
            if (!scanner.hasNextLine()) throw new IllegalStateException("неожиданный конец ввода");
            String s = scanner.nextLine().strip();
            try { return EnumInput.parseNullable(s, type); }
            catch (IllegalArgumentException e) { out.println("Ошибка: " + e.getMessage() + " Повторите ввод."); }
        }
    }

    private <E extends Enum<E>> String listEnumNames(Class<E> type) {
        return Arrays.stream(type.getEnumConstants()).map(Enum::name).collect(Collectors.joining(", "));
    }

    private static HumanBeing copyShell(HumanBeing b) {
        HumanBeing h = new HumanBeing();
        h.setId(b.getId());
        h.setName(b.getName());
        h.setCoordinates(b.getCoordinates());
        h.setCreationDate(b.getCreationDate());
        h.setRealHero(b.isRealHero());
        h.setHasToothpick(b.getHasToothpick().orElse(null));
        h.setImpactSpeed(b.getImpactSpeed());
        h.setWeaponType(b.getWeaponType().orElse(null));
        h.setMood(b.getMood().orElse(null));
        h.setCar(b.getCar().orElse(null));
        return h;
    }
}
