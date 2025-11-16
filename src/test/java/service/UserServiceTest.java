package service;

import dto.UserRequestDto;
import dto.UserResponseDto;
import entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private Validator validator;

    @Spy
    private UserMapper mapper = new UserMapper();

    private UserService service;

    private User user;
    private UserRequestDto dto;

    @BeforeEach
    void setUp() {
        service = new UserService(repository, mapper, validator);

        dto = UserRequestDto.builder()
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .created_at(LocalDate.now())
                .build();

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .createdAt(dto.created_at())
                .build();
    }

    @Test
    void createUser_validData_repositoryCalled() {
        when(validator.validate(dto)).thenReturn(Set.of());
        when(mapper.toEntity(dto)).thenReturn(user);

        service.createUser(dto);

        verify(validator).validate(dto);
        verify(mapper).toEntity(dto);
        verify(repository).create(user);
    }

    @Test
    void createUser_invalidData_exceptionThrown() {
        @SuppressWarnings("unchecked")
        ConstraintViolation<UserRequestDto> violation =
                (ConstraintViolation<UserRequestDto>) mock(ConstraintViolation.class);

        when(validator.validate(dto)).thenReturn(Set.of(violation));

        assertThrows(IllegalArgumentException.class, () -> service.createUser(dto));
        verify(repository, never()).create(any());
    }

    @Test
    void readAllUsers_returnsListFromRepo() {
        List<UserResponseDto> list = List.of(
                UserResponseDto.builder()
                        .id(1L)
                        .name("A")
                        .email("a@mail.com")
                        .age(20)
                        .createdAt(LocalDate.now())
                        .build()
        );

        when(repository.findAll()).thenReturn(list);

        List<UserResponseDto> result = service.readAllUsers();

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).name());
        verify(repository).findAll();
    }

    @Test
    void readUserById_existingUser_returnsDto() {
        when(repository.findById(1L)).thenReturn(user);

        UserResponseDto expected = UserResponseDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .createdAt(dto.created_at())
                .build();

        when(mapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = service.readUserById(1L);

        assertEquals(1L, actual.id());
        assertEquals("Test User", actual.name());
        verify(repository).findById(1L);
    }

    @Test
    void readUserById_userNotFound_exception() {
        when(repository.findById(10L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.readUserById(10L));
    }

    @Test
    void updateUser_validData_updatesUser() {
        UserRequestDto update = UserRequestDto.builder()
                .name("Updated")
                .email("u@mail.com")
                .age(30)
                .build();

        User updateEntity = User.builder()
                .name("Updated")
                .email("u@mail.com")
                .age(30)
                .createdAt(dto.created_at())
                .build();

        when(validator.validate(update)).thenReturn(Set.of());
        when(mapper.toEntity(update)).thenReturn(updateEntity);

        User updated = User.builder()
                .id(1L)
                .name("Updated")
                .email("u@mail.com")
                .age(30)
                .createdAt(dto.created_at())
                .build();

        when(repository.update(1L, updateEntity)).thenReturn(updated);

        UserResponseDto expected = UserResponseDto.builder()
                .id(1L)
                .name("Updated")
                .email("u@mail.com")
                .age(30)
                .createdAt(dto.created_at())
                .build();

        when(mapper.toDto(updated)).thenReturn(expected);

        UserResponseDto result = service.updateUser(1L, update);

        assertEquals("Updated", result.name());
        assertEquals("u@mail.com", result.email());
    }

    @Test
    void deleteUser_validId_repositoryCalled() {
        service.deleteUser(1L);
        verify(repository).delete(1L);
    }

    @Test
    void deleteUser_invalidId_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> service.deleteUser(0L));
        verify(repository, never()).delete(any());
    }
}
