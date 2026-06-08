package ru.itmo.prog.lab6.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.prog.lab6.common.util.ConsoleInterrupt;
import ru.itmo.prog.lab6.server.collection.CollectionManager;

import java.util.Locale;

public final class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String DEFAULT_FILE = "data.csv";
    private static final int DEFAULT_PORT = 8080;

    private Main() {}

    public static void main(String[] args) {
        Locale.setDefault(Locale.forLanguageTag("ru"));
        ConsoleInterrupt.install(() -> System.out.println("\nCtrl+C отключен. Для остановки сервера введите stop."));

        String filePath = DEFAULT_FILE;
        int port = DEFAULT_PORT;
        if (args.length >= 1) {
            if (isInteger(args[0])) {
                port = parsePort(args[0], DEFAULT_PORT);
            } else {
                filePath = args[0];
            }
        }
        if (args.length >= 2) {
            port = parsePort(args[1], DEFAULT_PORT);
        }

        if (args.length > 2) {
            System.err.println("Использование: java -jar lab6.jar server [файл.csv] [порт]");
        }

        CollectionManager manager = new CollectionManager(filePath);
        manager.loadFrom(filePath, msg -> log.warn("{}", msg));

        log.info("Загружено {} элементов из {}", manager.getData().size(), filePath);

        ServerApp app = new ServerApp(port, manager);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Завершение работы, сохранение коллекции...");
            try {
                CollectionManager.SaveResult result = manager.saveWithBackup();
                if (result.isPrimary()) {
                    log.info("Коллекция сохранена.");
                } else {
                    log.warn("Основной файл коллекции недоступен: {}", result.getPrimaryError().getMessage());
                    log.warn("Коллекция сохранена в backup-файл {}", result.getPath());
                    System.out.println("[Сервер] Основной файл недоступен: " + result.getPrimaryError().getMessage());
                    System.out.println("[Сервер] Коллекция сохранена в backup-файл: " + result.getPath());
                }
            } catch (Exception e) {
                log.error("Ошибка при сохранении: {}", e.getMessage());
                for (Throwable suppressed : e.getSuppressed()) {
                    log.error("Ошибка backup-сохранения: {}", suppressed.getMessage());
                }
                System.err.println("[Сервер] Коллекция не сохранена: " + e.getMessage());
            }
        }));

        app.run();
        if (app.getExitCode() != 0) {
            System.exit(app.getExitCode());
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
