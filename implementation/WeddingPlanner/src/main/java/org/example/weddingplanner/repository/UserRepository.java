package org.example.weddingplanner.repository;

import org.example.weddingplanner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);

    // Retrieves a user entity based on their email address for login verification
    User findByEmail(String email);
}