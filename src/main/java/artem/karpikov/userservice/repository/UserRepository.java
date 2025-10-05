// Репозиторий для работы с пользователями через Spring Data JPA
package com.example.userservice.repository;

import com.example.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Аннотация указывает, что это Spring-компонент репозитория
@Repository
// JpaRepository предоставляет стандартные CRUD-операции
// <User, Long> — тип сущности и тип её первичного ключа
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data автоматически сгенерирует запрос по имени метода
    // Ищет пользователя по email
    User findByEmail(String email);
}
