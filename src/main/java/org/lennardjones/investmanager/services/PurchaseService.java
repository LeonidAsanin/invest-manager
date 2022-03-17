package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.User;
import org.lennardjones.investmanager.repositories.PurchaseRepository;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for convenient working with {@link org.lennardjones.investmanager.entities.Purchase Purchase} objects.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;

    public PurchaseService(PurchaseRepository purchaseRepository, ProductService productService) {
        this.purchaseRepository = purchaseRepository;
        this.productService = productService;
    }

    public List<Purchase> getListByUsername(String username) {
        return purchaseRepository.findByOwner_Username(username);
    }

    public List<Purchase> getListByUsername(String username, int page, SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = switch (sortType) {
            case NONE -> PageRequest.of(page, 10, sortDirection, "id");
            case NAME -> PageRequest.of(page, 10, sortDirection, "name", "dateTime", "id");
            case DATE -> PageRequest.of(page, 10, sortDirection, "dateTime", "name", "id");
        };
        return purchaseRepository.findByOwner_Username(username, pageRequest);
    }

    public List<Purchase> getListByUsernameContainingSubstring(String username, String substring) {
        return purchaseRepository.findByOwner_UsernameAndNameContainingIgnoreCase(username, substring);
    }

    public List<Purchase> getListByUsernameContainingSubstring(String username, String substring, int page,
                                                               SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = switch (sortType) {
            case NONE -> PageRequest.of(page, 10, sortDirection, "id");
            case NAME -> PageRequest.of(page, 10, sortDirection, "name", "dateTime", "id");
            case DATE -> PageRequest.of(page, 10, sortDirection, "dateTime", "name", "id");
        };
        return purchaseRepository.findByOwner_UsernameAndNameContainingIgnoreCase(username, substring, pageRequest);
    }

    public String getNameById(Long id) {
        return purchaseRepository.getById(id).getName();
    }

    public void save(Purchase purchase) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var ownerId = purchase.getOwner().getId();
        if (userId != null && userId.equals(ownerId)) {
            purchaseRepository.save(purchase);
            productService.setDateChanged();
        } else {
            throw new RuntimeException("Attempt to save purchase to someone else's account");
        }
    }

    public void deleteById(Long id) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var ownerId = purchaseRepository.findById(id).orElseThrow().getOwner().getId();
        if (userId != null && userId.equals(ownerId)) {
            purchaseRepository.deleteById(id);
            productService.setDateChanged();
        } else {
            throw new RuntimeException("Attempt to delete someone else's purchase");
        }
    }
}
