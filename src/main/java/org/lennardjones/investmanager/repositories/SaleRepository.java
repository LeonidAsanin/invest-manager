package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Override
    @Modifying
    @Query("delete from Sale where id = :id")
    void deleteById(Long id);

    List<Sale> findBySeller_IdAndNameContainingIgnoreCase(Long id, String name);
}