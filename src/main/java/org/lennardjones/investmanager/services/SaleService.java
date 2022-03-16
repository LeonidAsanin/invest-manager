package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.entities.User;
import org.lennardjones.investmanager.repositories.UserRepository;
import org.lennardjones.investmanager.repositories.SaleRepository;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    private final ProductService productService;

    public SaleService(SaleRepository saleRepository, ProductService productService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
    }

    public List<Sale> getListByUsername(String username) {
        return saleRepository.findBySeller_Username(username);
    }

    public List<Sale> getListByUsername(String username, int page, SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = switch (sortType) {
            case NONE -> PageRequest.of(page, 10, sortDirection, "id");
            case NAME -> PageRequest.of(page, 10, sortDirection, "name", "dateTime", "id");
            case DATE -> PageRequest.of(page, 10, sortDirection, "dateTime", "name", "id");
        };
        return saleRepository.findBySeller_Username(username, pageRequest);
    }

    public List<Sale> getListByUsernameContainingSubstring(String username, String substring, int page,
                                                           SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = switch (sortType) {
            case NONE -> PageRequest.of(page, 10, sortDirection, "id");
            case NAME -> PageRequest.of(page, 10, sortDirection, "name", "dateTime", "id");
            case DATE -> PageRequest.of(page, 10, sortDirection, "dateTime", "name", "id");
        };
        return saleRepository.findBySeller_UsernameAndNameContainingIgnoreCase(username, substring, pageRequest);
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
