package repository;

import dto.UserResponseDto;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        log.debug("Инициализация UserRepositoryImpl с EntityManager");
        this.em = em;
    }

    @Override
    public void create(User user) {
        log.debug("Создание нового пользователя: {}", user.getEmail());
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(user);
            transaction.commit();
            log.info("Пользователь успешно создан с ID: {}", user.getId());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                log.warn("Откат транзакции при создании пользователя", e);
                transaction.rollback();
            }
            log.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать пользователя", e);
        }
    }

    @Override
    public List<UserResponseDto> findAll() {
        log.debug("Запрос всех пользователей");
        try {
            List<User> users = em.createQuery("from User", User.class).getResultList();
            log.debug("Найдено {} пользователей", users.size());
            return users.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось получить список пользователей", e);
        }
    }

    @Override
    public User update(Long id, User updatedUser) {
        log.debug("Обновление пользователя с ID: {}", id);
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            User existing = em.find(User.class, id);
            if (existing == null) {
                log.warn("Пользователь с ID={} не найден", id);
                throw new IllegalArgumentException("Пользователь с id=" + id + " не найден");
            }

            log.debug("Обновление данных пользователя: name={}, email={}",
                    updatedUser.getName(), updatedUser.getEmail());

            existing.setName(updatedUser.getName());
            existing.setAge(updatedUser.getAge());
            existing.setEmail(updatedUser.getEmail());
            existing.setCreatedAt(updatedUser.getCreatedAt());

            em.merge(existing);
            transaction.commit();
            log.info("Пользователь с ID={} успешно обновлен", id);

            return existing;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                log.warn("Откат транзакции при обновлении пользователя", e);
                transaction.rollback();
            }
            log.error("Ошибка при обновлении пользователя с ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаление пользователя с ID: {}", id);
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                log.info("Пользователь с ID={} удален", id);
            } else {
                log.warn("Попытка удаления несуществующего пользователя с ID={}", id);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                log.warn("Откат транзакции при удалении пользователя", e);
                transaction.rollback();
            }
            log.error("Ошибка при удалении пользователя с ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Не удалось удалить пользователя", e);
        }
    }

    @Override
    public User findById(Long id) {
        log.debug("Поиск пользователя по ID: {}", id);
        try {
            User user = em.find(User.class, id);
            if (user == null) {
                log.debug("Пользователь с ID={} не найден", id);
            } else {
                log.trace("Найден пользователь: ID={}, email={}", user.getId(), user.getEmail());
            }
            return user;
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя с ID={}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка при поиске пользователя", e);
        }
    }

    private UserResponseDto convertToDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .age(user.getAge())
                .createdAt(user.getCreatedAt())
                .build();
    }
}