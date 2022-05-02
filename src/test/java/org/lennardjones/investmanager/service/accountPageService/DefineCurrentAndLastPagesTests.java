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
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DefineCurrentAndLastPagesTests {
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
    String filterByName;
    String filterByTag;

    @BeforeEach
    void before() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                purchaseServiceMock, saleServiceMock);

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        filterByName = "filterByName";
        filterByTag = "filterByTag";
    }

    @Nested
    class PurchasePage {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.PURCHASE;
        @Test
        void testEmptyPage() {
            var currentPage = 0;

            List<Purchase> purchaseList = new ArrayList<>();

            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(currentPage);
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(purchaseList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    filterByName, filterByTag, modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 0);
        }

        @Test
        void testPageWithFilterByNameAndTag() {
            var currentPage = 0;

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                var purchase = new Purchase();
                purchase.setId((long) i);
                purchase.setOwner(user);
                purchase.setName("purchase_" + filterByName + "_" + i);
                purchase.setTag(filterByTag);
                purchaseList.add(purchase);
            }

            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(currentPage);
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(purchaseList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    filterByName, filterByTag, modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 2);
        }

        @Test
        void testPageWithFilterByName() {
            var currentPage = 0;

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                var purchase = new Purchase();
                purchase.setId((long) i);
                purchase.setOwner(user);
                purchase.setName("purchase_" + filterByName + "_" + i);
                purchase.setTag("tag");
                purchaseList.add(purchase);
            }

            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(currentPage);
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(purchaseList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    filterByName, "", modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 1);
        }

        @Test
        void testPageWithFilterByTag() {
            var currentPage = 0;

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 43; i++) {
                var purchase = new Purchase();
                purchase.setId((long) i);
                purchase.setOwner(user);
                purchase.setName("purchase_" + i);
                if (i % 2 != 0) {
                    purchase.setTag(filterByTag);
                } else {
                    purchase.setTag("other");
                }
                purchaseList.add(purchase);
            }

            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(currentPage);
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(purchaseList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    "", filterByTag, modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 3);
        }

        @Test
        void testPageWithoutFilters() {
            var currentPage = 0;

            List<Purchase> purchaseList = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                var purchase = new Purchase();
                purchase.setId((long) i);
                purchase.setOwner(user);
                purchase.setName("purchase_" + i);
                purchase.setTag("tag_" + i);
                purchaseList.add(purchase);
            }

            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(currentPage);
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(purchaseList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    "", "", modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 4);
        }
    }

    @Nested
    class SalePage {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.SALE;

        @Test
        void testEmptyPage() {
            var currentPage = 0;

            List<Sale> saleList = new ArrayList<>();

            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(currentPage);
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(saleList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    filterByName, filterByTag, modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 0);
        }

        @Test
        void testPageWithFilterByNameAndTag() {
            var currentPage = 0;

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                var sale = new Sale();
                sale.setId((long) i);
                sale.setSeller(user);
                sale.setName("purchase_" + filterByName + "_" + i);
                sale.setTag(filterByTag);
                saleList.add(sale);
            }

            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(currentPage);
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(saleList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    filterByName, filterByTag, modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 2);
        }

        @Test
        void testPageWithFilterByName() {
            var currentPage = 0;

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                var sale = new Sale();
                sale.setId((long) i);
                sale.setSeller(user);
                sale.setName("purchase_" + filterByName + "_" + i);
                sale.setTag("tag");
                saleList.add(sale);
            }

            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(currentPage);
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), filterByName))
                    .thenReturn(saleList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    filterByName, "", modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 1);
        }

        @Test
        void testPageWithFilterByTag() {
            var currentPage = 0;

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 43; i++) {
                var sale = new Sale();
                sale.setId((long) i);
                sale.setSeller(user);
                sale.setName("purchase_" + i);
                if (i % 2 != 0) {
                    sale.setTag(filterByTag);
                } else {
                    sale.setTag("other");
                }
                saleList.add(sale);
            }

            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(currentPage);
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(saleList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    "", filterByTag, modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 3);
        }

        @Test
        void testPageWithoutFilters() {
            var currentPage = 0;

            List<Sale> saleList = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                var sale = new Sale();
                sale.setId((long) i);
                sale.setSeller(user);
                sale.setName("purchase_" + i);
                sale.setTag("tag_" + i);
                saleList.add(sale);
            }

            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(currentPage);
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                    .thenReturn(saleList);

            accountPageService.defineCurrentAndLastPages(user.getUsername(), chosenTableToSee,
                    "", "", modelMock);

            Mockito.verify(modelMock)
                    .addAttribute("currentPage", currentPage +  1);
            Mockito.verify(modelMock)
                    .addAttribute("lastPage", 4);
        }
    }
}
