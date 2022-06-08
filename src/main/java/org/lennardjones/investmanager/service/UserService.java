package org.lennardjones.investmanager.service;

import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for convenient working with {@link User user} objects.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameIgnoreCase(username);
    }

    public void registerNewUser(User user) {
        userRepository.save(user);
    }

    public void update(User user) {
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var optionalAccount = userRepository.findByUsername(username);
        return optionalAccount
                .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));
    }
}
