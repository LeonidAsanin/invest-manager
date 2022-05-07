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
import org.lennardjones.investmanager.util.SortType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DefineTableTests {
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
    void setup() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                purchaseServiceMock, saleServiceMock);

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
    }

    @Nested
    class PurchaseTable {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.PURCHASE;

        @Test
        void testTableWithoutFilters() {
            //given
            var page = 1;
            var sortType = SortType.DATE;
            var sortDirection = Sort.Direction.ASC;
            List<Purchase> purchaseList = new ArrayList<>();
            purchaseList.add(new Purchase());
            List<Sale> saleList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(page);
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername(), page, sortType, sortDirection))
                    .thenReturn(purchaseList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "", "", modelMock);

            //then
            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }

        @Test
        void testTableWithFilterByName() {
            //given
            var page = 1;
            var sortType = SortType.NAME;
            var sortDirection = Sort.Direction.DESC;

            List<Purchase> purchaseList = new ArrayList<>();
            var purchase1 = new Purchase();
            purchase1.setName("awa");
            var purchase2 = new Purchase();
            purchase2.setName("aaw");
            Collections.addAll(purchaseList, purchase1, purchase2);

            List<Sale> saleList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(page);
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "w", page,
                            sortType, sortDirection))
                    .thenReturn(purchaseList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "w", "", modelMock);

            //then
            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }

        @Test
        void testTableWithFilterByTag() {
            //given
            var page = 1;
            var sortType = SortType.TAG_DATE;
            var sortDirection = Sort.Direction.ASC;

            List<Purchase> purchaseList = new ArrayList<>();
            var purchase1 = new Purchase();
            purchase1.setTag("tag");
            var purchase2 = new Purchase();
            purchase2.setTag("otherTag");
            Collections.addAll(purchaseList, purchase1, purchase2);

            List<Sale> saleList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(page);
            Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername(), page,
                            sortType, sortDirection))
                    .thenReturn(purchaseList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "", "tag", modelMock);

            //then
            purchaseList = new ArrayList<>();
            var purchase = new Purchase();
            purchase.setTag("tag");
            purchaseList.add(purchase);

            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }

        @Test
        void testTableWithFilterByNameAndTag() {
            //given
            var page = 1;
            var sortType = SortType.TAG_DATE;
            var sortDirection = Sort.Direction.DESC;

            List<Purchase> purchaseList = new ArrayList<>();
            var purchase1 = new Purchase();
            purchase1.setName("awa");
            purchase1.setTag("tag");
            var purchase2 = new Purchase();
            purchase2.setName("aaw");
            purchase2.setTag("otherTag");
            Collections.addAll(purchaseList, purchase1, purchase2);

            List<Sale> saleList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getPurchasePage())
                    .thenReturn(page);
            Mockito.when(purchaseServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "w", page,
                            sortType, sortDirection))
                    .thenReturn(purchaseList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "w", "tag", modelMock);

            //then
            purchaseList = new ArrayList<>();
            var purchase = new Purchase();
            purchase.setName("awa");
            purchase.setTag("tag");
            purchaseList.add(purchase);

            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }
    }

    @Nested
    class SaleTable {
        ChosenTableToSee chosenTableToSee = ChosenTableToSee.SALE;

        @Test
        void testTableWithoutFilters() {
            //given
            var page = 2;
            var sortType = SortType.NONE;
            var sortDirection = Sort.Direction.ASC;
            List<Purchase> purchaseList = Collections.emptyList();
            List<Sale> saleList = new ArrayList<>();
            saleList.add(new Sale());

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(page);
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername(), page, sortType, sortDirection))
                    .thenReturn(saleList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "", "", modelMock);

            //then
            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }

        @Test
        void testTableWithFilterByName() {
            //given
            var page = 3;
            var sortType = SortType.TAG_DATE;
            var sortDirection = Sort.Direction.ASC;

            List<Sale> saleList = new ArrayList<>();
            var sale1 = new Sale();
            sale1.setName("awa");
            var sale2 = new Sale();
            sale2.setName("aaw");
            Collections.addAll(saleList, sale1, sale2);

            List<Purchase> purchaseList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(page);
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "w", page,
                            sortType, sortDirection))
                    .thenReturn(saleList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "w", "", modelMock);


            //then
            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }

        @Test
        void testTableWithFilterByTag() {
            //given
            var page = 10;
            var sortType = SortType.TAG_DATE;
            var sortDirection = Sort.Direction.ASC;

            List<Sale> saleList = new ArrayList<>();
            var sale1 = new Sale();
            sale1.setTag("tag");
            var sale2 = new Sale();
            sale2.setTag("otherTag");
            Collections.addAll(saleList, sale1, sale2);

            List<Purchase> purchaseList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(page);
            Mockito.when(saleServiceMock.getListByUsername(user.getUsername(), page,
                            sortType, sortDirection))
                    .thenReturn(saleList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "", "tag", modelMock);

            //then
            saleList = new ArrayList<>();
            var sale = new Sale();
            sale.setTag("tag");
            saleList.add(sale);

            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }

        @Test
        void testTableWithFilterByNameAndTag() {
            //given
            var page = 1;
            var sortType = SortType.TAG_DATE;
            var sortDirection = Sort.Direction.DESC;

            List<Sale> saleList = new ArrayList<>();
            var sale1 = new Sale();
            sale1.setName("awa");
            sale1.setTag("tag");
            var sale2 = new Sale();
            sale2.setName("aaw");
            sale2.setTag("otherTag");
            Collections.addAll(saleList, sale1, sale2);

            List<Purchase> purchaseList = Collections.emptyList();

            //when
            Mockito.when(loggedUserManagementServiceMock.getSortType())
                    .thenReturn(sortType);
            Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                    .thenReturn(sortDirection);
            Mockito.when(loggedUserManagementServiceMock.getSalePage())
                    .thenReturn(page);
            Mockito.when(saleServiceMock.getListByUsernameContainingSubstring(user.getUsername(), "w", page,
                            sortType, sortDirection))
                    .thenReturn(saleList);
            accountPageService.defineTable(user.getUsername(), chosenTableToSee, "w", "tag", modelMock);

            //then
            saleList = new ArrayList<>();
            var sale = new Sale();
            sale.setName("awa");
            sale.setTag("tag");
            saleList.add(sale);

            Mockito.verify(modelMock)
                    .addAttribute("purchaseList", purchaseList);
            Mockito.verify(modelMock)
                    .addAttribute("saleList", saleList);
        }
    }
}
