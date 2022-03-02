package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.entities.User;
import org.lennardjones.investmanager.repositories.UserRepository;
import org.lennardjones.investmanager.repositories.SaleRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Service for convenient working with {@link org.lennardjones.investmanager.entities.Sale Sale} objects.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    public SaleService(SaleRepository saleRepository,
                       UserRepository userRepository,
                       ProductService productService) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    public List<Sale> getListByUsername(String username) {
        var accountOptional = userRepository.findByUsername(username);
        if (accountOptional.isPresent()) {
            return accountOptional.get().getSaleList();
        }
        return Collections.emptyList();
    }

    public List<Sale> getListByUsernameContainingSubstring(String username, String substring) {
        return saleRepository.findBySeller_UsernameAndNameContainingIgnoreCase(username, substring);
    }

    public void save(Sale sale) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var sellerId = sale.getSeller().getId();
        if (userId != null && userId.equals(sellerId)) {
            saleRepository.save(sale);
            productService.setDateChanged();
        } else {
            throw new RuntimeException("Attempt to save sale to someone else's account");
        }
    }

    public void deleteById(Long id) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var sellerId = saleRepository.findById(id).orElseThrow().getSeller().getId();
        if (userId != null && userId.equals(sellerId)) {
            saleRepository.deleteById(id);
            productService.setDateChanged();
        } else {
            throw new RuntimeException("Attempt to delete someone else's sale");
        }
    }
}
