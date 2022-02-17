package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.repositories.AccountRepository;
import org.lennardjones.investmanager.repositories.PurchaseRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PurchaseService {
    private final PurchaseRepository purchaseRepository;
    private final AccountRepository accountRepository;
    private  final LoggedUserManagementService loggedUserManagementService;

    public PurchaseService(PurchaseRepository purchaseRepository,
                           AccountRepository accountRepository,
                           LoggedUserManagementService loggedUserManagementService) {
        this.purchaseRepository = purchaseRepository;
        this.accountRepository = accountRepository;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    public List<Purchase> getListByOwnerId(Long userId) {
        var accountOptional = accountRepository.findById(userId);
        if (accountOptional.isPresent()) {
            return accountOptional.get().getPurchaseList();
        }
        return Collections.emptyList();
    }

    public List<Purchase> getListByOwnerIdContainingSubstring(Long id, String substring) {
        return purchaseRepository.findByOwner_IdAndNameContainingIgnoreCase(id, substring);
    }

    public String getNameById(Long id) {
        return purchaseRepository.getById(id).getName();
    }

    public void save(Purchase purchase) {
        var userId = loggedUserManagementService.getUserId();
        var ownerId = purchase.getOwner().getId();
        if (userId != null && userId.equals(ownerId)) {
            purchaseRepository.save(purchase);
        } else {
            throw new RuntimeException("Attempt to save purchase to someone else's account");
        }
    }

    public void deleteById(Long id) {
        var userId = loggedUserManagementService.getUserId();
        var ownerId = purchaseRepository.findById(id).orElseThrow().getOwner().getId();
        if (userId != null && userId.equals(ownerId)) {
            purchaseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Attempt to delete someone else's purchase");
        }
    }
}
