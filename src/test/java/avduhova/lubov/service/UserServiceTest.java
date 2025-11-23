package avduhova.lubov.service;

import avduhova.lubov.dto.UserRequestDto;
import avduhova.lubov.dto.UserResponseDto;
import avduhova.lubov.entity.User;
import avduhova.lubov.mapper.UserMapper;
import avduhova.lubov.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequestDto userRequestDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        userRequestDto = new UserRequestDto(
                "Test User",
                "test@example.com",
                25,
                LocalDate.now()
        );

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setAge(25);
        user.setCreatedAt(LocalDate.now());

        userResponseDto = new UserResponseDto(
                1L,
                "Test User",
                "test@example.com",
                25,
                LocalDate.now()
        );
    }

    @Test
    void findAll_shouldReturnListOfUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userResponseDto);

        // Act
        List<UserResponseDto> result = userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userResponseDto, result.get(0));
        verify(userRepository).findAll();
        verify(userMapper).toDto(user);
    }

    @Test
    void findById_shouldReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void findById_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.findById(1L));
        verify(userRepository).findById(1L);
        verifyNoInteractions(userMapper);
    }

    @Test
    void create_shouldSaveAndReturnCreatedUser() {
        // Arrange
        when(userMapper.toEntity(userRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.create(userRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userMapper).toEntity(userRequestDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void update_shouldUpdateAndReturnUser_whenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        // Act
        UserResponseDto result = userService.update(1L, userRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals(userResponseDto, result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(userMapper).updateEntity(user, userRequestDto);
        verify(userMapper).toDto(user);
    }

    @Test
    void update_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class,
                () -> userService.update(1L, userRequestDto));
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void delete_shouldDeleteUser_whenUserExists() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.delete(1L);

        // Assert
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenUserNotFound() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class,
                () -> userService.delete(1L));
        verify(userRepository).existsById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}