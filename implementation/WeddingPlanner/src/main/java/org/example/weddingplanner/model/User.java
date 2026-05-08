package org.example.weddingplanner.model;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entity representing a system user (Organizer).
 * Mapped to the "users" table in the database.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     * We use UUID instead of standard Long/Integer for enhanced security.
     */
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * User's age.
     */
    private Integer age;

    /**
     * Unique username chosen by the user for platform access.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * User's email address, used for authentication and notifications.
     * Must be unique across the platform.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Hashed password for security compliance.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Default constructor required by JPA/Hibernate.
     * Automatically generates a secure, random UUID upon object creation.
     */
    public User() {
        this.id = UUID.randomUUID();
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}