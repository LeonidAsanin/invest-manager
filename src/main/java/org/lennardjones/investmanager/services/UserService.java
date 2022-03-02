package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.User;
import org.lennardjones.investmanager.repositories.UserRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * Service for convenient working with {@link User user} objects.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Long getUserIdByUsername(String username) {
        var account = new User();
        account.setUsername(username);
        account = userRepository.findOne(Example.of(account)).orElseThrow();
        return account.getId();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public boolean exists(User user) {
        return userRepository.exists(Example.of(user));
    }

    public void registerNewUser(User user) {
        userRepository.save(user);
    }
}
