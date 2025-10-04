package com.groovify.service;

import org.springframework.stereotype.Service;
import com.groovify.jpa.model.Users;             // Users entity
import com.groovify.jpa.repo.LoginRepository;
import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepo;

    public LoginServiceImpl(LoginRepository loginRepo) {
        this.loginRepo = loginRepo;
    }

    @Override
    public boolean validateUser(String username, String password) {
        // Find users matching the username (case-insensitive)
        List<Users> users = loginRepo.findByNameIgnoreCase(username);

        if (users.isEmpty()) {
            return false; // username not found
        }

        Users u = users.get(0); // we expect only one user per name
        return u.getPassword().equals(password); // compare password
    }
}

