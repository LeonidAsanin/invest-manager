package org.lennardjones.investmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.repository.SaleRepository;
import org.lennardjones.investmanager.util.SortType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTests {
    @Mock
    SaleRepository saleRepositoryMock;
    @Mock
    ProductService productServiceMock;

    SaleService saleService;

    @BeforeEach
    void before() {
        saleService = new SaleService(saleRepositoryMock, productServiceMock);
    }

    @Test
    void getListByUsernameTest() {
        assertAll(
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("default");
                    sale.setSeller(user);
                    saleList.add(sale);
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username"))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService.getListByUsername("username");
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("nonSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            0, SortType.NONE, Sort.Direction.DESC);
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("nameSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC,
                            "name", "dateTime", "id");
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            1, SortType.NAME, Sort.Direction.ASC);
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("dateSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(2, 10, Sort.Direction.DESC,
                            "dateTime", "name", "id");
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            2, SortType.DATE, Sort.Direction.DESC);
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("tagNameSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(3, 10, Sort.Direction.ASC,
                            "tag", "name", "dateTime", "id");
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            3, SortType.TAG_NAME, Sort.Direction.ASC);
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("tagDateSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(4, 10, Sort.Direction.DESC,
                            "tag", "dateTime", "name", "id");
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            4, SortType.TAG_DATE, Sort.Direction.DESC);
                    assertEquals(saleListPage, returnedPurchaseListPage);
                }
        );
    }

    @Test
    void getListByUsernameAndProductNameTest() {
        List<Sale> saleList = new ArrayList<>();
        var sale = new Sale();
        var user = new User();
        user.setUsername("username");
        sale.setName("productName");
        sale.setSeller(user);
        saleList.add(sale);
        Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName("username", "productName"))
                .thenReturn(saleList);
        var returnedPurchaseList = saleService.getListByUsernameAndProductName("username",
                "productName");
        assertEquals(saleList, returnedPurchaseList);
    }

    @Test
    void getListByUsernameContainingSubstringTest() {
        assertAll(
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringDefault");
                    sale.setSeller(user);
                    saleList.add(sale);
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username", "substring"))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring");
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringNoneSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    0, SortType.NONE, Sort.Direction.DESC);
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringNameSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC,
                            "name", "dateTime", "id");
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    1, SortType.NAME, Sort.Direction.ASC);
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringDateSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(2, 10, Sort.Direction.DESC,
                            "dateTime", "name", "id");
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    2, SortType.DATE, Sort.Direction.DESC);
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringTagNameSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(3, 10, Sort.Direction.ASC,
                            "tag", "name", "dateTime", "id");
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    3, SortType.TAG_NAME, Sort.Direction.ASC);
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringTagNameSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(4, 10, Sort.Direction.DESC,
                            "tag", "dateTime", "name", "id");
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    4, SortType.TAG_DATE, Sort.Direction.DESC);
                    assertEquals(saleList, returnedPurchaseList);
                }
        );
    }

    @Test
    void saveTest() {
        var user = new User();
        user.setId(1L);
        var sale = new Sale();
        sale.setSeller(user);

        var authentication = new AuthenticationForTest(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        saleService.save(sale);
        Mockito.verify(saleRepositoryMock).save(sale);
        Mockito.verify(productServiceMock).setDataChanged();

        var otherUser = new User();
        otherUser.setId(2L);
        var otherSale= new Sale();
        otherSale.setSeller(otherUser);
        assertThrows(RuntimeException.class, () -> saleService.save(otherSale));
    }

    @Test
    void deleteByIdTest() {
        var user1 = new User();
        user1.setId(1L);
        var user2 = new User();
        user2.setId(2L);

        var sale1 = new Sale();
        sale1.setSeller(user1);
        var sale2 = new Sale();
        sale2.setSeller(user2);

        var authentication = new AuthenticationForTest(user1);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(saleRepositoryMock.findById(1L))
                .thenReturn(Optional.of(sale1));
        Mockito.when(saleRepositoryMock.findById(2L))
                .thenReturn(Optional.of(sale2));

        saleService.deleteById(1L);
        Mockito.verify(saleRepositoryMock).deleteById(1L);
        Mockito.verify(productServiceMock).setDataChanged();

        assertThrows(RuntimeException.class, () -> saleService.deleteById(2L));
    }

    @Test
    void updateTagsByUsernameAndProductNameTest() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");

        var authentication = new AuthenticationForTest(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Sale> saleList = new ArrayList<>();
        var sale1 = new Sale();
        var sale2 = new Sale();
        sale1.setName("productName");
        sale2.setName("productName");
        sale1.setTag("");
        sale2.setTag("");
        sale1.setSeller(user);
        sale2.setSeller(user);
        saleList.add(sale1);
        saleList.add(sale2);

        Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName("username", "productName"))
                .thenReturn(saleList);

        saleService.updateTagsByUsernameAndProductName("username", "productName", "tag");

        sale1.setTag("tag");
        sale2.setTag("tag");
        Mockito.verify(saleRepositoryMock).save(sale1);
        Mockito.verify(saleRepositoryMock).save(sale2);
        Mockito.verify(productServiceMock, Mockito.times(2)).setDataChanged();
    }

    @Test
    void getAnyByUsernameAndProductNameTest() {
        List<Sale> saleList = new ArrayList<>();
        var sale = new Sale();
        var user = new User();
        user.setUsername("username");
        sale.setName("productName");
        sale.setSeller(user);
        saleList.add(sale);
        Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName("username", "productName"))
                .thenReturn(saleList);
        var returnedOptionalPurchase = saleService
                .getAnyByUsernameAndProductName("username", "productName");
        assertTrue(returnedOptionalPurchase.isPresent());
        assertEquals("username", returnedOptionalPurchase.get().getSeller().getUsername());
        assertEquals("productName", returnedOptionalPurchase.get().getName());

        returnedOptionalPurchase = saleService
                .getAnyByUsernameAndProductName("otherUsername", "productName");
        assertFalse(returnedOptionalPurchase.isPresent());

        returnedOptionalPurchase = saleService
                .getAnyByUsernameAndProductName("username", "otherProductName");
        assertFalse(returnedOptionalPurchase.isPresent());

        returnedOptionalPurchase = saleService
                .getAnyByUsernameAndProductName("otherUsername", "otherProductName");
        assertFalse(returnedOptionalPurchase.isPresent());
    }
}
