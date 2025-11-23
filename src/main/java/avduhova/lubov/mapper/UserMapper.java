package avduhova.lubov.mapper;

import avduhova.lubov.dto.UserRequestDto;
import avduhova.lubov.dto.UserResponseDto;
import avduhova.lubov.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRequestDto dto) {
        return User.builder()
                .name(dto.name())
                .age(dto.age())
                .email(dto.email())
                .createdAt(dto.createdAt())
                .build();
    }

    public UserResponseDto toDto(User entity) {
        return UserResponseDto.builder()
                .id(entity.getId())
                .age(entity.getAge())
                .email(entity.getEmail())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntity(User user, UserRequestDto dto) {
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setAge(dto.age());
        user.setCreatedAt(dto.createdAt());
    }
}
