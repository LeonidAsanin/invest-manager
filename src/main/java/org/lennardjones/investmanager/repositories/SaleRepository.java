package org.lennardjones.investmanager.repositories;

import org.lennardjones.investmanager.entities.Sale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for creating repository for {@link org.lennardjones.investmanager.entities.Sale sale objects}
 * by Spring Data.
 *
 * @since 1.0
 * @author lennardjones
 */
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Override
    @Modifying
    @Query("delete from Sale where id = :id")
    void deleteById(Long id);

    List<Sale> findBySeller_Username(String username);

    List<Sale> findBySeller_Username(String username, Pageable pageable);

    List<Sale> findBySeller_UsernameAndNameContainingIgnoreCase(String username, String name, Pageable pageable);

}