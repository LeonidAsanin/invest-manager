package org.lennardjones.investmanager.util.purchaseSaleUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class SortSaleListTests {
    List<Sale> saleList;

    @BeforeEach
    void before() {
        saleList = new LinkedList<>();
    }

    @Test
    void shouldSortByIdAscendingOrder() {
        var sale1 = new Sale();
        sale1.setId(1L);

        var sale2 = new Sale();
        sale2.setId(2L);

        var sale3 = new Sale();
        sale3.setId(3L);

        Collections.addAll(saleList, sale2, sale3, sale1);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, SortType.NONE, Sort.Direction.ASC);
        Assertions.assertEquals(saleList.get(0), sale1);
        Assertions.assertEquals(saleList.get(1), sale2);
        Assertions.assertEquals(saleList.get(2), sale3);

    }

    @Test
    void shouldSortByIdDecreasingOrder() {
        var sale1 = new Sale();
        sale1.setId(1L);

        var sale2 = new Sale();
        sale2.setId(2L);

        var sale3 = new Sale();
        sale3.setId(3L);

        Collections.addAll(saleList, sale1, sale3, sale2);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, SortType.NONE, Sort.Direction.DESC);
        Assertions.assertEquals(saleList.get(0), sale3);
        Assertions.assertEquals(saleList.get(1), sale2);
        Assertions.assertEquals(saleList.get(2), sale1);
    }

    @Test
    void shouldSortByNameAscendingOrder() {
        var sale1 = new Sale();
        sale1.setDateTime(LocalDateTime.MAX);
        sale1.setName("a");

        var sale2 = new Sale();
        sale2.setDateTime(LocalDateTime.MAX);
        sale2.setName("b");

        var sale3 = new Sale();
        sale3.setDateTime(LocalDateTime.MIN);
        sale3.setName("b");

        Collections.addAll(saleList, sale2, sale1, sale3);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, SortType.NAME, Sort.Direction.ASC);
        Assertions.assertEquals(saleList.get(0), sale1);
        Assertions.assertEquals(saleList.get(1), sale3);
        Assertions.assertEquals(saleList.get(2), sale2);
    }

    @Test
    void shouldSortByNameDecreasingOrder() {
        var sale1 = new Sale();
        sale1.setDateTime(LocalDateTime.MAX);
        sale1.setName("a");

        var sale2 = new Sale();
        sale2.setDateTime(LocalDateTime.MAX);
        sale2.setName("b");

        var sale3 = new Sale();
        sale3.setDateTime(LocalDateTime.MIN);
        sale3.setName("b");

        Collections.addAll(saleList, sale1, sale2, sale3);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, SortType.NAME, Sort.Direction.DESC);
        Assertions.assertEquals(saleList.get(0), sale2);
        Assertions.assertEquals(saleList.get(1), sale3);
        Assertions.assertEquals(saleList.get(2), sale1);
    }

    @Test
    void shouldSortByDateAscendingOrder() {
        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setDateTime(LocalDateTime.MIN);
        sale1.setName("b");

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setDateTime(LocalDateTime.MIN);
        sale2.setName("c");

        var sale3 = new Sale();
        sale3.setId(3L);
        sale3.setDateTime(LocalDateTime.MAX);
        sale3.setName("a");

        Collections.addAll(saleList, sale2, sale3, sale1);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, SortType.DATE, Sort.Direction.ASC);
        Assertions.assertEquals(saleList.get(0), sale1);
        Assertions.assertEquals(saleList.get(1), sale2);
        Assertions.assertEquals(saleList.get(2), sale3);
    }

    @Test
    void shouldSortByDateDecreasingOrder() {
        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setDateTime(LocalDateTime.MIN);
        sale1.setName("b");

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setDateTime(LocalDateTime.MIN);
        sale2.setName("c");

        var sale3 = new Sale();
        sale3.setId(3L);
        sale3.setDateTime(LocalDateTime.MAX);
        sale3.setName("a");

        Collections.addAll(saleList, sale2, sale1, sale3);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, SortType.DATE, Sort.Direction.DESC);
        Assertions.assertEquals(saleList.get(0), sale3);
        Assertions.assertEquals(saleList.get(1), sale2);
        Assertions.assertEquals(saleList.get(2), sale1);
    }
}
