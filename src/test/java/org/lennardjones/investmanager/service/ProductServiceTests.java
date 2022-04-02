package org.lennardjones.investmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.model.Product;
import org.lennardjones.investmanager.repository.ProductRepository;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {
    @Mock
    PurchaseService purchaseServiceMock;
    @Mock
    SaleService saleServiceMock;

    ProductRepositoryMock productRepositoryMock;

    User user;
    String productName;
    String otherProductName;
    String tag;
    String otherTag;
    ProductService productService;

    @BeforeEach
    void before() {
        productRepositoryMock = new ProductRepositoryMock();
        productService = new ProductService(purchaseServiceMock, saleServiceMock, productRepositoryMock);
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        productName = "productName";
        otherProductName = "otherProductName";
        tag = "tag";
        otherTag = "otherTag";
    }

    @Test
    void getAllProductsWhereThereIsNone() {
        var productSet = productService.getAllByUser(user);
        assertNotNull(productSet);
        assertTrue(productSet.isEmpty());
    }

    @Test
    void calculateProfitsForNonExistingProduct() {
        productService.calculateProfits(1L, productName, 1.5);

        var productSet = productService.getAllByUser(user);
        assertNotNull(productSet);
        assertTrue(productSet.isEmpty());
    }

    @Test
    void oneProductTwoPurchasesZeroSales() {
        List<Purchase> purchaseList = new ArrayList<>();
        var purchase1 = new Purchase();
            purchase1.setName(productName);
            purchase1.setTag(tag);
            purchase1.setAmount(1);
            purchase1.setPrice(0.9);
            purchase1.setCommission(0.1);
            purchase1.setDateTime(LocalDateTime.now());
        var purchase2 = new Purchase();
            purchase2.setName(productName);
            purchase2.setTag(tag);
            purchase2.setAmount(2);
            purchase2.setPrice(2.8);
            purchase2.setCommission(0.2);
            purchase2.setDateTime(LocalDateTime.now());
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var averagePrice = ((purchase1.getPrice() + purchase1.getCommission()) * purchase1.getAmount() +
                (purchase2.getPrice() + purchase2.getCommission()) * purchase2.getAmount()) /
                (purchase1.getAmount() + purchase2.getAmount());
        var amount = purchase1.getAmount() + purchase2.getAmount();

        Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                .thenReturn(purchaseList);

        var productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        var product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(0, product.getCurrentPrice(), 0.01);
        assertEquals(-(averagePrice * amount), product.getAbsoluteProfit(), 0.01);
        assertEquals(-100, product.getRelativeProfit(), 0.01);

        /* Setting current price */
        var currentPrice = 5.;
        productService.calculateProfits(user.getId(), productName, currentPrice);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice) * amount, product.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice) - 1) * 100, product.getRelativeProfit(), 0.01);

        /* Changing parameters of purchases => no effect before invocation of setDataChanged() */
        purchase1.setAmount(98);
        purchase1.setTag(otherTag);
        purchase2.setPrice(5.);
        purchase2.setCommission(0.);
        purchase2.setTag(otherTag);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice) * amount, product.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice) - 1) * 100, product.getRelativeProfit(), 0.01);

        /* Recalculation of profits after invocation of setDataChanged() */
        averagePrice = ((purchase1.getPrice() + purchase1.getCommission()) * purchase1.getAmount() +
                (purchase2.getPrice() + purchase2.getCommission()) * purchase2.getAmount()) /
                (purchase1.getAmount() + purchase2.getAmount());
        amount = purchase1.getAmount() + purchase2.getAmount();
        productService.setDataChanged();
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(otherTag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice) * amount, product.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice) - 1) * 100, product.getRelativeProfit(), 0.01);
    }

    @Test
    void oneProductTwoPurchasesOneSaleThenCompleteSelling() {
        List<Purchase> purchaseList = new ArrayList<>();
        List<Sale> saleList = new ArrayList<>();
        var purchase1 = new Purchase();
            purchase1.setName(productName);
            purchase1.setTag(tag);
            purchase1.setAmount(2);
            purchase1.setPrice(0.9);
            purchase1.setCommission(0.1);
            purchase1.setDateTime(LocalDateTime.MIN);
        var purchase2 = new Purchase();
            purchase2.setName(productName);
            purchase2.setTag(tag);
            purchase2.setAmount(3);
            purchase2.setPrice(2.8);
            purchase2.setCommission(0.2);
            purchase2.setDateTime(LocalDateTime.now());
        var sale = new Sale();
            sale.setName(productName);
            sale.setTag(tag);
            sale.setAmount(3);
            sale.setPrice(1.1);
            sale.setCommission(0.1);
            sale.setDateTime(LocalDateTime.MAX);
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);
        saleList.add(sale);
        var averagePrice = purchase2.getPrice() + purchase2.getCommission();
        var amount = 2;

        Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                .thenReturn(purchaseList);
        Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                .thenReturn(saleList);

        var productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        var product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(0, product.getCurrentPrice(), 0.01);
        assertEquals(-(averagePrice * amount), product.getAbsoluteProfit(), 0.01);
        assertEquals(-100, product.getRelativeProfit(), 0.01);

        /* Setting current price */
        var currentPrice = 5.;
        productService.calculateProfits(user.getId(), productName, currentPrice);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product = new ArrayList<>(productSet).get(0);
        assertEquals(product.getName(), productName);
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice) * amount, product.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice) - 1) * 100, product.getRelativeProfit(), 0.01);

        /* Changing parameters of purchases => no effect before invocation of setDataChanged() */
        purchase1.setAmount(20);
        purchase2.setAmount(10);
        sale.setAmount(10);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice) * amount, product.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice) - 1) * 100, product.getRelativeProfit(), 0.01);

        /* Recalculation of profits after invocation of setDataChanged() */
        averagePrice = (purchase1.getPrice() + purchase1.getCommission() +
                       purchase2.getPrice() + purchase2.getCommission()) / 2;
        amount = 20;
        productService.setDataChanged();
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product.getName());
        assertEquals(tag, product.getTag());
        assertEquals(averagePrice, product.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice) * amount, product.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice) - 1) * 100, product.getRelativeProfit(), 0.01);

        /* Changing parameters of purchases => no effect before invocation of setDataChanged() */
        sale.setAmount(30);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());

        /* Applying changes after setDataChanged() method invocation */
        productService.setDataChanged();
        productSet = productService.getAllByUser(user);
        assertEquals(0, productSet.size());
    }

    @Test
    void twoProducts() {
        List<Purchase> purchaseList = new ArrayList<>();
        var purchase1 = new Purchase();
            purchase1.setName(productName);
            purchase1.setTag(tag);
            purchase1.setAmount(1);
            purchase1.setPrice(0.9);
            purchase1.setCommission(0.1);
            purchase1.setDateTime(LocalDateTime.now());
        var purchase2 = new Purchase();
            purchase2.setName(otherProductName);
            purchase2.setTag(otherTag);
            purchase2.setAmount(2);
            purchase2.setPrice(2.8);
            purchase2.setCommission(0.2);
            purchase2.setDateTime(LocalDateTime.now());
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);
        var averagePrice1 = purchase1.getPrice() + purchase1.getCommission();
        var averagePrice2 = purchase2.getPrice() + purchase2.getCommission();
        var amount1 = purchase1.getAmount();
        var amount2 = purchase2.getAmount();

        Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                .thenReturn(purchaseList);

        var productSet = productService.getAllByUser(user);
        assertEquals(2, productSet.size());
        var product1 = new ArrayList<>(productSet).get(1);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(0, product1.getCurrentPrice(), 0.01);
        assertEquals(-(averagePrice1 * amount1), product1.getAbsoluteProfit(), 0.01);
        assertEquals(-100, product1.getRelativeProfit(), 0.01);
        var product2 = new ArrayList<>(productSet).get(0);
        assertEquals(otherProductName, product2.getName());
        assertEquals(otherTag, product2.getTag());
        assertEquals(averagePrice2, product2.getAveragePrice(), 0.01);
        assertEquals(0, product2.getCurrentPrice(), 0.01);
        assertEquals(-(averagePrice2 * amount2), product2.getAbsoluteProfit(), 0.01);
        assertEquals(-100, product2.getRelativeProfit(), 0.01);

        /* Setting current price */
        var currentPrice = 5.;
        productService.calculateProfits(user.getId(), productName, currentPrice);
        productSet = productService.getAllByUser(user);
        assertEquals(2, productSet.size());
        product1 = new ArrayList<>(productSet).get(1);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product1.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice1) * amount1, product1.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice1) - 1) * 100, product1.getRelativeProfit(), 0.01);
        product2 = new ArrayList<>(productSet).get(0);
        assertEquals(otherProductName, product2.getName());
        assertEquals(otherTag, product2.getTag());
        assertEquals(averagePrice2, product2.getAveragePrice(), 0.01);
        assertEquals(0, product2.getCurrentPrice(), 0.01);
        assertEquals(-(averagePrice2 * amount2), product2.getAbsoluteProfit(), 0.01);
        assertEquals(-100, product2.getRelativeProfit(), 0.01);

        /* Changing parameters of purchases => no effect before invocation of setDataChanged() */
        purchase1.setCommission(0.5);
        purchase2.setPrice(5.);
        productSet = productService.getAllByUser(user);
        assertEquals(2, productSet.size());
        product1 = new ArrayList<>(productSet).get(1);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product1.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice1) * amount1, product1.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice1) - 1) * 100, product1.getRelativeProfit(), 0.01);
        product2 = new ArrayList<>(productSet).get(0);
        assertEquals(otherProductName, product2.getName());
        assertEquals(otherTag, product2.getTag());
        assertEquals(averagePrice2, product2.getAveragePrice(), 0.01);
        assertEquals(0, product2.getCurrentPrice(), 0.01);
        assertEquals((0 - averagePrice2) * amount2, product2.getAbsoluteProfit(),0.01);
        assertEquals((0 / (averagePrice2) - 1) * 100, product2.getRelativeProfit(), 0.01);

        /* Recalculation of profits after invocation of setDataChanged() */
        averagePrice1 = purchase1.getPrice() + purchase1.getCommission();
        averagePrice2 = purchase2.getPrice() + purchase2.getCommission();
        amount1 = purchase1.getAmount();
        amount2 = purchase2.getAmount();
        productService.setDataChanged();
        productSet = productService.getAllByUser(user);
        assertEquals(2, productSet.size());
        product1 = new ArrayList<>(productSet).get(1);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product1.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice1) * amount1, product1.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice1) - 1) * 100, product1.getRelativeProfit(), 0.01);
        product2 = new ArrayList<>(productSet).get(0);
        assertEquals(otherProductName, product2.getName());
        assertEquals(otherTag, product2.getTag());
        assertEquals(averagePrice2, product2.getAveragePrice(), 0.01);
        assertEquals(0, product2.getCurrentPrice(), 0.01);
        assertEquals((0 - averagePrice2) * amount2, product2.getAbsoluteProfit(),0.01);
        assertEquals((0 / (averagePrice2) - 1) * 100, product2.getRelativeProfit(), 0.01);
    }

    @Test
    void addingNewProduct() {
        List<Purchase> purchaseList = new ArrayList<>();
        var purchase1 = new Purchase();
            purchase1.setName(productName);
            purchase1.setTag(tag);
            purchase1.setAmount(1);
            purchase1.setPrice(0.9);
            purchase1.setCommission(0.1);
            purchase1.setDateTime(LocalDateTime.now());
        var purchase2 = new Purchase();
            purchase2.setName(otherProductName);
            purchase2.setTag(otherTag);
            purchase2.setAmount(2);
            purchase2.setPrice(2.8);
            purchase2.setCommission(0.2);
            purchase2.setDateTime(LocalDateTime.now());
        purchaseList.add(purchase1);
        var averagePrice1 = purchase1.getPrice() + purchase1.getCommission();
        var averagePrice2 = purchase2.getPrice() + purchase2.getCommission();
        var amount1 = purchase1.getAmount();
        var amount2 = purchase2.getAmount();

        Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                .thenReturn(purchaseList);

        var productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        var product1 = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(0, product1.getCurrentPrice(), 0.01);
        assertEquals(-(averagePrice1 * amount1), product1.getAbsoluteProfit(), 0.01);
        assertEquals(-100, product1.getRelativeProfit(), 0.01);

        /* Setting current price */
        var currentPrice = 5.;
        productService.calculateProfits(user.getId(), productName, currentPrice);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product1 = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product1.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice1) * amount1, product1.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice1) - 1) * 100, product1.getRelativeProfit(), 0.01);

        /* Adding new product => no effect before invocation of setDataChanged() */
        purchaseList.add(purchase2);
        productSet = productService.getAllByUser(user);
        assertEquals(1, productSet.size());
        product1 = new ArrayList<>(productSet).get(0);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product1.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice1) * amount1, product1.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice1) - 1) * 100, product1.getRelativeProfit(), 0.01);

        /* Recalculating of products after invocation of setDataChanged() */
        productService.setDataChanged();
        productSet = productService.getAllByUser(user);
        assertEquals(2, productSet.size());
        product1 = new ArrayList<>(productSet).get(1);
        assertEquals(productName, product1.getName());
        assertEquals(tag, product1.getTag());
        assertEquals(averagePrice1, product1.getAveragePrice(), 0.01);
        assertEquals(currentPrice, product1.getCurrentPrice(), 0.01);
        assertEquals((currentPrice - averagePrice1) * amount1, product1.getAbsoluteProfit(),0.01);
        assertEquals((currentPrice / (averagePrice1) - 1) * 100, product1.getRelativeProfit(), 0.01);
        var product2 = new ArrayList<>(productSet).get(0);
        assertEquals(otherProductName, product2.getName());
        assertEquals(otherTag, product2.getTag());
        assertEquals(averagePrice2, product2.getAveragePrice(), 0.01);
        assertEquals(0, product2.getCurrentPrice(), 0.01);
        assertEquals((0 - averagePrice2) * amount2, product2.getAbsoluteProfit(),0.01);
        assertEquals((0 / (averagePrice2) - 1) * 100, product2.getRelativeProfit(), 0.01);
    }

    static class ProductRepositoryMock implements ProductRepository {
        private List<TestableProduct> productList = new ArrayList<>();

        @Override
        public Optional<Double> getCurrentPriceByUserIdAndProductName(Long userId, String productName) {
            return productList.stream()
                    .filter(p -> p.getOwnerId().equals(userId) && p.getName().equals(productName))
                    .map(Product::getCurrentPrice)
                    .findAny();
        }

        @Override
        public void save(Long userId, String productName, double currentPrice) {
            /* Deleting old version of product */
            productList = productList.stream()
                    .filter(p -> !p.getOwnerId().equals(userId) && !p.getName().equals(productName))
                    .collect(Collectors.toList());

            /* Adding new version of product */
            var product = new TestableProduct();
            product.setOwnerId(userId);
            product.setName(productName);
            product.setCurrentPrice(currentPrice);
            productList.add(product);
        }

        @Override
        public List<String> getAllNamesByUserId(Long userId) {
            return productList.stream()
                    .filter(p -> p.getOwnerId().equals(userId))
                    .map(Product::getName)
                    .collect(Collectors.toList());
        }

        @Override
        public void removeByUserIdAndProductName(Long userId, String productName) {
            productList = productList.stream()
                    .filter(p -> !p.getOwnerId().equals(userId) && !p.getName().equals(productName))
                    .collect(Collectors.toList());
        }

        static class TestableProduct extends Product {
            private Long ownerId;

            public Long getOwnerId() {
                return ownerId;
            }

            public void setOwnerId(Long ownerId) {
                this.ownerId = ownerId;
            }
        }
    }
}
