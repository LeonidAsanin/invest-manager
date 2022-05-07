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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checking queue of purchases and sales for being correct")
class IsQueueIncorrectTests {
    final String PRODUCT_NAME = "productName";
    final String OTHER_PRODUCT_NAME = "otherProductName";

    List<Purchase> purchaseList;
    List<Sale> saleList;

    @BeforeEach
    void setup() {
        purchaseList = new LinkedList<>();
        saleList = new LinkedList<>();
    }

    @Test
    @DisplayName("CASE: No sales, no purchases")
    void noSalesNoPurchases() {
        //given

        //when
        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME));

        //then
        var sale = new Sale();
        sale.setId(1L);
        sale.setName(OTHER_PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MIN);
        sale.setAmount(1);

        saleList.add(sale);

        assertFalse(PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME));
    }

    @Test
    @DisplayName("CASE: Sales without purchases")
    void salesWithoutPurchases() {
        //given
        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setName(PRODUCT_NAME);
        sale1.setDateTime(LocalDateTime.MIN);
        sale1.setAmount(1);

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setName(OTHER_PRODUCT_NAME);
        sale2.setDateTime(LocalDateTime.MAX);
        sale2.setAmount(2);

        saleList.add(sale1);
        saleList.add(sale2);

        //when
        var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertTrue(result);
    }

    @Test
    @DisplayName("CASE: Purchases without sales")
    void purchasesWithoutSales() {
        //given
        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setName(PRODUCT_NAME);
        purchase1.setDateTime(LocalDateTime.MIN);
        purchase1.setAmount(1);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setName(OTHER_PRODUCT_NAME);
        purchase2.setDateTime(LocalDateTime.MAX);
        purchase2.setAmount(11);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        //when
        var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("CASE: Sale after purchase Ns <= Np, Np = 3")
    void saleAfterPurchaseWithLessOrEqualAmount(int saleAmount) {
        //given
        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setName(PRODUCT_NAME);
        purchase.setDateTime(LocalDateTime.MIN);
        purchase.setAmount(3);

        purchaseList.add(purchase);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MAX);
        sale.setAmount(saleAmount);

        saleList.add(sale);

        assertAll(
                () -> {
                    //when
                    var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

                    //then
                    assertFalse(result);
                },
                () -> {
                    //and given
                    purchase.setDateTime(LocalDateTime.MAX);

                    //when
                    var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

                    //then
                    assertFalse(result);
                },
                () -> {
                    //and given
                    var otherSale = new Sale();
                    otherSale.setId(2L);
                    otherSale.setName(OTHER_PRODUCT_NAME);
                    otherSale.setDateTime(LocalDateTime.MAX);
                    otherSale.setAmount(12);

                    saleList.add(otherSale);

                    //when
                    var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

                    //then
                    assertFalse(result);
                }
        );
    }

    @Test
    @DisplayName("CASE: Sale after purchase Ns > Np")
    void saleAfterPurchaseWithGreaterAmount() {
        //given
        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setName(PRODUCT_NAME);
        purchase.setDateTime(LocalDateTime.MIN);
        purchase.setAmount(3);

        purchaseList.add(purchase);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MAX);
        sale.setAmount(4);

        saleList.add(sale);

        //when
        var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("CASE: Sale before purchase Ns <= Np, Np = 2")
    void saleBeforePurchase(int saleAmount) {
        //given
        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setName(PRODUCT_NAME);
        purchase.setDateTime(LocalDateTime.MAX);
        purchase.setAmount(2);

        purchaseList.add(purchase);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MIN);
        sale.setAmount(saleAmount);

        saleList.add(sale);

        //when
        var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("CASE: Sale after two purchases Ns <= Np, Np = 3")
    void saleAfterTwoPurchasesWithLessOrEqualAmount(int saleAmount) {
        //given
        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setName(PRODUCT_NAME);
        purchase1.setDateTime(LocalDateTime.MIN);
        purchase1.setAmount(1);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setName(PRODUCT_NAME);
        purchase2.setDateTime(LocalDateTime.MIN);
        purchase2.setAmount(2);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MAX);
        sale.setAmount(saleAmount);

        saleList.add(sale);

        assertAll(
                () -> {
                    //when
                    var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

                    //then
                    assertFalse(result);
                },
                () -> {
                    //and given
                    purchase1.setDateTime(LocalDateTime.MAX);
                    purchase2.setDateTime(LocalDateTime.MAX);

                    //when
                    var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

                    //then
                    assertFalse(result);
                },
                () -> {
                    //and given
                    var otherSale = new Sale();
                    otherSale.setId(2L);
                    otherSale.setName(OTHER_PRODUCT_NAME);
                    otherSale.setDateTime(LocalDateTime.MAX);
                    otherSale.setAmount(12);

                    saleList.add(otherSale);

                    //when
                    var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

                    //then
                    assertFalse(result);
                }
        );
    }

    @Test
    @DisplayName("CASE: Sale after two purchases Ns > Np")
    void saleAfterTwoPurchasesWithGreaterAmount() {
        //given
        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setName(PRODUCT_NAME);
        purchase1.setDateTime(LocalDateTime.MIN);
        purchase1.setAmount(1);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setName(PRODUCT_NAME);
        purchase2.setDateTime(LocalDateTime.MIN);
        purchase2.setAmount(2);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MAX);
        sale.setAmount(4);

        saleList.add(sale);

        //when
        var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    @DisplayName("CASE: Sale before two purchases Ns <= Np, Np = 2")
    void saleBeforeTwoPurchases(int saleAmount) {
        //given
        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setName(PRODUCT_NAME);
        purchase1.setDateTime(LocalDateTime.MAX);
        purchase1.setAmount(1);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setName(PRODUCT_NAME);
        purchase2.setDateTime(LocalDateTime.MAX);
        purchase2.setAmount(1);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setDateTime(LocalDateTime.MIN);
        sale.setAmount(saleAmount);

        saleList.add(sale);

        //when
        var result = PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertTrue(result);
    }
}
