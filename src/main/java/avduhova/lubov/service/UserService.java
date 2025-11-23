package avduhova.lubov.service;

import avduhova.lubov.dto.UserRequestDto;
import avduhova.lubov.dto.UserResponseDto;
import avduhova.lubov.entity.User;
import lombok.RequiredArgsConstructor;
import avduhova.lubov.mapper.UserMapper;
import avduhova.lubov.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserResponseDto> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public UserResponseDto findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));
    }

    @Transactional
    public UserResponseDto create(UserRequestDto dto) {
        User user = mapper.toEntity(dto);
        User saved = repository.save(user);
        return mapper.toDto(saved);
    }

    @Transactional
    public UserResponseDto update(Long id, UserRequestDto dto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Пользователь не найден"));
        mapper.updateEntity(user, dto);
        User saved = repository.save(user);
        return mapper.toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Пользователь не найден");
        }
        repository.deleteById(id);
    }
}