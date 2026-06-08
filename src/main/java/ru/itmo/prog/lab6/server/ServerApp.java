package ru.itmo.prog.lab6.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.prog.lab6.common.network.Request;
import ru.itmo.prog.lab6.common.network.Response;
import ru.itmo.prog.lab6.server.collection.CollectionManager;
import ru.itmo.prog.lab6.server.command.CommandRegistry;
import ru.itmo.prog.lab6.server.modules.CommandProcessor;
import ru.itmo.prog.lab6.server.modules.ConnectionModule;
import ru.itmo.prog.lab6.server.modules.RequestReader;
import ru.itmo.prog.lab6.server.modules.ResponseSender;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class ServerApp {
    private static final Logger log = LoggerFactory.getLogger(ServerApp.class);
    private static final int BUFFER_SIZE = 65507;
    private static final int ADMIN_POLL_TIMEOUT_MS = 200;

    private final int port;
    private final CollectionManager collectionManager;
    private final Set<SocketAddress> knownClients = new HashSet<>();
    private int exitCode = 0;
    private boolean running = true;

    public ServerApp(int port, CollectionManager collectionManager) {
        this.port = port;
        this.collectionManager = collectionManager;
    }

    public void run() {
        log.info("Запуск сервера на порту {}", port);
        log.info("Файл коллекции: {}", collectionManager.getFilePath());
        System.out.println("[Сервер] Команды администратора: save, stop");

        try (DatagramSocket socket = new DatagramSocket(port);
             BufferedReader adminInput = new BufferedReader(
                     new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            socket.setSoTimeout(ADMIN_POLL_TIMEOUT_MS);
            log.info("Сервер запущен. Серверные команды: save, stop");

            ConnectionModule connectionModule = new ConnectionModule(socket, BUFFER_SIZE);
            RequestReader requestReader = new RequestReader();
            CommandRegistry commandRegistry = new CommandRegistry(collectionManager);
            CommandProcessor commandProcessor = new CommandProcessor(commandRegistry);
            ResponseSender responseSender = new ResponseSender(socket);

            while (running) {
                pollAdminInput(adminInput);
                if (!running) break;

                try {
                    ConnectionModule.Packet packet = connectionModule.receive();

                    logNewConnection(packet.address());
                    log.info("Новый запрос от {}", packet.address());
                    Request request = requestReader.deserialize(packet.data());
                    log.debug("Команда: {}", request.getCommandType());

                    Response response = commandProcessor.process(request);
                    responseSender.send(packet.address(), response);
                    log.info("Ответ отправлен клиенту {}", packet.address());

                } catch (SocketTimeoutException ignored) {
                } catch (ClassNotFoundException e) {
                    log.error("Ошибка десериализации: {}", e.getMessage());
                } catch (IOException e) {
                    log.error("Ошибка ввода-вывода: {}", e.getMessage());
                }
            }

            log.info("Остановка сервера...");

        } catch (BindException e) {
            exitCode = 1;
            log.error("Порт {} занят. Остановите старый сервер или укажите другой порт.", port);
            System.err.println("[Сервер] Порт " + port + " занят.");
            System.err.println("[Сервер] Если прошлый сервер остановлен Ctrl+Z, выполните fg и затем stop.");
        } catch (IOException e) {
            exitCode = 1;
            log.error("Критическая ошибка сервера: {}", e.getMessage(), e);
        }
    }

    private void logNewConnection(SocketAddress address) {
        if (knownClients.add(address)) {
            log.info("Получено новое подключение от клиента {}", address);
        }
    }

    private void pollAdminInput(BufferedReader adminInput) throws IOException {
        while (adminInput.ready()) {
            String line = adminInput.readLine();
            if (line == null) return;
            handleAdminCommand(line.strip().toLowerCase());
        }
    }

    private void handleAdminCommand(String line) {
        if (line.equals("save")) {
            saveCollection();
        } else if (line.equals("stop") || line.equals("exit")) {
            log.info("Команда остановки от администратора.");
            running = false;
        } else if (!line.isEmpty()) {
            System.out.println("[Сервер] Команды: save, stop");
        }
    }

    private CollectionManager.SaveResult saveCollection() {
        try {
            CollectionManager.SaveResult result = collectionManager.saveWithBackup();
            if (result.isPrimary()) {
                log.info("Коллекция сохранена в {}", result.getPath());
                System.out.println("[Сервер] Коллекция сохранена в файл: " + result.getPath());
            } else {
                log.warn("Основной файл коллекции недоступен: {}", result.getPrimaryError().getMessage());
                log.warn("Коллекция сохранена в backup-файл {}", result.getPath());
                System.out.println("[Сервер] Основной файл недоступен: " + result.getPrimaryError().getMessage());
                System.out.println("[Сервер] Коллекция сохранена в backup-файл: " + result.getPath());
            }
            return result;
        } catch (IOException e) {
            log.error("Ошибка сохранения: {}", e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                log.error("Ошибка backup-сохранения: {}", suppressed.getMessage());
            }
            System.out.println("[Сервер] Коллекция не сохранена: " + e.getMessage());
            return null;
        }
    }

    public void stop() {
        running = false;
    }

    public int getExitCode() {
        return exitCode;
    }
}
