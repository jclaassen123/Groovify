package com.groovify.jpa.repo;

import com.groovify.jpa.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByName(String name); // Useful for fetching a user by username
}
