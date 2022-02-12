package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUsernameIgnoreCase(@NonNull String username);
}