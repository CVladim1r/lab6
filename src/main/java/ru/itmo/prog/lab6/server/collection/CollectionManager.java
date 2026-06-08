package ru.itmo.prog.lab6.server.collection;

import ru.itmo.prog.lab6.common.io.CsvService;
import ru.itmo.prog.lab6.common.model.Car;
import ru.itmo.prog.lab6.common.model.HumanBeing;
import ru.itmo.prog.lab6.common.model.Mood;
import ru.itmo.prog.lab6.common.model.WeaponType;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private static final DateTimeFormatter SHOW_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int NAME_MAX = 20;
    private static final int CAR_MAX = 24;

    private final TreeMap<Integer, HumanBeing> data = new TreeMap<>();
    private final LocalDateTime initTime;
    private final String filePath;
    private int nextId = 1;

    public CollectionManager(String filePath) {
        this.initTime = LocalDateTime.now();
        this.filePath = filePath;
    }

    public void loadFrom(String path, java.util.function.Consumer<String> onWarning) {
        try {
            Map<Integer, HumanBeing> loaded = CsvService.load(path, onWarning::accept);
            loaded.forEach(data::put);
            recomputeNextId();
        } catch (java.io.FileNotFoundException | java.nio.file.NoSuchFileException e) {
            onWarning.accept("Файл данных не найден, пустая коллекция: " + path);
        } catch (IOException e) {
            onWarning.accept("Ошибка чтения CSV: " + e.getMessage());
        }
    }

    private void recomputeNextId() {
        nextId = data.values().stream().mapToInt(HumanBeing::getId).max().orElse(0) + 1;
    }

    public String getFilePath() { return filePath; }
    public LocalDateTime getInitTime() { return initTime; }
    public int size() { return data.size(); }

    public SortedMap<Integer, HumanBeing> getData() {
        return Collections.unmodifiableSortedMap(data);
    }

    public int getNextId() { return nextId; }

    public void consumeNextId() { nextId++; }

    public void save() throws IOException {
        CsvService.save(filePath, data);
    }

    public SaveResult saveWithBackup() throws IOException {
        try {
            save();
            return new SaveResult(true, filePath, null);
        } catch (IOException primaryError) {
            String backupPath = buildBackupPath(filePath);
            try {
                CsvService.save(backupPath, data);
                return new SaveResult(false, backupPath, primaryError);
            } catch (IOException backupError) {
                IOException result = new IOException("Не удалось сохранить коллекцию в основной файл и backup.", primaryError);
                result.addSuppressed(backupError);
                throw result;
            }
        }
    }

    private static String buildBackupPath(String sourcePath) {
        Path source = Paths.get(sourcePath);
        Path fileName = source.getFileName();
        String name = fileName == null ? "data.csv" : fileName.toString();
        String backupName = backupName(name);
        Path parent = source.getParent();
        return parent == null ? backupName : parent.resolve(backupName).toString();
    }

    private static String backupName(String name) {
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            return name.substring(0, dot) + ".backup" + name.substring(dot);
        }
        return name + ".backup.csv";
    }

    public static final class SaveResult {
        private final boolean primary;
        private final String path;
        private final IOException primaryError;

        private SaveResult(boolean primary, String path, IOException primaryError) {
            this.primary = primary;
            this.path = path;
            this.primaryError = primaryError;
        }

        public boolean isPrimary() { return primary; }
        public String getPath() { return path; }
        public IOException getPrimaryError() { return primaryError; }
    }

    public String buildInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Тип: TreeMap<Integer, HumanBeing>\n");
        sb.append("Дата инициализации: ").append(initTime).append("\n");
        sb.append("Количество элементов: ").append(data.size());
        if (!data.isEmpty()) {
            sb.append("\nПервый ключ: ").append(data.firstKey()).append(", последний: ").append(data.lastKey());
        }
        return sb.toString();
    }

    public String buildShow() {
        if (data.isEmpty()) return "Коллекция пуста.";

        List<Map.Entry<Integer, HumanBeing>> sorted = data.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());
        String[] head = {"key","id","name","x","y","created","realHero","hasToothpick","impact","weapon","mood","car / cool"};
        List<String[]> lines = sorted.stream().map(e -> rowShow(e.getKey(), e.getValue())).collect(Collectors.toList());

        int cols = head.length;
        int[] w = new int[cols];
        for (int j = 0; j < cols; j++) w[j] = head[j].length();
        lines.forEach(line -> {
            for (int j = 0; j < cols; j++) w[j] = Math.max(w[j], line[j].length());
        });

        StringBuilder sb = new StringBuilder();
        appendSep(sb, w);
        appendRow(sb, w, head);
        appendSep(sb, w);
        lines.forEach(line -> appendRow(sb, w, line));
        appendSep(sb, w);
        return sb.toString();
    }

    private static String[] rowShow(int key, HumanBeing h) {
        return new String[]{
            String.valueOf(key),
            String.valueOf(h.getId()),
            fit(h.getName(), NAME_MAX),
            String.valueOf(h.getCoordinates().getX()),
            String.valueOf(h.getCoordinates().getY()),
            h.getCreationDate().format(SHOW_DATE),
            h.isRealHero() ? "yes" : "no",
            showOptBool(h.getHasToothpick().orElse(null)),
            String.valueOf(h.getImpactSpeed()),
            showEnum(h.getWeaponType().orElse(null)),
            showEnum(h.getMood().orElse(null)),
            showCar(h.getCar().orElse(null))
        };
    }

    private static String showCar(Car c) {
        if (c == null) return "-";
        String n = c.getName();
        if (c.getCool() == null) return fit(n + " / -", CAR_MAX);
        return fit(n + (c.getCool() ? " / yes" : " / no"), CAR_MAX);
    }

    private static String showEnum(Enum<?> e) { return e == null ? "-" : e.name(); }

    private static String showOptBool(Boolean b) {
        if (b == null) return "-";
        return b ? "yes" : "no";
    }

    private static String fit(String s, int max) {
        if (s == null) return "-";
        return s.length() <= max ? s : s.substring(0, max - 3) + "...";
    }

    private static void appendSep(StringBuilder sb, int[] w) {
        sb.append("+");
        for (int n : w) { sb.append("-".repeat(n + 2)); sb.append("+"); }
        sb.append("\n");
    }

    private static void appendRow(StringBuilder sb, int[] w, String[] cells) {
        sb.append("|");
        for (int j = 0; j < cells.length; j++) {
            sb.append(String.format(" %-" + w[j] + "s |", cells[j]));
        }
        sb.append("\n");
    }

    public String buildGroupCountingByName() {
        if (data.isEmpty()) return "Коллекция пуста.";
        return data.values().stream().collect(Collectors.groupingBy(HumanBeing::getName, Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> e.getKey() + " : " + e.getValue()).collect(Collectors.joining("\n"));
    }

    public long countByWeaponType(WeaponType type) {
        return data.values().stream().filter(h -> type == null ? h.getWeaponType().isEmpty() : h.getWeaponType().filter(type::equals).isPresent()).count();
    }

    public String buildMoodsDescending() {
        if (data.isEmpty()) return "Коллекция пуста.";
        Comparator<Mood> byOrdDesc = (a, b) -> {
            if (a == b) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Integer.compare(b.ordinal(), a.ordinal());
        };
        return data.values().stream().map(h -> h.getMood().orElse(null)).sorted(byOrdDesc).map(m -> m == null ? "null" : m.name()).collect(Collectors.joining("\n"));
    }

    public int removeLower(HumanBeing threshold) {
        List<Integer> keys = data.entrySet().stream().filter(e -> e.getValue().compareTo(threshold) < 0).map(Map.Entry::getKey).collect(Collectors.toList());
        keys.forEach(data::remove);
        return keys.size();
    }

    public int removeGreaterKey(int key) {
        List<Integer> keys = data.keySet().stream().filter(k -> k > key).collect(Collectors.toList());
        keys.forEach(data::remove);
        return keys.size();
    }

    public int removeLowerKey(int key) {
        List<Integer> keys = data.keySet().stream().filter(k -> k < key).collect(Collectors.toList());
        keys.forEach(data::remove);
        return keys.size();
    }

    public void clear() {
        data.clear();
        recomputeNextId();
    }

    public Optional<String> insert(int key, HumanBeing value) {
        if (data.containsKey(key)) return Optional.of("Ключ " + key + " уже существует.");
        data.put(key, value);
        consumeNextId();
        return Optional.empty();
    }

    public Optional<String> updateByHumanId(int humanId, HumanBeing updated) {
        for (Map.Entry<Integer, HumanBeing> e : data.entrySet()) {
            if (e.getValue().getId() == humanId) {
                updated.setId(humanId);
                updated.setCreationDate(e.getValue().getCreationDate());
                e.setValue(updated);
                return Optional.empty();
            }
        }
        return Optional.of("Элемент с id=" + humanId + " не найден.");
    }

    public Optional<String> removeKey(int key) {
        if (!data.containsKey(key)) return Optional.of("Ключ " + key + " отсутствует.");
        data.remove(key);
        return Optional.empty();
    }

    public Optional<HumanBeing> getById(int id) {
        return data.values().stream().filter(h -> h.getId() == id).findFirst();
    }
}
