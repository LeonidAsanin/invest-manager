package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}