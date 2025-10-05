// Интеграционные тесты для UserRepository с использованием Testcontainers
package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

// Аннотация указывает, что это JPA-тест (загружает только слой репозиториев)
@DataJpaTest
// Отключаем использование встроенной БД (например, H2), чтобы использовать Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// Включаем поддержку Testcontainers в JUnit
@Testcontainers
public class UserRepositoryIT {

    // Создаём контейнер PostgreSQL с помощью Testcontainers
    // Контейнер будет запущен один раз на весь класс тестов
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    // Динамически подставляем URL подключения к БД из контейнера
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Передаём Spring Boot URL подключения, полученный от контейнера
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    // Внедряем тестируемый репозиторий
    @Autowired
    private UserRepository userRepository;

    // Очищаем БД перед каждым тестом для изоляции
    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    // Тест сохранения и поиска пользователя по email
    @Test
    void saveUser_ThenFindByEmail_ShouldReturnUser() {
        // Создаём тестового пользователя
        User user = new User("Анна", "anna@example.com");

        // Сохраняем в БД через репозиторий
        User savedUser = userRepository.save(user);

        // Проверяем, что ID присвоен (значит, запись действительно в БД)
        assertNotNull(savedUser.getId());

        // Ищем пользователя по email
        User foundUser = userRepository.findByEmail("anna@example.com");

        // Проверяем, что пользователь найден и данные совпадают
        assertNotNull(foundUser);
        assertEquals("Анна", foundUser.getName());
        assertEquals("anna@example.com", foundUser.getEmail());
        assertEquals(savedUser.getId(), foundUser.getId());
    }

    // Тест поиска несуществующего пользователя
    @Test
    void findByEmail_WithNonExistingEmail_ShouldReturnNull() {
        // Ищем несуществующий email
        User found = userRepository.findByEmail("notfound@example.com");

        // Ожидаем null
        assertNull(found);
    }

    // Тест уникальности email (попытка сохранить дубликат)
    @Test
    void saveUser_WithDuplicateEmail_ShouldFail() {
        // Сохраняем первого пользователя
        userRepository.save(new User("Петр", "petr@example.com"));

        // Пытаемся сохранить второго с тем же email
        User duplicate = new User("Петр2", "petr@example.com");

        // Ожидаем исключение из-за ограничения UNIQUE в БД
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicate);
            // Принудительно сбрасываем контекст, чтобы триггернуть ошибку БД
            userRepository.flush();
        });
    }
}
