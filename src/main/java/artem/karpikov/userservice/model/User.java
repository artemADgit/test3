// Модель пользователя: содержит основные поля пользователя
package com.example.userservice.model;

import jakarta.persistence.*;

// Аннотация указывает, что этот класс является JPA-сущностью
@Entity
// Указывает имя таблицы в базе данных
@Table(name = "users")
public class User {

    // Первичный ключ, генерируемый автоматически
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Имя пользователя, не может быть пустым
    @Column(nullable = false)
    private String name;

    // Email пользователя, должен быть уникальным и не пустым
    @Column(nullable = false, unique = true)
    private String email;

    // Конструктор по умолчанию (обязателен для JPA)
    public User() {}

    // Конструктор с параметрами для удобства создания объектов
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Геттеры и сеттеры

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
