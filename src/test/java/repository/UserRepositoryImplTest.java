package repository;

import dto.UserResponseDto;
import entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest extends UserRepositoryContainer {

    private EntityManager em;
    private UserRepository userRepository;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String UPDATED_NAME = "Updated Name";
    private static final String UPDATED_EMAIL = "updated@example.com";

    @BeforeEach
    void setUp() {
        em = emf.createEntityManager();
        userRepository = new UserRepositoryImpl(em);

        em.getTransaction().begin();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }

    @Test
    void create_ShouldSaveUser() {
        User user = createTestUser();

        userRepository.create(user);

        User found = em.find(User.class, user.getId());
        assertNotNull(found);
        assertEquals(user.getEmail(), found.getEmail());
        assertEquals(user.getName(), found.getName());
    }

    @Test
    void findAll_ShouldReturnAllSavedUsers() {
        User user1 = createTestUser();
        User user2 = createTestUser("another@example.com", "Another User");

        em.getTransaction().begin();
        em.persist(user1);
        em.persist(user2);
        em.getTransaction().commit();

        List<UserResponseDto> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.email().equals(user1.getEmail())));
        assertTrue(users.stream().anyMatch(u -> u.email().equals(user2.getEmail())));
    }

    @Test
    void update_ShouldChangeUserData() {
        User user = createTestUser();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        User updated = new User();
        updated.setName(UPDATED_NAME);
        updated.setEmail(UPDATED_EMAIL);
        updated.setAge(30);

        User result = userRepository.update(user.getId(), updated);

        assertNotNull(result);
        assertEquals(UPDATED_NAME, result.getName());
        assertEquals(UPDATED_EMAIL, result.getEmail());
        assertEquals(30, result.getAge());

        User found = em.find(User.class, user.getId());
        assertEquals(UPDATED_NAME, found.getName());
        assertEquals(UPDATED_EMAIL, found.getEmail());
    }

    @Test
    void update_WhenUserDoesNotExist_ShouldThrow() {
        User updatedUser = new User();
        updatedUser.setName(UPDATED_NAME);
        updatedUser.setEmail(UPDATED_EMAIL);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> userRepository.update(999L, updatedUser)
        );

        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertEquals("Пользователь с id=999 не найден", ex.getCause().getMessage());
    }

    @Test
    void delete_ShouldRemoveUser() {
        User user = createTestUser();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        Long id = user.getId();
        assertNotNull(em.find(User.class, id));

        userRepository.delete(id);

        assertNull(em.find(User.class, id));
    }

    @Test
    void delete_WhenUserDoesNotExist_ShouldNotFail() {
        assertDoesNotThrow(() -> userRepository.delete(999L));
    }

    private User createTestUser() {
        return createTestUser(TEST_EMAIL, "Test User");
    }

    private User createTestUser(String email, String name) {
        return User.builder()
                .name(name)
                .email(email)
                .age(25)
                .createdAt(LocalDate.now())
                .build();
    }
}
