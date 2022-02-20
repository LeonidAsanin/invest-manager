package org.lennardjones.investmanager.util.purchaseSaleUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class CalculateBenefitsFromSalesTest {
    List<Purchase> purchaseList;
    List<Sale> saleList;
    String productName = "test product";

    @BeforeEach
    public void before() {
        purchaseList = new LinkedList<>();
        saleList = new LinkedList<>();
    }

    @Test
    public void methodDoesNotAffectInputLists() {
        var defaultPurchaseId = 1L;
        var defaultPurchaseOwner = new Account();
        var defaultPurchaseDate = LocalDate.MIN;
        var defaultPurchaseName = productName;
        var defaultPurchasePrice = 10;
        var defaultPurchaseAmount = 5;
        var defaultPurchaseCommission = 1;
        var defaultPurchaseCurrentPrice = 50;

        var purchase = new Purchase();
            purchase.setId(defaultPurchaseId);
            purchase.setOwner(defaultPurchaseOwner);
            purchase.setDate(defaultPurchaseDate);
            purchase.setName(defaultPurchaseName);
            purchase.setPrice(defaultPurchasePrice);
            purchase.setAmount(defaultPurchaseAmount);
            purchase.setCommission(defaultPurchaseCommission);
            purchase.setCurrentPrice(defaultPurchaseCurrentPrice);

        var defaultSaleId = 2L;
        var defaultSeller= new Account();
        var defaultSaleDate = LocalDate.MAX;
        var defaultSaleName = productName;
        var defaultSalePrice = 22;
        var defaultSaleAmount = 3;
        var defaultSaleCommission = 2;
        var defaultSaleAbsoluteBenefit = 100;
        var defaultSaleRelativeBenefit = 0.5;

        var sale = new Sale();
            sale.setId(defaultSaleId);
            sale.setSeller(defaultSeller);
            sale.setDate(defaultSaleDate);
            sale.setName(defaultSaleName);
            sale.setPrice(defaultSalePrice);
            sale.setAmount(defaultSaleAmount);
            sale.setCommission(defaultSaleCommission);
            sale.setAbsoluteBenefit(defaultSaleAbsoluteBenefit);
            sale.setRelativeBenefit(defaultSaleRelativeBenefit);

        purchaseList.add(purchase);
        saleList.add(sale);

        PurchaseSaleUtil.calculateBenefitsFromSales(purchaseList, saleList, productName);

        Assertions.assertEquals((long) purchase.getId(), defaultPurchaseId);
        Assertions.assertEquals(purchase.getOwner(), defaultPurchaseOwner);
        Assertions.assertEquals(purchase.getDate(), defaultPurchaseDate);
        Assertions.assertEquals(purchase.getName(), defaultPurchaseName);
        Assertions.assertEquals(purchase.getPrice(), defaultPurchasePrice);
        Assertions.assertEquals(purchase.getAmount(), defaultPurchaseAmount);
        Assertions.assertEquals(purchase.getCommission(), defaultPurchaseCommission);
        Assertions.assertEquals(purchase.getCurrentPrice(), defaultPurchaseCurrentPrice);

        Assertions.assertEquals((long) sale.getId(), defaultSaleId);
        Assertions.assertEquals(sale.getSeller(), defaultSeller);
        Assertions.assertEquals(sale.getDate(), defaultSaleDate);
        Assertions.assertEquals(sale.getName(), defaultSaleName);
        Assertions.assertEquals(sale.getPrice(), defaultSalePrice);
        Assertions.assertEquals(sale.getAmount(), defaultSaleAmount);
        Assertions.assertEquals(sale.getCommission(), defaultSaleCommission);
        Assertions.assertEquals(sale.getAbsoluteBenefit(), defaultSaleAbsoluteBenefit);
        Assertions.assertEquals(sale.getRelativeBenefit(), defaultSaleRelativeBenefit);
    }

    @Test
    public void oneSaleWithAmountLessThanPurchaseAmount() {
        var purchase = new Purchase();
            purchase.setName(productName);
            purchase.setAmount(5);
            purchase.setPrice(10);
            purchase.setCommission(1);
        purchaseList.add(purchase);

        var sale = new Sale();
            sale.setName(productName);
            sale.setAmount(3);
            sale.setPrice(22);
            sale.setCommission(1);
        saleList.add(sale);

        var methodResult = PurchaseSaleUtil
                .calculateBenefitsFromSales(purchaseList, saleList, productName);

        Assertions.assertEquals(1, methodResult.size());
        Assertions.assertEquals(productName, methodResult.get(0).getName());
        Assertions.assertEquals(sale.getAmount(), methodResult.get(0).getAmount());
        Assertions.assertEquals(sale.getCommission(), methodResult.get(0).getCommission());
        Assertions.assertEquals(sale.getAmount() * (sale.getPrice() - sale.getCommission()) -
                                sale.getAmount() * (purchase.getPrice() + purchase.getCommission()),
                                methodResult.get(0).getAbsoluteBenefit());
    }

    @Test
    public void twoSalesWithAmountLessThanPurchaseAmount() {
        var purchase = new Purchase();
        purchase.setName(productName);
        purchase.setAmount(5);
        purchase.setPrice(10);
        purchase.setCommission(1);
        purchaseList.add(purchase);

        var sale1 = new Sale();
        sale1.setName(productName);
        sale1.setAmount(3);
        sale1.setPrice(22);
        sale1.setCommission(1);

        var sale2 = new Sale();
        sale2.setName(productName);
        sale2.setAmount(1);
        sale2.setPrice(22);
        sale2.setCommission(1);

        saleList.add(sale1);
        saleList.add(sale2);

        var methodResult = PurchaseSaleUtil
                .calculateBenefitsFromSales(purchaseList, saleList, productName);

        Assertions.assertEquals(2, methodResult.size());
        Assertions.assertEquals(productName, methodResult.get(0).getName());
        Assertions.assertEquals(productName, methodResult.get(1).getName());
        Assertions.assertEquals(sale1.getAmount(), methodResult.get(0).getAmount());
        Assertions.assertEquals(sale1.getAmount(), methodResult.get(0).getAmount());
        Assertions.assertEquals(sale2.getCommission(), methodResult.get(1).getCommission());
        Assertions.assertEquals(sale2.getCommission(), methodResult.get(1).getCommission());
        Assertions.assertEquals(sale1.getAmount() * (sale1.getPrice() - sale1.getCommission()) -
                sale1.getAmount() * (purchase.getPrice() + purchase.getCommission()),
                methodResult.get(0).getAbsoluteBenefit());
        Assertions.assertEquals(sale2.getAmount() * (sale2.getPrice() - sale2.getCommission()) -
                sale2.getAmount() * (purchase.getPrice() + purchase.getCommission()),
                methodResult.get(1).getAbsoluteBenefit());
    }

    @Test
    public void oneSaleWithAmountEqualToPurchaseAmount() {
        var purchase = new Purchase();
        purchase.setName(productName);
        purchase.setAmount(5);
        purchase.setPrice(10);
        purchase.setCommission(1);
        purchaseList.add(purchase);

        var sale = new Sale();
        sale.setName(productName);
        sale.setAmount(5);
        sale.setPrice(22);
        sale.setCommission(1);
        saleList.add(sale);

        var methodResult = PurchaseSaleUtil
                .calculateBenefitsFromSales(purchaseList, saleList, productName);

        Assertions.assertEquals(1, methodResult.size());
        Assertions.assertEquals(productName, methodResult.get(0).getName());
        Assertions.assertEquals(sale.getAmount(), methodResult.get(0).getAmount());
        Assertions.assertEquals(sale.getCommission(), methodResult.get(0).getCommission());
        Assertions.assertEquals(sale.getAmount() * (sale.getPrice() - sale.getCommission()) -
                sale.getAmount() * (purchase.getPrice() + purchase.getCommission()),
                methodResult.get(0).getAbsoluteBenefit());
    }

    @Test
    public void oneSaleWithAmountGreaterThanPurchaseAmount() {
        var purchase1 = new Purchase();
        purchase1.setName(productName);
        purchase1.setAmount(5);
        purchase1.setPrice(10);
        purchase1.setCommission(1);

        var purchase2 = new Purchase();
        purchase2.setName(productName);
        purchase2.setAmount(5);
        purchase2.setPrice(20);
        purchase2.setCommission(2);

        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        var sale = new Sale();
        sale.setName(productName);
        sale.setAmount(7);
        sale.setPrice(22);
        sale.setCommission(1);
        saleList.add(sale);

        var methodResult = PurchaseSaleUtil
                .calculateBenefitsFromSales(purchaseList, saleList, productName);

        Assertions.assertEquals(1, methodResult.size());
        Assertions.assertEquals(productName, methodResult.get(0).getName());
        Assertions.assertEquals(sale.getAmount(), methodResult.get(0).getAmount());
        Assertions.assertEquals(sale.getCommission(), methodResult.get(0).getCommission());
        Assertions.assertEquals(sale.getAmount() * (sale.getPrice() - sale.getCommission()) -
                purchase1.getAmount() * (purchase1.getPrice() + purchase1.getCommission()) -
                (sale.getAmount() - purchase1.getAmount()) * (purchase2.getPrice() + purchase2.getCommission()),
                methodResult.get(0).getAbsoluteBenefit());
    }

}
