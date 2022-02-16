package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Override
    @Modifying
    @Query("DELETE FROM Sale WHERE id = :id")
    void deleteById(Long id);
}