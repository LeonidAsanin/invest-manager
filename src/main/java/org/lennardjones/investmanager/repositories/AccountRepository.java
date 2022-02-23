package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

/**
 * Interface for creating repository for {@link org.lennardjones.investmanager.entities.Account account objects}
 * by Spring Data.
 *
 * @since 1.0
 * @author lennardjones
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUsernameIgnoreCase(String username);
}