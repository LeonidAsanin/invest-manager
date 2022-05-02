package org.lennardjones.investmanager.service.accountPageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
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
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DefinePageTests {
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

    @Nested
    class PurchasePage {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.PURCHASE;

        @Test
        void testNullPage() {
            accountPageService.definePage(user.getUsername(), null, chosenTableToSee,
                    "", "");
        }
        @Test
        void testFirstEmptyPage(){
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(Collections.emptyList());
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                            .thenReturn(2);
            accountPageService.definePage(user.getUsername(), "FIRST", chosenTableToSee,
                    "", "");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(0);
        }
        @Test
        void testLastPageWithoutFilters(){
            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                purchaseList.add(new Purchase());
            }
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(0);
            accountPageService.definePage(user.getUsername(), "LAST", chosenTableToSee,
                    "", "");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(3);
        }
        @Test
        void testNextPageWithFilterByName(){
            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                var purchase = new Purchase();
                purchase.setName("filter");
                purchaseList.add(purchase);
            }
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "filter"))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(1);
            accountPageService.definePage(user.getUsername(), "NEXT", chosenTableToSee,
                    "filter", "");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(2);
        }
        @Test
        void testPreviousPageWithFilterByTag(){
            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var purchase = new Purchase();
                if (i % 2 == 0) {
                    purchase.setTag("tag");
                } else {
                    purchase.setTag("other");
                }
                purchaseList.add(purchase);
            }
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(3);
            accountPageService.definePage(user.getUsername(), "PREVIOUS", chosenTableToSee,
                    "", "tag");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(2);
        }
        @Test
        void testCurrentPageWithFilterByNameAndTag(){
            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var purchase = new Purchase();
                purchase.setName("filter");
                if (i % 2 == 0) {
                    purchase.setTag("tag");
                } else {
                    purchase.setTag("other");
                }
                purchaseList.add(purchase);
            }
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "filter"))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(2);
            accountPageService.definePage(user.getUsername(), "CURRENT", chosenTableToSee,
                    "filter", "tag");
        }
    }

    @Nested
    class SalePage {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.SALE;

        @Test
        void testNullPage() {
            accountPageService.definePage(user.getUsername(), null, chosenTableToSee,
                    "", "");
        }
        @Test
        void testFirstEmptyPage(){
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(Collections.emptyList());
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(0);
            accountPageService.definePage(user.getUsername(), "FIRST", chosenTableToSee,
                    "", "");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setSalePage(0);
        }
        @Test
        void testLastPageWithoutFilters(){
            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                saleList.add(new Sale());
            }
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(4);
            accountPageService.definePage(user.getUsername(), "LAST", chosenTableToSee,
                    "", "");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setSalePage(3);
        }
        @Test
        void testNextPageWithFilterByName(){
            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                var sale = new Sale();
                sale.setName("filter");
                saleList.add(sale);
            }
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "filter"))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(2);
            accountPageService.definePage(user.getUsername(), "NEXT", chosenTableToSee,
                    "filter", "");
        }
        @Test
        void testPreviousPageWithFilterByTag(){
            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var sale = new Sale();
                if (i % 2 == 0) {
                    sale.setTag("tag");
                } else {
                    sale.setTag("other");
                }
                saleList.add(sale);
            }
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(0);
            accountPageService.definePage(user.getUsername(), "PREVIOUS", chosenTableToSee,
                    "", "tag");
        }
        @Test
        void testCurrentPageWithFilterByNameAndTag(){
            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var sale = new Sale();
                sale.setName("filter");
                if (i % 2 == 0) {
                    sale.setTag("tag");
                } else {
                    sale.setTag("other");
                }
                saleList.add(sale);
            }
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "filter"))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(20);
            accountPageService.definePage(user.getUsername(), "CURRENT", chosenTableToSee,
                    "filter", "tag");
            Mockito.verify(loggedUserManagementServiceMock)
                    .setSalePage(3);
        }
    }
}
