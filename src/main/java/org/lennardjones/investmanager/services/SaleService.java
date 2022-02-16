package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.repositories.AccountRepository;
import org.lennardjones.investmanager.repositories.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final AccountRepository accountRepository;
    private final LoggedUserManagementService loggedUserManagementService;

    public SaleService(SaleRepository saleRepository,
                       AccountRepository accountRepository,
                       LoggedUserManagementService loggedUserManagementService) {
        this.saleRepository = saleRepository;
        this.accountRepository = accountRepository;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    public List<Sale> getListBySellerId(Long userId) {
        var accountOptional = accountRepository.findById(userId);
        if (accountOptional.isPresent()) {
            return accountOptional.get().getSaleList();
        }
        return Collections.emptyList();
    }

    public void save(Sale sale) {
        var userId = loggedUserManagementService.getUserId();
        var sellerId = sale.getSeller().getId();
        if (userId != null && userId.equals(sellerId)) {
            saleRepository.save(sale);
        } else {
            throw new RuntimeException("Attempt to save sale to someone else's account");
        }
    }

    public void deleteById(Long id) {
        var userId = loggedUserManagementService.getUserId();
        var sellerId = saleRepository.findById(id).orElseThrow().getSeller().getId();
        if (userId != null && userId.equals(sellerId)) {
            saleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Attempt to delete someone else's sale");
        }
    }
}
