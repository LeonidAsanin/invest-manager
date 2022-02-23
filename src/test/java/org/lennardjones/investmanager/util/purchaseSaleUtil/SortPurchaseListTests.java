package org.lennardjones.investmanager.util.purchaseSaleUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortOrderType;
import org.lennardjones.investmanager.util.SortType;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class SortPurchaseListTests {
    List<Purchase> purchaseList;

    @BeforeEach
    void before() {
        purchaseList = new LinkedList<>();
    }

    @Test
    void shouldSortByIdAscendingOrder() {
        var purchase1 = new Purchase();
        purchase1.setId(1L);

        var purchase2 = new Purchase();
        purchase2.setId(2L);

        var purchase3 = new Purchase();
        purchase3.setId(3L);

        Collections.addAll(purchaseList, purchase2, purchase3, purchase1);
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, SortType.NONE, SortOrderType.ASC);
        Assertions.assertEquals(purchaseList.get(0), purchase1);
        Assertions.assertEquals(purchaseList.get(1), purchase2);
        Assertions.assertEquals(purchaseList.get(2), purchase3);

    }

    @Test
    void shouldSortByIdDecreasingOrder() {
        var purchase1 = new Purchase();
        purchase1.setId(1L);

        var purchase2 = new Purchase();
        purchase2.setId(2L);

        var purchase3 = new Purchase();
        purchase3.setId(3L);

        Collections.addAll(purchaseList, purchase1, purchase3, purchase2);
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, SortType.NONE, SortOrderType.DEC);
        Assertions.assertEquals(purchaseList.get(0), purchase3);
        Assertions.assertEquals(purchaseList.get(1), purchase2);
        Assertions.assertEquals(purchaseList.get(2), purchase1);
    }

    @Test
    void shouldSortByNameAscendingOrder() {
        var purchase1 = new Purchase();
        purchase1.setDate(LocalDate.MAX);
        purchase1.setName("a");

        var purchase2 = new Purchase();
        purchase2.setDate(LocalDate.MAX);
        purchase2.setName("b");

        var purchase3 = new Purchase();
        purchase3.setDate(LocalDate.MIN);
        purchase3.setName("b");

        Collections.addAll(purchaseList, purchase2, purchase1, purchase3);
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, SortType.NAME, SortOrderType.ASC);
        Assertions.assertEquals(purchaseList.get(0), purchase1);
        Assertions.assertEquals(purchaseList.get(1), purchase3);
        Assertions.assertEquals(purchaseList.get(2), purchase2);
    }

    @Test
    void shouldSortByNameDecreasingOrder() {
        var purchase1 = new Purchase();
        purchase1.setDate(LocalDate.MAX);
        purchase1.setName("a");

        var purchase2 = new Purchase();
        purchase2.setDate(LocalDate.MAX);
        purchase2.setName("b");

        var purchase3 = new Purchase();
        purchase3.setDate(LocalDate.MIN);
        purchase3.setName("b");

        Collections.addAll(purchaseList, purchase1, purchase2, purchase3);
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, SortType.NAME, SortOrderType.DEC);
        Assertions.assertEquals(purchaseList.get(0), purchase2);
        Assertions.assertEquals(purchaseList.get(1), purchase3);
        Assertions.assertEquals(purchaseList.get(2), purchase1);
    }

    @Test
    void shouldSortByDateAscendingOrder() {
        var purchase1 = new Purchase();
        purchase1.setDate(LocalDate.MIN);
        purchase1.setName("b");

        var purchase2 = new Purchase();
        purchase2.setDate(LocalDate.MIN);
        purchase2.setName("c");

        var purchase3 = new Purchase();
        purchase3.setDate(LocalDate.MAX);
        purchase3.setName("a");

        Collections.addAll(purchaseList, purchase2, purchase3, purchase1);
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, SortType.DATE, SortOrderType.ASC);
        Assertions.assertEquals(purchaseList.get(0), purchase1);
        Assertions.assertEquals(purchaseList.get(1), purchase2);
        Assertions.assertEquals(purchaseList.get(2), purchase3);
    }

    @Test
    void shouldSortByDateDecreasingOrder() {
        var purchase1 = new Purchase();
        purchase1.setDate(LocalDate.MIN);
        purchase1.setName("b");

        var purchase2 = new Purchase();
        purchase2.setDate(LocalDate.MIN);
        purchase2.setName("c");

        var purchase3 = new Purchase();
        purchase3.setDate(LocalDate.MAX);
        purchase3.setName("a");

        Collections.addAll(purchaseList, purchase2, purchase1, purchase3);
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, SortType.DATE, SortOrderType.DEC);
        Assertions.assertEquals(purchaseList.get(0), purchase3);
        Assertions.assertEquals(purchaseList.get(1), purchase2);
        Assertions.assertEquals(purchaseList.get(2), purchase1);
    }
}
