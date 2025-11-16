package mapper;

import dto.UserRequestDto;
import dto.UserResponseDto;
import entity.User;
import view.ConsoleView;

public class UserMapper {
    public User toEntity(UserRequestDto dto) {
        return User.builder()
                .name(dto.name())
                .age(dto.age())
                .email(dto.email())
                .createdAt(dto.created_at())
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

    public UserRequestDto toDto(ConsoleView view) {
        return UserRequestDto.builder()
                .name(view.askUserName())
                .age(view.askUserAge())
                .email(view.askUserEmail())
                .created_at(view.askUserCreateAt())
                .build();
    }
}
