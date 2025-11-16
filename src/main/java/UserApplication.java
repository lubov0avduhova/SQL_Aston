import controller.UserConsoleController;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserApplication {
    public static void main(String[] args) {
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("user-unit");
             EntityManager manager = factory.createEntityManager()) {
            UserConsoleController controller = new UserConsoleController(manager);
            controller.start();
        } catch (Exception e) {
            log.error("Ошибка при освобождении ресурсов", e);
        }
        log.info("Приложение завершило работу");
    }
}
