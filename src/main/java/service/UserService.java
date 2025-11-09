package service;

import dto.UserRequestDto;
import dto.UserResponseDto;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import mapper.UserMapper;
import repository.UserRepository;
import repository.UserRepositoryImpl;

import java.util.List;
import java.util.Set;

@Slf4j
public class UserService {
    private final Validator validator;
    private final UserMapper mapper;
    private final UserRepository repository;

    public UserService(EntityManager em) {
        log.info("Инициализация UserService...");
        try {
            this.validator = Validation.buildDefaultValidatorFactory().getValidator();
            this.mapper = new UserMapper();
            this.repository = new UserRepositoryImpl(em);
            log.info("UserService успешно инициализирован");
        } catch (Exception e) {
            log.error("Ошибка при инициализации UserService", e);
            throw new RuntimeException("Не удалось инициализировать сервис", e);
        }
    }

    public void createUser(UserRequestDto dto) {
        log.info("Создание нового пользователя");
        try {
            validateDto(dto);
            log.debug("Валидация DTO прошла успешно");

            User user = toUserEntity(dto);
            log.debug("Создана сущность пользователя: {}", user.getEmail());

            repository.create(user);
            log.info("Пользователь успешно создан с ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<UserResponseDto> readAllUsers() {
        log.debug("Запрос всех пользователей");
        try {
            List<UserResponseDto> users = repository.findAll();
            log.info("Найдено {} пользователей", users.size());
            return users;
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось получить список пользователей", e);
        }
    }

    public UserResponseDto readUserById(Long id) {
        log.debug("Поиск пользователя по ID: {}", id);
        try {
            validateId(id);
            User found = repository.findById(id);

            if (found == null) {
                log.warn("Пользователь с ID={} не найден", id);
                throw new IllegalArgumentException("Пользователь не найден");
            }

            log.debug("Пользователь найден: ID={}, email={}", found.getId(), found.getEmail());
            return mapper.toDto(found);
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя с ID={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        log.info("Обновление пользователя с ID: {}", id);
        try {
            validateId(id);
            validateDto(dto);
            log.debug("Валидация данных прошла успешно");

            User user = toUserEntity(dto);
            log.debug("Данные для обновления: email={}, name={}", user.getEmail(), user.getName());

            User updatedUser = repository.update(id, user);
            log.info("Пользователь с ID={} успешно обновлен", id);

            return mapper.toDto(updatedUser);
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя с ID={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteUser(Long id) {
        log.info("Удаление пользователя с ID: {}", id);
        try {
            validateId(id);
            repository.delete(id);
            log.info("Пользователь с ID={} успешно удален", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя с ID={}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    private void validateDto(UserRequestDto dto) {
        log.trace("Валидация DTO: {}", dto);
        Set<ConstraintViolation<UserRequestDto>> violations = validator.validate(dto);

        if (!violations.isEmpty()) {
            String errorMsg = violations.iterator().next().getMessage();
            log.warn("Ошибка валидации DTO: {}", errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    private void validateId(Long id) {
        log.trace("Валидация ID: {}", id);
        if (id == null || id <= 0) {
            String errorMsg = "ID должен быть положительным числом";
            log.warn(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

    private User toUserEntity(UserRequestDto dto) {
        log.trace("Преобразование DTO в сущность User");
        try {
            return mapper.toEntity(dto);
        } catch (Exception e) {
            log.error("Ошибка при преобразовании DTO в сущность: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при обработке данных пользователя", e);
        }
    }
}