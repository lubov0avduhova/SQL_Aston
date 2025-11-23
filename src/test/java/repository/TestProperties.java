package repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.Yaml;

public class TestProperties {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = TestProperties.class
                .getClassLoader()
                .getResourceAsStream("application-test.yml")) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlProps = yaml.load(input);
            Map<String, Object> postgresProps = (Map<String, Object>)
                    ((Map<String, Object>) yamlProps.get("testcontainers")).get("postgres");

            properties.putAll(postgresProps.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey(),
                            e -> e.getValue().toString()
                    )));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test properties", e);
        }
    }

    public static String getPostgresImage() {
        return properties.getProperty("image");
    }

    public static String getDatabaseName() {
        return properties.getProperty("database");
    }

    public static String getUsername() {
        return properties.getProperty("username");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }
}