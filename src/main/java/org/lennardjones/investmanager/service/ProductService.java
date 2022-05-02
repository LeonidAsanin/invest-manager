package org.lennardjones.investmanager.service;

import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.model.Product;
import org.lennardjones.investmanager.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.*;
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
    private final PurchaseService purchaseService;
    private final SaleService saleService;
    private final ProductRepository productRepository;

    private Set<Product> productSet;
    private boolean isDataChanged;

    public ProductService(PurchaseService purchaseService,
                          SaleService saleService,
                          ProductRepository productRepository) {
        this.purchaseService = purchaseService;
        this.saleService = saleService;
        this.productRepository = productRepository;

        productSet = new HashSet<>();
        isDataChanged = true;
    }

    public void setDataChanged() {
        isDataChanged = true;
    }

    public void calculateProfits(Long userId, String productName, double currentPrice) {
        /* Searching for product by name */
        var optionalProduct = productSet.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst();

        /* Updating current price and calculating current profits if product exists */
        if (optionalProduct.isPresent()) {
            var product = optionalProduct.get();

            product.setCurrentPrice(currentPrice);
            productRepository.save(userId, productName, currentPrice);

            var absoluteProfit = (currentPrice - product.getAveragePrice()) * product.getAmount();
            product.setAbsoluteProfit(absoluteProfit);

            var relativePrice = (currentPrice / product.getAveragePrice() - 1) * 100;
            product.setRelativeProfit(relativePrice);
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

            /* Cloning of input list elements in order not to affect them */
            for (int i = 0; i < purchaseStack.size(); i++) {
                var purchase = purchaseStack.get(i);
                var purchaseClone = (Purchase) purchase.clone();
                purchaseStack.set(i, purchaseClone);
            }
            for (int i = 0; i < saleStack.size(); i++) {
                var sale = saleStack.get(i);
                var saleClone = (Sale) sale.clone();
                saleStack.set(i, saleClone);
            }

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
                    .mapToDouble(p -> (p.getPrice() + p.getCommission()) * p.getAmount())
                    .sum() / productAmount;

            /* Filling in fields of the product */
            var product = new Product();
                product.setName(productName);
                product.setAmount(productAmount);
                product.setAveragePrice(averagePriceConsideringCommission);
            var tag = purchaseStack.stream().findAny().orElseThrow().getTag();
                product.setTag(tag);

            /* Setting current price and calculating current profits if corresponding data exists */
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
        if (isDataChanged) {
            var purchaseList = purchaseService.getListByUsername(user.getUsername());
            var saleList = saleService.getListByUsername(user.getUsername());
            calculateProducts(user.getId(), purchaseList, saleList);
            for (var product : productSet) {
                calculateProfits(user.getId(), product.getName(), product.getCurrentPrice());
            }
            isDataChanged = false;
        }
        return productSet;
    }
}
