package repository;

import dto.UserResponseDto;
import entity.User;

import java.util.List;

public interface UserRepository {
    void create(User user);

    List<UserResponseDto> findAll();

    User update(Long id, User oldUser);

    void delete(Long id);

    User findById(Long id);
}
