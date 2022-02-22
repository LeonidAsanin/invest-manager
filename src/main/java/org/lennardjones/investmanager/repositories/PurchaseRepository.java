package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    @Override
    @Modifying
    @Query("delete from Purchase where id = :id")
    void deleteById(Long id);

    List<Purchase> findByOwner_IdAndNameContainingIgnoreCase(Long id, String name);
}