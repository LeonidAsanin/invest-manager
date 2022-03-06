package org.lennardjones.investmanager.services;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.entities.User;
import org.lennardjones.investmanager.model.Product;
import org.lennardjones.investmanager.repositories.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for calculating and interacting with products that user currently have.
 *
 * @author lennardjones
 * @since 1.1
 */
@Service
@SessionScope
public class ProductService {
    private final PurchaseService purchaseService;
    private final SaleService saleService;
    private final ProductRepository productRepository;

    private Set<Product> productSet;
    private boolean isDateChanged;

    public ProductService(PurchaseService purchaseService,
                          SaleService saleService,
                          ProductRepository productRepository) {
        this.purchaseService = purchaseService;
        this.saleService = saleService;
        this.productRepository = productRepository;

        isDateChanged = true;
    }

    public void setDateChanged() {
        isDateChanged = true;
    }

    public void calculateBenefits(Long userId, String productName, double currentPrice) {
        /* Searching for product by name */
        var optionalProduct = productSet.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst();

        /* Updating current price and calculating current benefits if product exists */
        if (optionalProduct.isPresent()) {
            var product = optionalProduct.get();

            product.setCurrentPrice(currentPrice);
            productRepository.save(userId, productName, currentPrice);

            var absoluteBenefit = (currentPrice - product.getAveragePrice()) * product.getAmount();
            product.setAbsoluteProfit(absoluteBenefit);

            var relativePrice = (currentPrice / product.getAveragePrice() - 1) * 100;
            product.setRelativeProfit(relativePrice);

            productSet.add(product);
        }
    }

    private void calculateProducts(Long userId, List<Purchase> purchaseList, List<Sale> saleList) {
        productSet = new HashSet<>();

        /* Defining unique names of the purchases */
        var uniquePurchaseNames = new HashSet<String>();
        for (var purchase : purchaseList) {
            uniquePurchaseNames.add(purchase.getName());
        }

        /* Removing unnecessary entries from "productNameCurrentPriceMap" map */
        for (var productName : productRepository.getAllNamesByUserId(userId)) {
            if (!uniquePurchaseNames.contains(productName)) {
                productRepository.removeByUserIdAndProductName(userId, productName);
            }
        }

        /* Calculating current products for all unique names */
        for (var productName : uniquePurchaseNames) {

            /* Defining purchase and sale stacks of the product by name and sorting them by date */
            var purchaseStack = purchaseList.stream()
                    .filter(p -> p.getName().equals(productName))
                    .sorted(Comparator.comparing(Purchase::getDateTime))
                    .collect(Collectors.toCollection(LinkedList::new));
            var saleStack = saleList.stream()
                    .filter(s -> s.getName().equals(productName))
                    .sorted(Comparator.comparing(Sale::getDateTime))
                    .collect(Collectors.toCollection(LinkedList::new));

            /* Calculating resulting purchaseStack */
            while (!saleStack.isEmpty()) {
                var purchase = purchaseStack.removeFirst();

                var sale = saleStack.removeFirst();

                var purchaseAmount = purchase.getAmount();
                var saleAmount = sale.getAmount();

                var remainingProductAmount = purchaseAmount - saleAmount;

                if (remainingProductAmount > 0) {
                    purchase.setAmount(remainingProductAmount);
                    purchaseStack.addFirst(purchase);
                } else if (remainingProductAmount < 0) {
                    sale.setAmount(-remainingProductAmount);
                    saleStack.addFirst(sale);
                }
            }

            /* Calculating data for the product fields */
            var productAmount = purchaseStack.stream()
                    .mapToInt(Purchase::getAmount)
                    .sum();
            if (productAmount == 0) continue; // If there is no product with given name, then go to the next iteration
            var averagePriceConsideringCommission = purchaseStack.stream()
                    .mapToDouble(p -> p.getPrice() + p.getCommission())
                    .average()
                    .orElse(0);

            /* Filling in fields of the product */
            var product = new Product();
            product.setName(productName);
            product.setAmount(productAmount);
            product.setAveragePrice(averagePriceConsideringCommission);

            /* Setting current price and calculating current benefits if corresponding data exists */
            var optionalCurrentPrice = productRepository
                    .getCurrentPriceByUserIdAndProductName(userId, productName);
            if (optionalCurrentPrice.isPresent()) {
                double currentPrice = optionalCurrentPrice.get();

                product.setCurrentPrice(currentPrice);

                var absoluteBenefit = (currentPrice - averagePriceConsideringCommission) * productAmount;
                product.setAbsoluteProfit(absoluteBenefit);

                var relativePrice = (currentPrice / averagePriceConsideringCommission - 1) * 100;
                product.setRelativeProfit(relativePrice);
            }

            /* Adding product to the product set */
            productSet.add(product);
        }

        /* Sorting current product set by name */
        productSet = productSet.stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Product> getAllByUser(User user) {
        if (isDateChanged) {
            var purchaseList = purchaseService.getListByUsername(user.getUsername());
            var saleList = saleService.getListByUsername(user.getUsername());
            calculateProducts(user.getId(), purchaseList, saleList);
            isDateChanged = false;
        }
        return productSet;
    }
}
