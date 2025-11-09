package dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserRequestDto(
        @NotBlank(message = "Имя не должно быть пустым") String name,

        @Email(message = "Email должен содержать @") String email,

        @NotNull @Min(value = 1, message = "Возраст не должен быть меньше 1 года")
        @Max(value = 99, message = "Возраст не должен быть больше 99 лет") Integer age,

        @NotNull(message = "Дата не должна быть пустой")
        @PastOrPresent(message = "Дата не должна быть в будущем") LocalDate created_at
) {
}
