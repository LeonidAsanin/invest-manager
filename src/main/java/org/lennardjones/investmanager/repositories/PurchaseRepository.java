package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for creating repository for {@link org.lennardjones.investmanager.entities.Purchase purchase objects}
 * by Spring Data.
 *
 * @since 1.0
 * @author lennardjones
 */
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Override
    @Modifying
    @Query("delete from Purchase where id = :id")
    void deleteById(Long id);

    List<Purchase> findByOwner_UsernameAndNameContainingIgnoreCase(String username, String name);

}