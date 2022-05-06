package org.lennardjones.investmanager.service;

import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.repository.PurchaseRepository;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for convenient working with {@link org.lennardjones.investmanager.entity.Purchase Purchase} objects.
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
        var pageRequest = PurchaseSaleUtil.createPageRequestByParameters(page, sortType, sortDirection);
        return purchaseRepository.findByOwner_Username(username, pageRequest);
    }

    public List<Purchase> getListByUsernameAndProductName(String username, String productName) {
        return purchaseRepository.findByOwner_UsernameAndName(username, productName);
    }

    public List<Purchase> getListByUsernameContainingSubstring(String username, String substring) {
        return purchaseRepository.findByOwner_UsernameAndNameContainingIgnoreCase(username, substring);
    }

    public List<Purchase> getListByUsernameContainingSubstring(String username, String substring, int page,
                                                               SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = PurchaseSaleUtil.createPageRequestByParameters(page, sortType, sortDirection);
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
            productService.setDataChanged();
        } else {
            throw new RuntimeException("Attempt to save purchase " + purchase +
                                       " to someone else's account with id " + ownerId);
        }
    }

    public void deleteById(Long id) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var ownerId = purchaseRepository.findById(id).orElseThrow().getOwner().getId();
        if (userId != null && userId.equals(ownerId)) {
            purchaseRepository.deleteById(id);
            productService.setDataChanged();
        } else {
            throw new RuntimeException("Attempt to delete purchase with id " + id +
                                       "which belongs to someone else's account with id " + ownerId);
        }
    }

    public void updateTagsByUsernameAndProductName(String username, String productName, String tag) {
        var purchaseList = getListByUsernameAndProductName(username, productName);
        for (var purchase : purchaseList) {
            purchase.setTag(tag);
            save(purchase);
        }
    }

    public Optional<Purchase> getAnyByUsernameAndProductName(String username, String productName) {
        return purchaseRepository.findByOwner_UsernameAndName(username, productName)
                .stream()
                .findAny();
    }
}
