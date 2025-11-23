package avduhova.lubov.contoller;

import avduhova.lubov.dto.UserRequestDto;
import avduhova.lubov.entity.User;
import avduhova.lubov.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        UserRequestDto request = new UserRequestDto(
                "Ivan",
                "ivan@example.com",
                25,
                LocalDate.now()
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("ivan@example.com"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    void getUserById_returnsExistingUser() throws Exception {
        User saved = repository.save(User.builder()
                .name("Anna")
                .email("anna@example.com")
                .age(30)
                .createdAt(LocalDate.now())
                .build());

        mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void updateUser_updatesFields() throws Exception {
        User saved = repository.save(User.builder()
                .name("Old")
                .email("old@example.com")
                .age(50)
                .createdAt(LocalDate.now())
                .build());

        UserRequestDto update = new UserRequestDto(
                "New",
                "new@example.com",
                45,
                LocalDate.now()
        );

        mockMvc.perform(put("/api/users/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.age").value(45));
    }

    @Test
    void deleteUser_removesRecord() throws Exception {
        User saved = repository.save(User.builder()
                .name("Delete")
                .email("delete@example.com")
                .age(40)
                .createdAt(LocalDate.now())
                .build());

        mockMvc.perform(delete("/api/users/" + saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @Test
    void getAll_returnsList() throws Exception {
        repository.save(User.builder()
                .name("First")
                .email("first@example.com")
                .age(20)
                .createdAt(LocalDate.now())
                .build());
        repository.save(User.builder()
                .name("Second")
                .email("second@example.com")
                .age(22)
                .createdAt(LocalDate.now())
                .build());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", org.hamcrest.Matchers.is(2)));
    }
}