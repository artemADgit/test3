// Юнит-тесты для UserService с использованием Mockito
package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Расширение Mockito для автоматической инициализации моков
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    // Создаём мок-объект репозитория (подмена реальной зависимости)
    @Mock
    private UserRepository userRepository;

    // Внедряем мок в тестируемый сервис
    @InjectMocks
    private UserService userService;

    // Переменные для тестовых данных
    private User testUser;
    private String testName = "Иван";
    private String testEmail = "ivan@example.com";

    // Выполняется перед каждым тестом: инициализация тестовых данных
    @BeforeEach
    void setUp() {
        testUser = new User(testName, testEmail);
        testUser.setId(1L);
    }

    // Тест успешного создания пользователя
    @Test
    void createUser_WithValidData_ShouldSaveUser() {
        // Настройка поведения мока: при вызове findByEmail возвращаем null (пользователь не существует)
        when(userRepository.findByEmail(testEmail)).thenReturn(null);
        // При вызове save возвращаем сохранённого пользователя с ID
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Вызываем тестируемый метод
        User result = userService.createUser(testName, testEmail);

        // Проверяем, что результат не null
        assertNotNull(result);
        // Проверяем, что имя и email совпадают
        assertEquals(testName, result.getName());
        assertEquals(testEmail, result.getEmail());
        // Проверяем, что методы мока были вызваны 1 раз
        verify(userRepository, times(1)).findByEmail(testEmail);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Тест попытки создания пользователя с уже существующим email
    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Настройка мока: пользователь с таким email уже существует
        when(userRepository.findByEmail(testEmail)).thenReturn(testUser);

        // Ожидаем исключение при вызове метода
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testName, testEmail)
        );

        // Проверяем сообщение исключения
        assertEquals("Пользователь с таким email уже существует", exception.getMessage());
        // Проверяем, что save не был вызван
        verify(userRepository, never()).save(any(User.class));
    }

    // Тест получения пользователя по ID
    @Test
    void getUserById_WithExistingId_ShouldReturnUser() {
        // Настройка мока: findById возвращает Optional с пользователем
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(testUser));

        // Вызываем метод
        User result = userService.getUserById(1L);

        // Проверяем результат
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
    }

    // Тест получения несуществующего пользователя
    @Test
    void getUserById_WithNonExistingId_ShouldThrowException() {
        // Настройка мока: findById возвращает пустой Optional
        when(userRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        // Ожидаем исключение
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}
