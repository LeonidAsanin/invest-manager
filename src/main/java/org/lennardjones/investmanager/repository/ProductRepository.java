package org.lennardjones.investmanager.repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface for describing {@link org.lennardjones.investmanager.model.Product product} repository methods.
 *
 * @since 1.1
 * @author lennardjones
 */
public interface ProductRepository {

    Optional<Double> getCurrentPriceByUserIdAndProductName(Long userId, String productName);

    void save(Long userId, String productName, double currentPrice);

    List<String> getAllNamesByUserId(Long userId);

    void removeByUserIdAndProductName(Long userId, String productName);
}
