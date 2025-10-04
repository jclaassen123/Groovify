package com.groovify.jpa.repo;

import java.util.List;

import com.groovify.jpa.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Users, Integer> {
    // JPA throws an exception if we attempt to return a single object that doesn't exist, so return a list
    // even though we only expect either an empty list or a single element.
    List<Users> findByNameIgnoreCase(String name);
}
