package org.lennardjones.investmanager.repository;

import org.lennardjones.investmanager.entity.Purchase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for creating repository for {@link org.lennardjones.investmanager.entity.Purchase purchase objects}
 * by Spring Data.
 *
 * @since 1.0
 * @author lennardjones
 */
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Override // for force deleting
    @Modifying
    @Query("delete from Purchase where id = :id")
    void deleteById(Long id);

    List<Purchase> findByOwner_Username(String username);

    List<Purchase> findByOwner_Username(String username, Pageable pageable);

    List<Purchase> findByOwner_UsernameAndName(String username, String name);

    List<Purchase> findByOwner_UsernameAndNameContainingIgnoreCase(String username, String substring);

    List<Purchase> findByOwner_UsernameAndNameContainingIgnoreCase(String username, String substring, Pageable pageable);

}