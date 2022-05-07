package org.lennardjones.investmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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

import java.time.LocalDateTime;
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
    @Mock
    PurchaseService purchaseServiceMock;

    SaleService saleService;

    @BeforeEach
    void setup() {
        saleService = new SaleService(saleRepositoryMock, productServiceMock, purchaseServiceMock);
    }

    @Test
    void getListByUsernameTest() {
        assertAll(
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("default");
                    sale.setSeller(user);
                    saleList.add(sale);

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username"))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService.getListByUsername("username");

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("nonSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            0, SortType.NONE, Sort.Direction.DESC);

                    //then
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("nameSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC,
                            "name", "dateTime", "id");

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            1, SortType.NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("dateSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(2, 10, Sort.Direction.DESC,
                            "dateTime", "name", "id");

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            2, SortType.DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("tagNameSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(3, 10, Sort.Direction.ASC,
                            "tag", "name", "dateTime", "id");

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            3, SortType.TAG_NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(saleListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Sale> saleListPage = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("tagDateSort");
                    sale.setSeller(user);
                    saleListPage.add(sale);
                    var pageRequest = PageRequest.of(4, 10, Sort.Direction.DESC,
                            "tag", "dateTime", "name", "id");

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_Username("username", pageRequest))
                            .thenReturn(saleListPage);
                    var returnedPurchaseListPage = saleService.getListByUsername("username",
                            4, SortType.TAG_DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(saleListPage, returnedPurchaseListPage);
                }
        );
    }

    @Test
    void getListByUsernameAndProductNameTest() {
        //given
        List<Sale> saleList = new ArrayList<>();
        var sale = new Sale();
        var user = new User();
        user.setUsername("username");
        sale.setName("productName");
        sale.setSeller(user);
        saleList.add(sale);

        //when
        Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName("username", "productName"))
                .thenReturn(saleList);
        var returnedPurchaseList = saleService.getListByUsernameAndProductName("username",
                "productName");

        //then
        assertEquals(saleList, returnedPurchaseList);
    }

    @Test
    void getListByUsernameContainingSubstringTest() {
        assertAll(
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringDefault");
                    sale.setSeller(user);
                    saleList.add(sale);

                    //when
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username", "substring"))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring");

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringNoneSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

                    //when
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    0, SortType.NONE, Sort.Direction.DESC);

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringNameSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC,
                            "name", "dateTime", "id");

                    //when
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    1, SortType.NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringDateSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(2, 10, Sort.Direction.DESC,
                            "dateTime", "name", "id");

                    //when
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    2, SortType.DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringTagNameSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(3, 10, Sort.Direction.ASC,
                            "tag", "name", "dateTime", "id");

                    //when
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    3, SortType.TAG_NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productNameContainingSubstringTagNameSort");
                    sale.setSeller(user);
                    saleList.add(sale);
                    var pageRequest = PageRequest.of(4, 10, Sort.Direction.DESC,
                            "tag", "dateTime", "name", "id");

                    //when
                    Mockito.when(saleRepositoryMock
                                    .findBySeller_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(saleList);
                    var returnedPurchaseList = saleService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    4, SortType.TAG_DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(saleList, returnedPurchaseList);
                }
        );
    }

    @Test
    void saveTest() {
        assertAll(
                () -> {
                    //given
                    var user = new User();
                    user.setId(1L);
                    var sale = new Sale();
                    sale.setSeller(user);

                    var authentication = new AuthenticationForServiceTests(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //when
                    saleService.save(sale);

                    //then
                    Mockito.verify(saleRepositoryMock).save(sale);
                    Mockito.verify(productServiceMock).setDataChanged();
                },
                () -> {
                    //and given
                    var otherUser = new User();
                    otherUser.setId(2L);
                    var otherSale = new Sale();
                    otherSale.setSeller(otherUser);

                    //when
                    Executable failedSave = () -> saleService.save(otherSale);

                    //then
                    assertThrows(RuntimeException.class, failedSave);
                }
        );
    }

    @Test
    void updateAllByNameTest() {
        //given
        var user = new User();
        user.setId(1L);
        user.setUsername("username");

        var sale = new Sale();
        sale.setId(1L);
        sale.setSeller(user);
        sale.setName("productName");
        sale.setTag("tag");
        sale.setDateTime(LocalDateTime.MAX);
        sale.setAmount(1);
        sale.setPrice(4.0);
        sale.setCommission(1.0);
        sale.setAbsoluteProfit(0.);
        sale.setRelativeProfit(0.);

        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setOwner(user);
        purchase.setName("productName");
        purchase.setTag("tag");
        purchase.setDateTime(LocalDateTime.MIN);
        purchase.setAmount(1);
        purchase.setPrice(1.0);
        purchase.setCommission(.5);

        List<Sale> saleList = new ArrayList<>();
        saleList.add(sale);

        List<Purchase> purchaseList = new ArrayList<>();
        purchaseList.add(purchase);

        var authentication = new AuthenticationForServiceTests(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //when
        Mockito.when(purchaseServiceMock.getListByUsernameAndProductName(user.getUsername(), "productName"))
                .thenReturn(purchaseList);
        Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName(user.getUsername(), "productName"))
                .thenReturn(saleList);
        saleService.updateProfitsByName("productName");

        //then
        var updatedSale = (Sale) sale.clone();
        updatedSale.setAbsoluteProfit(1.5);
        updatedSale.setRelativeProfit(100.);

        Mockito.verify(saleRepositoryMock)
                .save(updatedSale);
        Mockito.verify(productServiceMock)
                .setDataChanged();
    }

    @Test
    void deleteByIdTest() {
        assertAll(
                () -> {
                    //given
                    var user1 = new User();
                    user1.setId(1L);
                    var user2 = new User();
                    user2.setId(2L);

                    var sale1 = new Sale();
                    sale1.setSeller(user1);
                    var sale2 = new Sale();
                    sale2.setSeller(user2);

                    var authentication = new AuthenticationForServiceTests(user1);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //when
                    Mockito.when(saleRepositoryMock.findById(1L))
                            .thenReturn(Optional.of(sale1));
                    Mockito.when(saleRepositoryMock.findById(2L))
                            .thenReturn(Optional.of(sale2));
                    saleService.deleteById(1L);

                    //then
                    Mockito.verify(saleRepositoryMock)
                            .deleteById(1L);
                    Mockito.verify(productServiceMock)
                            .setDataChanged();
                },
                () -> {
                    //and given
                    var saleId = 2L;

                    //when
                    Executable failedDelete = () -> saleService.deleteById(saleId);

                    //then
                    assertThrows(RuntimeException.class, failedDelete);
                }
        );
    }

    @Test
    void updateTagsByUsernameAndProductNameTest() {
        //given
        var user = new User();
        user.setId(1L);
        user.setUsername("username");

        var authentication = new AuthenticationForServiceTests(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        List<Sale> saleList = new ArrayList<>();
        var sale1 = new Sale();
        var sale2 = new Sale();
        sale1.setId(1L);
        sale2.setId(2L);
        sale1.setName("productName");
        sale2.setName("productName");
        sale1.setTag("");
        sale2.setTag("");
        sale1.setSeller(user);
        sale2.setSeller(user);
        saleList.add(sale1);
        saleList.add(sale2);

        //when
        Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName("username", "productName"))
                .thenReturn(saleList);
        saleService.updateTagsByUsernameAndProductName("username", "productName", "tag");

        //then
        sale1.setTag("tag");
        sale2.setTag("tag");
        Mockito.verify(saleRepositoryMock)
                .save(sale1);
        Mockito.verify(saleRepositoryMock)
                .save(sale2);
        Mockito.verify(productServiceMock, Mockito.times(2))
                .setDataChanged();
    }

    @Test
    void getAnyByUsernameAndProductNameTest() {
        assertAll(
                () -> {
                    //given
                    List<Sale> saleList = new ArrayList<>();
                    var sale = new Sale();
                    var user = new User();
                    user.setUsername("username");
                    sale.setName("productName");
                    sale.setSeller(user);
                    saleList.add(sale);

                    //when
                    Mockito.when(saleRepositoryMock.findBySeller_UsernameAndName("username", "productName"))
                            .thenReturn(saleList);
                    var returnedOptionalPurchase = saleService
                            .getAnyByUsernameAndProductName("username", "productName");

                    //then
                    assertTrue(returnedOptionalPurchase.isPresent());
                    assertEquals("username", returnedOptionalPurchase.get()
                            .getSeller()
                            .getUsername());
                    assertEquals("productName", returnedOptionalPurchase.get()
                            .getName());
                },
                () -> {
                    //given
                    var username = "otherUsername";
                    var name = "productName";

                    //when
                    var returnedOptionalPurchase = saleService
                            .getAnyByUsernameAndProductName(username, name);

                    //then
                    assertFalse(returnedOptionalPurchase.isPresent());
                },
                () -> {
                    //given
                    var username = "username";
                    var name = "otherProductName";

                    //when
                    var returnedOptionalPurchase = saleService
                            .getAnyByUsernameAndProductName(username, name);

                    //then
                    assertFalse(returnedOptionalPurchase.isPresent());
                },
                () -> {
                    //given
                    var username = "otherUsername";
                    var name = "otherProductName";

                    //when
                    var returnedOptionalPurchase = saleService
                            .getAnyByUsernameAndProductName(username, name);

                    //then
                    assertFalse(returnedOptionalPurchase.isPresent());
                }
        );
    }
}
