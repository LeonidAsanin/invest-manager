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

    public PurchaseService(PurchaseRepository purchaseRepository, AccountRepository accountRepository) {
        this.purchaseRepository = purchaseRepository;
        this.accountRepository = accountRepository;
    }

    public List<Purchase> getListByOwnerId(Long userId) {
        var accountOptional = accountRepository.findById(userId);
        if (accountOptional.isPresent()) {
            return accountOptional.get().getPurchaseList();
        }
        return Collections.emptyList();
    }

    public void save(Purchase purchase) {
        purchaseRepository.save(purchase);
    }

    public void deleteById(Long id) {
        purchaseRepository.deleteById(id);
    }
}
