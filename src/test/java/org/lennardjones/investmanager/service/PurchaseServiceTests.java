package org.lennardjones.investmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.repository.PurchaseRepository;
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
class PurchaseServiceTests {
    @Mock
    PurchaseRepository purchaseRepositoryMock;
    @Mock
    ProductService productServiceMock;

    PurchaseService purchaseService;

    @BeforeEach
    void setup() {
        purchaseService = new PurchaseService(purchaseRepositoryMock, productServiceMock);
    }

    @Test
    void getListByUsernameTest() {
        assertAll(
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("default");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_Username("username"))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService.getListByUsername("username");

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Purchase> purchaseListPage = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("nonSort");
                    purchase.setOwner(user);
                    purchaseListPage.add(purchase);
                    var pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_Username("username", pageRequest))
                            .thenReturn(purchaseListPage);
                    var returnedPurchaseListPage = purchaseService.getListByUsername("username",
                            0, SortType.NONE, Sort.Direction.DESC);

                    //then
                    assertEquals(purchaseListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Purchase> purchaseListPage = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("nameSort");
                    purchase.setOwner(user);
                    purchaseListPage.add(purchase);
                    var pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC,
                            "name", "dateTime", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_Username("username", pageRequest))
                            .thenReturn(purchaseListPage);
                    var returnedPurchaseListPage = purchaseService.getListByUsername("username",
                            1, SortType.NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(purchaseListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Purchase> purchaseListPage = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("dateSort");
                    purchase.setOwner(user);
                    purchaseListPage.add(purchase);
                    var pageRequest = PageRequest.of(2, 10, Sort.Direction.DESC,
                            "dateTime", "name", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_Username("username", pageRequest))
                            .thenReturn(purchaseListPage);
                    var returnedPurchaseListPage = purchaseService.getListByUsername("username",
                            2, SortType.DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(purchaseListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Purchase> purchaseListPage = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("tagNameSort");
                    purchase.setOwner(user);
                    purchaseListPage.add(purchase);
                    var pageRequest = PageRequest.of(3, 10, Sort.Direction.ASC,
                            "tag", "name", "dateTime", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_Username("username", pageRequest))
                            .thenReturn(purchaseListPage);
                    var returnedPurchaseListPage = purchaseService.getListByUsername("username",
                            3, SortType.TAG_NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(purchaseListPage, returnedPurchaseListPage);
                },
                () -> {
                    //given
                    List<Purchase> purchaseListPage = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("tagDateSort");
                    purchase.setOwner(user);
                    purchaseListPage.add(purchase);
                    var pageRequest = PageRequest.of(4, 10, Sort.Direction.DESC,
                            "tag", "dateTime", "name", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_Username("username", pageRequest))
                            .thenReturn(purchaseListPage);
                    var returnedPurchaseListPage = purchaseService.getListByUsername("username",
                            4, SortType.TAG_DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(purchaseListPage, returnedPurchaseListPage);
                }
        );
    }

    @Test
    void getListByUsernameAndProductNameTest() {
        //given
        List<Purchase> purchaseList = new ArrayList<>();
        var purchase = new Purchase();
        var user = new User();
        user.setUsername("username");
        purchase.setName("productName");
        purchase.setOwner(user);
        purchaseList.add(purchase);

        //when
        Mockito.when(purchaseRepositoryMock.findByOwner_UsernameAndName("username", "productName"))
                .thenReturn(purchaseList);
        var returnedPurchaseList = purchaseService.getListByUsernameAndProductName("username",
                "productName");

        //then
        assertEquals(purchaseList, returnedPurchaseList);
    }

    @Test
    void getListByUsernameContainingSubstringTest() {
        assertAll(
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productNameContainingSubstringDefault");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);

                    //when
                    Mockito.when(purchaseRepositoryMock
                                    .findByOwner_UsernameAndNameContainingIgnoreCase("username", "substring"))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService
                            .getListByUsernameContainingSubstring("username", "substring");

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productNameContainingSubstringNoneSort");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);
                    var pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "id");

                    //when
                    Mockito.when(purchaseRepositoryMock
                                    .findByOwner_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    0, SortType.NONE, Sort.Direction.DESC);

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productNameContainingSubstringNameSort");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);
                    var pageRequest = PageRequest.of(1, 10, Sort.Direction.ASC,
                            "name", "dateTime", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock
                                    .findByOwner_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    1, SortType.NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productNameContainingSubstringDateSort");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);
                    var pageRequest = PageRequest.of(2, 10, Sort.Direction.DESC,
                            "dateTime", "name", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock
                                    .findByOwner_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    2, SortType.DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productNameContainingSubstringTagNameSort");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);
                    var pageRequest = PageRequest.of(3, 10, Sort.Direction.ASC,
                            "tag", "name", "dateTime", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock
                                    .findByOwner_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    3, SortType.TAG_NAME, Sort.Direction.ASC);

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                },
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productNameContainingSubstringTagNameSort");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);
                    var pageRequest = PageRequest.of(4, 10, Sort.Direction.DESC,
                            "tag", "dateTime", "name", "id");

                    //when
                    Mockito.when(purchaseRepositoryMock
                                    .findByOwner_UsernameAndNameContainingIgnoreCase("username",
                                            "substring", pageRequest))
                            .thenReturn(purchaseList);
                    var returnedPurchaseList = purchaseService
                            .getListByUsernameContainingSubstring("username", "substring",
                                    4, SortType.TAG_DATE, Sort.Direction.DESC);

                    //then
                    assertEquals(purchaseList, returnedPurchaseList);
                }
        );
    }

    @Test
    void getNameByIdTest() {
        //given
        var purchase = new Purchase();
        purchase.setName("productName");

        //when
        Mockito.when(purchaseRepositoryMock.getById(1L))
                .thenReturn(purchase);
        var returnedName = purchaseService.getNameById(1L);

        //then
        assertEquals(purchase.getName(), returnedName);
    }

    @Test
    void saveTest() {
        assertAll(
                () -> {
                    //given
                    var user = new User();
                    user.setId(1L);
                    var purchase = new Purchase();
                    purchase.setOwner(user);

                    var authentication = new AuthenticationForServiceTests(user);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //when
                    purchaseService.save(purchase);

                    //then
                    Mockito.verify(purchaseRepositoryMock).save(purchase);
                    Mockito.verify(productServiceMock).setDataChanged();
                },
                () -> {
                    //and given
                    var otherUser = new User();
                    otherUser.setId(2L);
                    var otherPurchase = new Purchase();
                    otherPurchase.setOwner(otherUser);

                    //when
                    Executable failSave = () -> purchaseService.save(otherPurchase);

                    //then
                    assertThrows(RuntimeException.class, failSave);
                }
        );

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

                    var purchase1 = new Purchase();
                    purchase1.setOwner(user1);
                    var purchase2 = new Purchase();
                    purchase2.setOwner(user2);

                    var authentication = new AuthenticationForServiceTests(user1);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    //when
                    Mockito.when(purchaseRepositoryMock.findById(1L))
                            .thenReturn(Optional.of(purchase1));
                    Mockito.when(purchaseRepositoryMock.findById(2L))
                            .thenReturn(Optional.of(purchase2));
                    purchaseService.deleteById(1L);

                    //then
                    Mockito.verify(purchaseRepositoryMock).deleteById(1L);
                    Mockito.verify(productServiceMock).setDataChanged();
                },
                () -> {
                    //and given
                    var purchaseId = 2L;

                    //when
                    Executable failDelete = () -> purchaseService.deleteById(purchaseId);

                    //then
                    assertThrows(RuntimeException.class, failDelete);
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

        List<Purchase> purchaseList = new ArrayList<>();
        var purchase1 = new Purchase();
        var purchase2 = new Purchase();
        purchase1.setName("productName");
        purchase2.setName("productName");
        purchase1.setTag("");
        purchase2.setTag("");
        purchase1.setOwner(user);
        purchase2.setOwner(user);
        purchaseList.add(purchase1);
        purchaseList.add(purchase2);

        //when
        Mockito.when(purchaseRepositoryMock.findByOwner_UsernameAndName("username", "productName"))
                .thenReturn(purchaseList);
        purchaseService.updateTagsByUsernameAndProductName("username", "productName", "tag");

        //then
        purchase1.setTag("tag");
        purchase2.setTag("tag");
        Mockito.verify(purchaseRepositoryMock, Mockito.times(2))
                .save(purchase1);
        Mockito.verify(purchaseRepositoryMock, Mockito.times(2))
                .save(purchase2);
        Mockito.verify(productServiceMock, Mockito.times(2))
                .setDataChanged();
    }

    @Test
    void getAnyByUsernameAndProductNameTest() {
        assertAll(
                () -> {
                    //given
                    List<Purchase> purchaseList = new ArrayList<>();
                    var purchase = new Purchase();
                    var user = new User();
                    user.setUsername("username");
                    purchase.setName("productName");
                    purchase.setOwner(user);
                    purchaseList.add(purchase);

                    //when
                    Mockito.when(purchaseRepositoryMock.findByOwner_UsernameAndName("username", "productName"))
                            .thenReturn(purchaseList);
                    var returnedOptionalPurchase = purchaseService
                            .getAnyByUsernameAndProductName("username", "productName");

                    //then
                    assertTrue(returnedOptionalPurchase.isPresent());
                    assertEquals("username", returnedOptionalPurchase.get().getOwner().getUsername());
                    assertEquals("productName", returnedOptionalPurchase.get().getName());
                },
                () -> {
                    //given
                    var username = "otherUsername";
                    var productName = "productName";

                    //when
                    var returnedOptionalPurchase = purchaseService
                            .getAnyByUsernameAndProductName(username, productName);

                    //then
                    assertFalse(returnedOptionalPurchase.isPresent());
                },
                () -> {
                    //given
                    var username = "username";
                    var productName = "otherProductName";

                    //when
                    var returnedOptionalPurchase = purchaseService
                            .getAnyByUsernameAndProductName(username, productName);

                    //then
                    assertFalse(returnedOptionalPurchase.isPresent());
                },
                () -> {
                    //given
                    var username = "otherUsername";
                    var productName = "otherProductName";

                    //when
                    var returnedOptionalPurchase = purchaseService
                            .getAnyByUsernameAndProductName(username, productName);

                    //then
                    assertFalse(returnedOptionalPurchase.isPresent());
                }
        );
    }
}
