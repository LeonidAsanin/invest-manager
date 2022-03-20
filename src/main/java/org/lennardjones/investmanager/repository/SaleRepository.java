package org.lennardjones.investmanager.repository;

import org.lennardjones.investmanager.entity.Sale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Interface for creating repository for {@link org.lennardjones.investmanager.entity.Sale sale objects}
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

    List<Sale> findBySeller_UsernameAndName(String username, String name);

    List<Sale> findBySeller_UsernameAndNameContainingIgnoreCase(String username, String substring);

    List<Sale> findBySeller_UsernameAndNameContainingIgnoreCase(String username, String substring, Pageable pageable);

}