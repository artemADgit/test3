// Сервисный класс для бизнес-логики работы с пользователями
package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Аннотация указывает, что это Spring-сервис (бизнес-логика)
@Service
public class UserService {

    // Внедрение зависимости репозитория через конструктор (рекомендуется)
    private final UserRepository userRepository;

    // Конструктор для внедрения зависимости
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Метод для сохранения нового пользователя
    public User createUser(String name, String email) {
        // Проверка на существование пользователя с таким email
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        // Создаём нового пользователя
        User user = new User(name, email);
        // Сохраняем в БД и возвращаем сохранённый объект
        return userRepository.save(user);
    }

    // Метод для получения пользователя по ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }
}
