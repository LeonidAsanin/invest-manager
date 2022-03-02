package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface for creating repository for {@link User user objects}
 * by Spring Data.
 *
 * @since 1.0
 * @author lennardjones
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);
}