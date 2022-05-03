package org.lennardjones.investmanager.util.purchaseSaleUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Checking queue of purchases and sales for being correct")
class IsQueueIncorrectTests {
    List<Purchase> purchaseList;
    List<Sale> saleList;
    String productName = "productName";
    String otherProductName = "otherProductName";

    @BeforeEach
    void before() {
        purchaseList = new LinkedList<>();
        saleList = new LinkedList<>();
    }

    @Test
    @DisplayName("No sales, no purchases")
    void noSalesNoPurchases() {
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(otherProductName);
            sale.setDateTime(LocalDateTime.MIN);
            sale.setAmount(1);
        saleList.add(sale);
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @Test
    @DisplayName("Sales without purchases")
    void salesWithoutPurchases() {
        var sale1 = new Sale();
            sale1.setId(1L);
            sale1.setName(productName);
            sale1.setDateTime(LocalDateTime.MIN);
            sale1.setAmount(1);
        var sale2 = new Sale();
            sale2.setId(2L);
            sale2.setName(otherProductName);
            sale2.setDateTime(LocalDateTime.MAX);
            sale2.setAmount(2);
        saleList.add(sale1);
        saleList.add(sale2);

        assertTrue(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @Test
    @DisplayName("Purchases without sales")
    void purchasesWithoutSales() {
        var purchase1 = new Purchase();
            purchase1.setId(1L);
            purchase1.setName(productName);
            purchase1.setDateTime(LocalDateTime.MIN);
            purchase1.setAmount(1);
        var purchase2 = new Purchase();
            purchase2.setId(2L);
            purchase2.setName(otherProductName);
            purchase2.setDateTime(LocalDateTime.MAX);
            purchase2.setAmount(11);
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Sale after purchase Ns <= Np, Np = 3")
    void saleAfterPurchaseWithLessOrEqualAmount(int saleAmount) {
        var purchase = new Purchase();
            purchase.setId(1L);
            purchase.setName(productName);
            purchase.setDateTime(LocalDateTime.MIN);
            purchase.setAmount(3);
        purchaseList.add(purchase);

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(productName);
            sale.setDateTime(LocalDateTime.MAX);
            sale.setAmount(saleAmount);
        saleList.add(sale);

        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));

        purchase.setDateTime(LocalDateTime.MAX);
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));

        var otherSale = new Sale();
            otherSale.setId(2L);
            otherSale.setName(otherProductName);
            otherSale.setDateTime(LocalDateTime.MAX);
            otherSale.setAmount(12);
        saleList.add(otherSale);
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @Test
    @DisplayName("Sale after purchase Ns > Np")
    void saleAfterPurchaseWithGreaterAmount() {
        var purchase = new Purchase();
            purchase.setId(1L);
            purchase.setName(productName);
            purchase.setDateTime(LocalDateTime.MIN);
            purchase.setAmount(3);
        purchaseList.add(purchase);

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(productName);
            sale.setDateTime(LocalDateTime.MAX);
            sale.setAmount(4);
        saleList.add(sale);

        assertTrue(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Sale before purchase Ns <= Np, Np = 2")
    void saleBeforePurchase(int saleAmount) {
        var purchase = new Purchase();
            purchase.setId(1L);
            purchase.setName(productName);
            purchase.setDateTime(LocalDateTime.MAX);
            purchase.setAmount(2);
        purchaseList.add(purchase);

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(productName);
            sale.setDateTime(LocalDateTime.MIN);
            sale.setAmount(saleAmount);
        saleList.add(sale);

        assertTrue(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Sale after two purchases Ns <= Np, Np = 3")
    void saleAfterTwoPurchasesWithLessOrEqualAmount(int saleAmount) {
        var purchase1 = new Purchase();
            purchase1.setId(1L);
            purchase1.setName(productName);
            purchase1.setDateTime(LocalDateTime.MIN);
            purchase1.setAmount(1);
        var purchase2 = new Purchase();
            purchase2.setId(2L);
            purchase2.setName(productName);
            purchase2.setDateTime(LocalDateTime.MIN);
            purchase2.setAmount(2);
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(productName);
            sale.setDateTime(LocalDateTime.MAX);
            sale.setAmount(saleAmount);
        saleList.add(sale);

        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));

        purchase1.setDateTime(LocalDateTime.MAX);
        purchase2.setDateTime(LocalDateTime.MAX);
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));

        var otherSale = new Sale();
            otherSale.setId(2L);
            otherSale.setName(otherProductName);
            otherSale.setDateTime(LocalDateTime.MAX);
            otherSale.setAmount(12);
        saleList.add(otherSale);
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @Test
    @DisplayName("Sale after two purchases Ns > Np")
    void saleAfterTwoPurchasesWithGreaterAmount() {
        var purchase1 = new Purchase();
            purchase1.setId(1L);
            purchase1.setName(productName);
            purchase1.setDateTime(LocalDateTime.MIN);
            purchase1.setAmount(1);
        var purchase2 = new Purchase();
            purchase2.setId(2L);
            purchase2.setName(productName);
            purchase2.setDateTime(LocalDateTime.MIN);
            purchase2.setAmount(2);
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(productName);
            sale.setDateTime(LocalDateTime.MAX);
            sale.setAmount(4);
        saleList.add(sale);

        assertTrue(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("Sale before two purchases Ns <= Np, Np = 2")
    void saleBeforeTwoPurchases(int saleAmount) {
        var purchase1 = new Purchase();
            purchase1.setId(1L);
            purchase1.setName(productName);
            purchase1.setDateTime(LocalDateTime.MAX);
            purchase1.setAmount(1);
        var purchase2 = new Purchase();
            purchase2.setId(2L);
            purchase2.setName(productName);
            purchase2.setDateTime(LocalDateTime.MAX);
            purchase2.setAmount(1);
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
            sale.setId(1L);
            sale.setName(productName);
            sale.setDateTime(LocalDateTime.MIN);
            sale.setAmount(saleAmount);
        saleList.add(sale);

        assertTrue(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName));
    }
}
