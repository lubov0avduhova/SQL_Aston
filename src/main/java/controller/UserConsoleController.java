package controller;

import dto.UserResponseDto;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import service.UserService;
import view.ConsoleView;
import view.Menu;

import java.util.List;

@Slf4j
public class UserConsoleController {
    private final UserService service;
    private final ConsoleView view;
    private final UserMapper mapper;

    public UserConsoleController(EntityManager entityManager) {
        log.info("Инициализация UserConsoleController...");
        try {
            this.service = new UserService(entityManager);
            this.view = new ConsoleView();
            mapper = new UserMapper();
            log.info("UserConsoleController успешно инициализирован");
        } catch (Exception e) {
            log.error("Ошибка при инициализации UserConsoleController", e);
            throw new RuntimeException("Не удалось инициализировать контроллер", e);
        }
    }

    public void start() {
        log.info("Запуск приложения");

        boolean running = true;
        while (running) {
            try {
                view.printMenu();
                String input = view.printInput();
                Menu menu = parseInput(input);
                log.debug("Выбрано меню: {}", menu);

                switch (menu) {
                    case CREATE -> {
                        log.info("Начало создания пользователя");
                        service.createUser(mapper.toDto(view));
                        view.printMessage("Пользователь успешно создан");
                        log.info("Пользователь успешно создан");
                    }
                    case READ -> {
                        log.info("Запрос списка пользователей");
                        List<UserResponseDto> users = service.readAllUsers();
                        users.forEach(user -> view.printMessage(user.toString()));
                        log.debug("Получено {} пользователей", users.size());
                    }
                    case READ_BY_ID -> {
                        log.info("Запрос пользователя по ID");
                        Long id = view.askUserId();
                        UserResponseDto user = service.readUserById(id);
                        view.printMessage(user.toString());
                        log.debug("Получен 1 пользователь");
                    }

                    case UPDATE -> {
                        log.info("Начало обновления пользователя");
                        Long id = view.askUserId();
                        log.debug("ID пользователя для обновления: {}", id);
                        UserResponseDto updated = service.updateUser(id, mapper.toDto(view));
                        view.printMessage("Пользователь успешно обновлен: " + updated);
                        log.info("Пользователь с ID={} успешно обновлен", id);
                    }
                    case DELETE -> {
                        log.info("Начало удаления пользователя");
                        Long id = view.askUserId();
                        log.debug("ID пользователя для удаления: {}", id);
                        service.deleteUser(id);
                        view.printMessage("Пользователь успешно удален");
                        log.info("Пользователь с ID={} успешно удален", id);
                    }
                    case EXIT -> {
                        log.info("Завершение работы приложения");
                        running = false;
                    }
                }
            } catch (IllegalArgumentException e) {
                String errorMsg = "Ошибка ввода: " + e.getMessage();
                view.printMessage(errorMsg);
                log.warn(errorMsg);
            } catch (Exception e) {
                String errorMsg = "Произошла непредвиденная ошибка: " + e.getMessage();
                view.printMessage(errorMsg);
                log.error(errorMsg, e);
            }
        }
    }

    private Menu parseInput(String input) {
        try {
            int number = Integer.parseInt(input);
            return Menu.values()[number - 1];
        } catch (Exception e) {
            String errorMsg = "Неверный ввод: " + input;
            log.warn(errorMsg);
            throw new IllegalArgumentException("Неверный ввод, попробуйте снова.");
        }
    }
}