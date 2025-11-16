package repository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

@Testcontainers
class UserRepositoryContainer {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    protected static EntityManagerFactory emf;

    @BeforeAll
    static void initEntityManager() {
        POSTGRES.start();

        emf = Persistence.createEntityManagerFactory(
                "test-container",
                Map.of(
                        "hibernate.connection.url", POSTGRES.getJdbcUrl(),
                        "hibernate.connection.username", POSTGRES.getUsername(),
                        "hibernate.connection.password", POSTGRES.getPassword(),
                        "hibernate.hbm2ddl.auto", "update"
                )
        );
    }
}

