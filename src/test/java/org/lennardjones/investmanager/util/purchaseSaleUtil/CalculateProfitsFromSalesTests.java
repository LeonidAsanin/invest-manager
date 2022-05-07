package org.lennardjones.investmanager.util.purchaseSaleUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Calculation of profits from sales")
class CalculateProfitsFromSalesTests {
    final String PRODUCT_NAME = "test product";
    final String OTHER_PRODUCT_NAME = "other test product";

    List<Purchase> purchaseList;
    List<Sale> saleList;

    @BeforeEach
    void setup() {
        purchaseList = new LinkedList<>();
        saleList = new LinkedList<>();
    }

    @Test
    @DisplayName("Proper filtering by product name")
    void methodShouldFilterByProductName() {
        //given
        var purchase1 = new Purchase();
        purchase1.setAmount(1);
        purchase1.setPrice(100.001);
        purchase1.setCommission(0.);
        purchase1.setName(PRODUCT_NAME);

        var purchase2 = new Purchase();
        purchase2.setAmount(1);
        purchase2.setPrice(1000.);
        purchase2.setName(OTHER_PRODUCT_NAME);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale1 = new Sale();
        sale1.setName(PRODUCT_NAME);
        sale1.setAmount(1);
        sale1.setPrice(200.);
        sale1.setCommission(0.);

        var sale2 = new Sale();
        sale2.setName(OTHER_PRODUCT_NAME);
        sale2.setAmount(1);
        sale2.setPrice(2000.);
        sale2.setCommission(0.);

        saleList.add(sale1);
        saleList.add(sale2);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertEquals(1, methodResult.size());
        assertEquals(PRODUCT_NAME, methodResult.get(0).getName());

        var absoluteProfit = sale1.getAmount() * sale1.getPrice() - purchase1.getAmount() * purchase1.getPrice();
        assertEquals(absoluteProfit, methodResult.get(0).getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale1.getPrice() - sale1.getCommission()) * sale1.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, methodResult.get(0).getRelativeProfit(), 0.01);
    }

    @Test
    @DisplayName("No affect on input lists")
    void methodDoesNotAffectInputLists() {
        //given
        var defaultPurchaseId = 1L;
        var defaultPurchaseOwner = new User();
        var defaultPurchaseDate = LocalDateTime.MIN;
        var defaultPurchaseName = PRODUCT_NAME;
        var defaultPurchasePrice = 10.;
        var defaultPurchaseAmount = 5;
        var defaultPurchaseCommission = 1.;

        var defaultSaleId = 2L;
        var defaultSeller = new User();
        var defaultSaleDate = LocalDateTime.MAX;
        var defaultSaleName = PRODUCT_NAME;
        var defaultSalePrice = 22.;
        var defaultSaleAmount = 3;
        var defaultSaleCommission = 2.;
        var defaultSaleAbsoluteProfit = 100.;
        var defaultSaleRelativeProfit = 0.5;

        var purchase = new Purchase();
        purchase.setId(defaultPurchaseId);
        purchase.setOwner(defaultPurchaseOwner);
        purchase.setDateTime(defaultPurchaseDate);
        purchase.setName(defaultPurchaseName);
        purchase.setPrice(defaultPurchasePrice);
        purchase.setAmount(defaultPurchaseAmount);
        purchase.setCommission(defaultPurchaseCommission);

        var sale = new Sale();
        sale.setId(defaultSaleId);
        sale.setSeller(defaultSeller);
        sale.setDateTime(defaultSaleDate);
        sale.setName(defaultSaleName);
        sale.setPrice(defaultSalePrice);
        sale.setAmount(defaultSaleAmount);
        sale.setCommission(defaultSaleCommission);
        sale.setAbsoluteProfit(defaultSaleAbsoluteProfit);
        sale.setRelativeProfit(defaultSaleRelativeProfit);

        purchaseList.add(purchase);
        saleList.add(sale);

        //when
        PurchaseSaleUtil.calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        assertAll(
                () -> assertEquals((long) purchase.getId(), defaultPurchaseId),
                () -> assertEquals(purchase.getOwner(), defaultPurchaseOwner),
                () -> assertEquals(purchase.getDateTime(), defaultPurchaseDate),
                () -> assertEquals(purchase.getName(), defaultPurchaseName),
                () -> assertEquals(purchase.getPrice(), defaultPurchasePrice),
                () -> assertEquals(purchase.getAmount(), defaultPurchaseAmount),
                () -> assertEquals(purchase.getCommission(), defaultPurchaseCommission),

                () -> assertEquals((long) sale.getId(), defaultSaleId),
                () -> assertEquals(sale.getSeller(), defaultSeller),
                () -> assertEquals(sale.getDateTime(), defaultSaleDate),
                () -> assertEquals(sale.getName(), defaultSaleName),
                () -> assertEquals(sale.getPrice(), defaultSalePrice),
                () -> assertEquals(sale.getAmount(), defaultSaleAmount),
                () -> assertEquals(sale.getCommission(), defaultSaleCommission),
                () -> assertEquals(sale.getAbsoluteProfit(), defaultSaleAbsoluteProfit),
                () -> assertEquals(sale.getRelativeProfit(), defaultSaleRelativeProfit)
        );
    }

    @Test
    @DisplayName("CASE: One sale with amount less than product amount")
    void oneSaleWithAmountLessThanProductAmount() {
        //given
        var purchase = new Purchase();
        purchase.setName(PRODUCT_NAME);
        purchase.setAmount(5);
        purchase.setPrice(10.001);
        purchase.setCommission(1.);

        purchaseList.add(purchase);

        var sale = new Sale();
        sale.setName(PRODUCT_NAME);
        sale.setAmount(3);
        sale.setPrice(22.);
        sale.setCommission(1.);

        saleList.add(sale);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        var absoluteProfit = sale.getAmount() * (sale.getPrice() - sale.getCommission()) -
                sale.getAmount() * (purchase.getPrice() + purchase.getCommission());
        assertEquals(absoluteProfit, methodResult.get(0).getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale.getPrice() - sale.getCommission()) * sale.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, methodResult.get(0).getRelativeProfit(), 0.01);
    }

    @Test
    @DisplayName("CASE: Two sales with amount less than product amount")
    void twoSalesWithAmountLessThanProductAmount() {
        //given
        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setName(PRODUCT_NAME);
        purchase.setAmount(5);
        purchase.setPrice(10.);
        purchase.setCommission(1.);

        purchaseList.add(purchase);

        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setName(PRODUCT_NAME);
        sale1.setAmount(3);
        sale1.setPrice(22.003);
        sale1.setCommission(1.);
        sale1.setDateTime(LocalDateTime.MAX);

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setName(PRODUCT_NAME);
        sale2.setAmount(1);
        sale2.setPrice(12.004);
        sale2.setCommission(1.);
        sale2.setDateTime(LocalDateTime.MAX);

        saleList.add(sale1);
        saleList.add(sale2);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        var newSale1 = methodResult.stream()
                .filter(s -> s.getId().equals(1L))
                .findAny()
                .orElseThrow();
        var newSale2 = methodResult.stream()
                .filter(s -> s.getId().equals(2L))
                .findAny()
                .orElseThrow();

        var absoluteProfit = sale1.getAmount() * (sale1.getPrice() - sale1.getCommission()) -
                sale1.getAmount() * (purchase.getPrice() + purchase.getCommission());
        assertEquals(absoluteProfit, newSale1.getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale1.getPrice() - sale1.getCommission()) * sale1.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, newSale1.getRelativeProfit(), 0.01);

        absoluteProfit = sale2.getAmount() * (sale2.getPrice() - sale2.getCommission()) -
                sale2.getAmount() * (purchase.getPrice() + purchase.getCommission());
        assertEquals(absoluteProfit, newSale2.getAbsoluteProfit(), 0.01);

        fullPriceOfSelling = (sale2.getPrice() - sale2.getCommission()) * sale2.getAmount();
        relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, newSale2.getRelativeProfit(), 0.01);
    }

    @Test
    @DisplayName("CASE: One sale with amount equal to product amount")
    void oneSaleWithAmountEqualToProductAmount() {
        //given
        var purchase = new Purchase();
        purchase.setName(PRODUCT_NAME);
        purchase.setAmount(5);
        purchase.setPrice(10.);
        purchase.setCommission(1.);

        purchaseList.add(purchase);

        var sale = new Sale();
        sale.setName(PRODUCT_NAME);
        sale.setAmount(5);
        sale.setPrice(22.);
        sale.setCommission(1.);

        saleList.add(sale);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        var absoluteProfit = sale.getAmount() * (sale.getPrice() - sale.getCommission()) -
                sale.getAmount() * (purchase.getPrice() + purchase.getCommission());
        assertEquals(absoluteProfit, methodResult.get(0).getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale.getPrice() - sale.getCommission()) * sale.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, methodResult.get(0).getRelativeProfit(), 0.01);
    }

    @Test
    @DisplayName("CASE: Two sales with amount equal to product amount")
    void twoSalesWithAmountEqualToProductAmount() {
        //given
        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setName(PRODUCT_NAME);
        purchase.setAmount(5);
        purchase.setPrice(10.);
        purchase.setCommission(1.);
        purchase.setDateTime(LocalDateTime.MIN);

        purchaseList.add(purchase);

        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setName(PRODUCT_NAME);
        sale1.setAmount(2);
        sale1.setPrice(22.);
        sale1.setCommission(1.);
        sale1.setDateTime(LocalDateTime.MAX);

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setName(PRODUCT_NAME);
        sale2.setAmount(3);
        sale2.setPrice(12.);
        sale2.setCommission(1.);
        sale2.setDateTime(LocalDateTime.MAX);

        saleList.add(sale1);
        saleList.add(sale2);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        var newSale1 = methodResult.stream()
                .filter(s -> s.getId().equals(1L))
                .findAny()
                .orElseThrow();
        var newSale2 = methodResult.stream()
                .filter(s -> s.getId().equals(2L))
                .findAny()
                .orElseThrow();

        var absoluteProfit = sale1.getAmount() * (sale1.getPrice() - sale1.getCommission()) -
                sale1.getAmount() * (purchase.getPrice() + purchase.getCommission());
        assertEquals(absoluteProfit, newSale1.getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale1.getPrice() - sale1.getCommission()) * sale1.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, newSale1.getRelativeProfit(), 0.01);

        absoluteProfit = sale2.getAmount() * (sale2.getPrice() - sale2.getCommission()) -
                sale2.getAmount() * (purchase.getPrice() + purchase.getCommission());
        assertEquals(absoluteProfit, newSale2.getAbsoluteProfit(), 0.01);

        fullPriceOfSelling = (sale2.getPrice() - sale2.getCommission()) * sale2.getAmount();
        relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, newSale2.getRelativeProfit(), 0.01);
    }

    @Test
    @DisplayName("CASE: One sale with amount greater than product amount")
    void oneSaleWithAmountGreaterThanProductAmount() {
        //given
        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setName(PRODUCT_NAME);
        purchase1.setAmount(5);
        purchase1.setPrice(10.);
        purchase1.setCommission(1.);
        purchase1.setDateTime(LocalDateTime.MIN);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setName(PRODUCT_NAME);
        purchase2.setAmount(5);
        purchase2.setPrice(20.);
        purchase2.setCommission(2.);
        purchase2.setDateTime(LocalDateTime.MIN);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
        sale.setId(1L);
        sale.setName(PRODUCT_NAME);
        sale.setAmount(7);
        sale.setPrice(22.);
        sale.setCommission(1.);

        saleList.add(sale);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        var absoluteProfit = sale.getAmount() * (sale.getPrice() - sale.getCommission()) -
                purchase1.getAmount() * (purchase1.getPrice() + purchase1.getCommission()) -
                (sale.getAmount() - purchase1.getAmount()) * (purchase2.getPrice() + purchase2.getCommission());
        assertEquals(absoluteProfit, methodResult.get(0).getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale.getPrice() - sale.getCommission()) * sale.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, methodResult.get(0).getRelativeProfit(), 0.01);
    }

    @Test
    @DisplayName("CASE: Two sales with amount greater than product amount")
    void twoSalesWithAmountGreaterThanProductAmount() {
        //given
        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setName(PRODUCT_NAME);
        purchase1.setAmount(5);
        purchase1.setPrice(10.);
        purchase1.setCommission(1.);
        purchase1.setDateTime(LocalDateTime.MIN);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setName(PRODUCT_NAME);
        purchase2.setAmount(5);
        purchase2.setPrice(20.);
        purchase2.setCommission(2.);
        purchase2.setDateTime(LocalDateTime.MIN);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setName(PRODUCT_NAME);
        sale1.setAmount(4);
        sale1.setPrice(22.);
        sale1.setCommission(1.);
        sale1.setDateTime(LocalDateTime.MAX);

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setName(PRODUCT_NAME);
        sale2.setAmount(4);
        sale2.setPrice(12.);
        sale2.setCommission(1.);
        sale2.setDateTime(LocalDateTime.MAX);

        saleList.add(sale1);
        saleList.add(sale2);

        //when
        var methodResult = PurchaseSaleUtil
                .calculateProfitsFromSales(purchaseList, saleList, PRODUCT_NAME);

        //then
        var newSale1 = methodResult.stream()
                .filter(s -> s.getId().equals(1L))
                .findAny()
                .orElseThrow();
        var newSale2 = methodResult.stream()
                .filter(s -> s.getId().equals(2L))
                .findAny()
                .orElseThrow();

        var absoluteProfit = sale1.getAmount() * (sale1.getPrice() - sale1.getCommission()) -
                sale1.getAmount() * (purchase1.getPrice() + purchase1.getCommission());
        assertEquals(absoluteProfit, newSale1.getAbsoluteProfit(), 0.01);

        var fullPriceOfSelling = (sale1.getPrice() - sale1.getCommission()) * sale1.getAmount();
        var relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, newSale1.getRelativeProfit(), 0.01);

        absoluteProfit = sale2.getAmount() * (sale2.getPrice() - sale2.getCommission()) -
                (purchase1.getAmount() - sale1.getAmount()) * (purchase1.getPrice() + purchase1.getCommission()) -
                (sale2.getAmount() - (purchase1.getAmount() - sale1.getAmount())) *
                        (purchase2.getPrice() + purchase2.getCommission());
        assertEquals(absoluteProfit, newSale2.getAbsoluteProfit(), 0.01);

        fullPriceOfSelling = (sale2.getPrice() - sale2.getCommission()) * sale2.getAmount();
        relativeProfit = (fullPriceOfSelling / (fullPriceOfSelling - absoluteProfit) - 1) * 100;
        assertEquals(relativeProfit, newSale2.getRelativeProfit(), 0.01);
    }
}
