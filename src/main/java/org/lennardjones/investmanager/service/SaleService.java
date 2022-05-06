package org.lennardjones.investmanager.service;

import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.repository.SaleRepository;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for convenient working with {@link org.lennardjones.investmanager.entity.Sale Sale} objects.
 *
 * @since 1.0
 * @author lennardjones
 */
@Service
public class SaleService {
    private final SaleRepository saleRepository;
    private final ProductService productService;
    private final PurchaseService purchaseService;

    public SaleService(SaleRepository saleRepository, ProductService productService, PurchaseService purchaseService) {
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.purchaseService = purchaseService;
    }

    public List<Sale> getListByUsername(String username) {
        return saleRepository.findBySeller_Username(username);
    }

    public List<Sale> getListByUsername(String username, int page, SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = PurchaseSaleUtil.createPageRequestByParameters(page, sortType, sortDirection);
        return saleRepository.findBySeller_Username(username, pageRequest);
    }

    public List<Sale> getListByUsernameAndProductName(String username, String productName) {
        return saleRepository.findBySeller_UsernameAndName(username, productName);
    }

    public List<Sale> getListByUsernameContainingSubstring(String username, String substring) {
        return saleRepository.findBySeller_UsernameAndNameContainingIgnoreCase(username, substring);
    }

    public List<Sale> getListByUsernameContainingSubstring(String username, String substring, int page,
                                                           SortType sortType, Sort.Direction sortDirection) {
        var pageRequest = PurchaseSaleUtil.createPageRequestByParameters(page, sortType, sortDirection);
        return saleRepository.findBySeller_UsernameAndNameContainingIgnoreCase(username, substring, pageRequest);
    }

    public void save(Sale sale) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var sellerId = sale.getSeller().getId();
        if (userId != null && userId.equals(sellerId)) {
            saleRepository.save(sale);
            productService.setDataChanged();
        } else {
            throw new RuntimeException("Attempt to save sale " + sale +
                                       " to someone else's account with id " + sellerId);
        }
    }

    public void updateProfitsByName(String productName) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var purchaseList = purchaseService.getListByUsernameAndProductName(user.getUsername(), productName);
        var saleList = getListByUsernameAndProductName(user.getUsername(), productName);
        saleList = PurchaseSaleUtil.calculateProfitsFromSales(purchaseList, saleList, productName);
        for (var sale : saleList) {
            save(sale);
        }
    }

    public void deleteById(Long id) {
        var user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var userId = user.getId();
        var sellerId = saleRepository.findById(id).orElseThrow().getSeller().getId();
        if (userId != null && userId.equals(sellerId)) {
            saleRepository.deleteById(id);
            productService.setDataChanged();
        } else {
            throw new RuntimeException("Attempt to delete sale with id " + id +
                                       "which belongs to someone else's account with id " + sellerId);
        }
    }

    public void updateTagsByUsernameAndProductName(String username, String productName, String tag) {
        var saleList = getListByUsernameAndProductName(username, productName);
        for (var sale : saleList) {
            sale.setTag(tag);
            save(sale);
        }
    }

    public Optional<Sale> getAnyByUsernameAndProductName(String username, String name) {
        return saleRepository.findBySeller_UsernameAndName(username, name)
                .stream()
                .findAny();
    }
}
