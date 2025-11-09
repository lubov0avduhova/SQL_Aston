package dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record UserResponseDto(Long id,
                              String name,
                              String email,
                              Integer age,
                              LocalDate createdAt) {
}