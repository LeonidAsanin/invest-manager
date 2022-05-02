package org.lennardjones.investmanager.service.accountPageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.model.PurchaseTotal;
import org.lennardjones.investmanager.model.SaleTotal;
import org.lennardjones.investmanager.service.AccountPageService;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DefineTotalsTests {
    @Mock
    LoggedUserManagementService loggedUserManagementServiceMock;
    @Mock
    PurchaseService purchaseServiceMock;
    @Mock
    SaleService saleServiceMock;
    @Mock
    Model modelMock;

    AccountPageService accountPageService;
    User user;

    @BeforeEach
    void before() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                purchaseServiceMock, saleServiceMock);

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
    }

    @Test
    void testDefaultPurchaseTotals() {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;

        accountPageService.defineTotals(user.getUsername(), chosenTableToSee, modelMock);

        Mockito.verify(modelMock)
                .addAttribute("purchaseTotal", new PurchaseTotal(0, 0));
        Mockito.verify(modelMock)
                .addAttribute("saleTotal",
                              new SaleTotal(0, 0, 0, 0));
    }

    @Test
    void testPurchaseTotals() {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;

        List<Purchase> purchaseList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            var purchase = new Purchase();
            purchase.setAmount(1);
            purchase.setPrice((double) 10);
            purchase.setCommission((double) 1);
            purchaseList.add(purchase);
        }

        Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                        .thenReturn(purchaseList);

        accountPageService.defineTotals(user.getUsername(), chosenTableToSee, modelMock);

        Mockito.verify(modelMock)
                .addAttribute("purchaseTotal",
                        new PurchaseTotal(50, 5));
        Mockito.verify(modelMock)
                .addAttribute("saleTotal",
                        new SaleTotal( 0, 0,0, 0));
    }

    @Test
    void testDefaultSaleTotals() {
        var chosenTableToSee = ChosenTableToSee.SALE;

        accountPageService.defineTotals(user.getUsername(), chosenTableToSee, modelMock);

        Mockito.verify(modelMock)
                .addAttribute("purchaseTotal", new PurchaseTotal(0, 0));
        Mockito.verify(modelMock)
                .addAttribute("saleTotal",
                        new SaleTotal(0, 0, 0, 0));
    }

    @Test
    void testSaleTotals() {
        var chosenTableToSee = ChosenTableToSee.SALE;

        List<Sale> saleList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var sale = new Sale();
            sale.setAmount(1);
            sale.setPrice((double) 11);
            sale.setCommission((double) 1);
            sale.setAbsoluteProfit((double) 5);
            sale.setRelativeProfit((double) 100);
            saleList.add(sale);
        }

        Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                .thenReturn(saleList);

        accountPageService.defineTotals(user.getUsername(), chosenTableToSee, modelMock);

        Mockito.verify(modelMock)
                .addAttribute("purchaseTotal",
                        new PurchaseTotal(0, 0));
        Mockito.verify(modelMock)
                .addAttribute("saleTotal",
                        new SaleTotal( 110, 10,50, 100));
    }
}
