package controller;

import dto.UserRequestDto;
import dto.UserResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import view.ConsoleView;
import view.Menu;

import java.util.List;

public class UserConsoleController {
    private static final Logger log = LoggerFactory.getLogger(UserConsoleController.class);
    private final UserService service;
    private final ConsoleView view;
    private final EntityManagerFactory emf;
    private final EntityManager em;

    public UserConsoleController() {
        log.info("Инициализация UserConsoleController...");
        try {
            this.emf = Persistence.createEntityManagerFactory("user-unit");
            this.em = emf.createEntityManager();
            this.service = new UserService(em);
            this.view = new ConsoleView();
            log.info("UserConsoleController успешно инициализирован");
        } catch (Exception e) {
            log.error("Ошибка при инициализации UserConsoleController", e);
            throw new RuntimeException("Не удалось инициализировать контроллер", e);
        }
    }

    public void start() {
        log.info("Запуск приложения");
        try {
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
                            UserRequestDto dto = toDto();
                            service.createUser(dto);
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
                            UserRequestDto dto = toDto();
                            UserResponseDto updated = service.updateUser(id, dto);
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
        } finally {
            log.info("Завершение работы приложения, освобождение ресурсов");
            try {
                if (em != null && em.isOpen()) {
                    em.close();
                }
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            } catch (Exception e) {
                log.error("Ошибка при освобождении ресурсов", e);
            }
            log.info("Приложение завершило работу");
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

    private UserRequestDto toDto() {
        log.debug("Запрос данных пользователя");
        return UserRequestDto.builder()
                .name(view.askUserName())
                .age(view.askUserAge())
                .email(view.askUserEmail())
                .created_at(view.askUserCreateAt())
                .build();
    }
}