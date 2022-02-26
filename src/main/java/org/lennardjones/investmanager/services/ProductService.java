package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for calculating and interacting with products that user currently have.
 *
 * @since 1.1
 * @author lennardjones
 */
@Service
@SessionScope
public class ProductService {
    private final LoggedUserManagementService loggedUserManagementService;
    private final PurchaseService purchaseService;
    private final SaleService saleService;
    private Set<Product> productList;
    private boolean isDateChanged;

    public ProductService(LoggedUserManagementService loggedUserManagementService,
                          PurchaseService purchaseService,
                          SaleService saleService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.purchaseService = purchaseService;
        this.saleService = saleService;
        isDateChanged = false;
    }

    public void setDateChanged() {
        isDateChanged = true;
    }

    public void setCurrentPriceByName(String productName, double currentPrice) {
        var optionalProduct = productList.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst();

        optionalProduct.ifPresent(product -> product.setCurrentPrice(currentPrice));
    }

    //TODO: implement "calculateProducts" method
    private void calculateProducts(List<Purchase> purchaseList, List<Sale> saleList) {
        productList = new HashSet<>();

        var uniquePurchaseNames = new HashSet<String>();
        for (var purchase : purchaseList) {
            uniquePurchaseNames.add(purchase.getName());
        }

        for (var productName : uniquePurchaseNames) {
            var purchases = purchaseList.stream()
                    .filter(p -> p.getName().equals(productName))
                    .collect(Collectors.toCollection(LinkedList::new));
            var sales = saleList.stream()
                    .filter(s -> s.getName().equals(productName))
                    .collect(Collectors.toCollection(LinkedList::new));
        }
    }

    public Set<Product> getAll() {
        if (isDateChanged) {
            var userId = loggedUserManagementService.getUserId();
            var purchaseList = purchaseService.getListByOwnerId(userId);
            var saleList = saleService.getListBySellerId(userId);
            calculateProducts(purchaseList, saleList);
            isDateChanged = false;
        }
        return productList;
    }
}
