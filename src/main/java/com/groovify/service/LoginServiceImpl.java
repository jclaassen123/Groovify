package com.groovify.service;

import org.springframework.stereotype.Service;
import com.groovify.jpa.model.Client;
import com.groovify.jpa.repo.LoginRepository;
import com.groovify.util.PasswordUtil;
import java.util.List;

/**
 * Implementation of {@link LoginService} that provides user authentication functionality.
 * <p>
 * This service is responsible for validating user credentials against the database.
 * It uses {@link LoginRepository} to perform queries on the {@link Client} entity.
 * </p>
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final LoginRepository loginRepo;

    public LoginServiceImpl(LoginRepository loginRepo) {
        this.loginRepo = loginRepo;
    }

    @Override
    public boolean validateClient(String username, String password) {
        List<Client> users = loginRepo.findByNameIgnoreCase(username);
        if (users.isEmpty()) {
            return false;
        }

        Client user = users.get(0);
        return PasswordUtil.verifyPassword(password, user.getPasswordSalt(), user.getPassword());
    }
}
