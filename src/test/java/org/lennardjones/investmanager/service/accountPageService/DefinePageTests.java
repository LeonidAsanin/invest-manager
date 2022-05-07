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

    AccountPageService accountPageService;
    User user;

    @BeforeEach
    void setup() {
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
            //given
            var filterByName = "";
            var filterByTag = "";

            //when
            accountPageService.definePage(user.getUsername(), null, chosenTableToSee,
                    filterByName, filterByTag);

            //then
                //no errors
        }
        @Test
        void testFirstEmptyPage(){
            //given
            var page = "FIRST";
            var filterByName = "";
            var filterByTag = "";

            //when
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(Collections.emptyList());
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                            .thenReturn(2);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(0);
        }
        @Test
        void testLastPageWithoutFilters(){
            //given
            var page = "LAST";
            var filterByName = "";
            var filterByTag = "";

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                purchaseList.add(new Purchase());
            }

            //when
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(0);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(3);
        }
        @Test
        void testNextPageWithFilterByName(){
            //given
            var page = "NEXT";
            var filterByName = "filter";
            var filterByTag = "";

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                var purchase = new Purchase();
                purchase.setName(filterByName);
                purchaseList.add(purchase);
            }

            //when
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(1);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(2);
        }
        @Test
        void testPreviousPageWithFilterByTag(){
            //given
            var page = "PREVIOUS";
            var filterByName = "";
            var filterByTag = "tag";

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var purchase = new Purchase();
                if (i % 2 == 0) {
                    purchase.setTag(filterByTag);
                } else {
                    purchase.setTag("other");
                }
                purchaseList.add(purchase);
            }

            //when
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(3);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setPurchasePage(2);
        }
        @Test
        void testCurrentPageWithFilterByNameAndTag(){
            //given
            var page = "CURRENT";
            var filterByName = "filter";
            var filterByTag = "tag";

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var purchase = new Purchase();
                purchase.setName(filterByName);
                if (i % 2 == 0) {
                    purchase.setTag(filterByTag);
                } else {
                    purchase.setTag("other");
                }
                purchaseList.add(purchase);
            }

            //when
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(purchaseList);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(2);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
                //no errors
        }
    }

    @Nested
    class SalePage {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.SALE;

        @Test
        void testNullPage() {
            //given
            var filterByName = "";
            var filterByTag = "";

            //when
            accountPageService.definePage(user.getUsername(), null, chosenTableToSee,
                    filterByName, filterByTag);

            //then
                //no errors
        }
        @Test
        void testFirstEmptyPage(){
            //given
            var page = "FIRST";
            var filterByName = "";
            var filterByTag = "";

            //when
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(Collections.emptyList());
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(0);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setSalePage(0);
        }
        @Test
        void testLastPageWithoutFilters(){
            //given
            var page = "LAST";
            var filterByName = "";
            var filterByTag = "";

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                saleList.add(new Sale());
            }

            //when
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(4);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setSalePage(3);
        }
        @Test
        void testNextPageWithFilterByName(){
            //given
            var page = "NEXT";
            var filterByName = "filter";
            var filterByTag = "";

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                var sale = new Sale();
                sale.setName(filterByName);
                saleList.add(sale);
            }

            //when
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(2);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
                //no errors
        }
        @Test
        void testPreviousPageWithFilterByTag(){
            //given
            var page = "PREVIOUS";
            var filterByName = "";
            var filterByTag = "tag";

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var sale = new Sale();
                if (i % 2 == 0) {
                    sale.setTag(filterByTag);
                } else {
                    sale.setTag("other");
                }
                saleList.add(sale);
            }

            //when
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(0);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
                //no errors
        }
        @Test
        void testCurrentPageWithFilterByNameAndTag(){
            //given
            var page = "CURRENT";
            var filterByName = "filter";
            var filterByTag = "tag";

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 62; i++) {
                var sale = new Sale();
                sale.setName(filterByName);
                if (i % 2 == 0) {
                    sale.setTag(filterByTag);
                } else {
                    sale.setTag("other");
                }
                saleList.add(sale);
            }

            //when
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(saleList);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(20);
            accountPageService.definePage(user.getUsername(), page, chosenTableToSee,
                    filterByName, filterByTag);

            //then
            Mockito.verify(loggedUserManagementServiceMock)
                    .setSalePage(3);
        }
    }
}
