package org.lennardjones.investmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class PurchaseControllerTests {

    @MockBean
    SaleService saleServiceMock;

    @MockBean
    PurchaseService purchaseServiceMock;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void addTest() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        var newPurchase = new Purchase();
        newPurchase.setOwner(user);
        newPurchase.setDateTime(LocalDateTime.of(2022, 1, 1, 1, 1));
        newPurchase.setName("productName");
        newPurchase.setTag(null);
        newPurchase.setAmount(1);
        newPurchase.setPrice(2.);
        newPurchase.setCommission(.2);

        var purchase = new Purchase();
        purchase.setId(1L);
        purchase.setOwner(user);
        purchase.setDateTime(LocalDateTime.of(2022, 2, 1, 1, 1));
        purchase.setName("productName");
        purchase.setTag("tag");
        purchase.setAmount(2);
        purchase.setPrice(4.);
        purchase.setCommission(.4);

        var sale = new Sale();
        sale.setId(1L);
        sale.setSeller(user);
        sale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
        sale.setName("productName");
        sale.setTag("tag");
        sale.setAmount(1);
        sale.setPrice(9.);
        sale.setCommission(.2);
        sale.setAbsoluteProfit(4.4);
        sale.setRelativeProfit(100.);

        assertAll(
                () -> {
                    Mockito.when(purchaseServiceMock.getAnyByUsernameAndProductName(user.getUsername(),
                                    newPurchase.getName()))
                            .thenReturn(Optional.of(purchase));

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/add")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("owner.id", user.getId().toString())
                                            .param("owner.username", user.getUsername())
                                            .param("owner.password", user.getPassword())
                                            .param("dateTime", newPurchase.getDateTime().toString())
                                            .param("name", newPurchase.getName())
                                            .param("amount", newPurchase.getAmount().toString())
                                            .param("price", newPurchase.getPrice().toString())
                                            .param("commission", newPurchase.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?page=LAST"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    newPurchase.setTag(purchase.getTag());

                    Mockito.verify(purchaseServiceMock, Mockito.times(1))
                            .save(newPurchase);

                    Mockito.verify(saleServiceMock, Mockito.times(1))
                            .updateProfitsByName(newPurchase.getName());
                },
                () -> {
                    Mockito.when(purchaseServiceMock.getAnyByUsernameAndProductName(user.getUsername(),
                                    newPurchase.getName()))
                            .thenReturn(Optional.empty());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/add")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("owner.id", user.getId().toString())
                                            .param("owner.username", user.getUsername())
                                            .param("owner.password", user.getPassword())
                                            .param("dateTime", newPurchase.getDateTime().toString())
                                            .param("name", newPurchase.getName())
                                            .param("amount", newPurchase.getAmount().toString())
                                            .param("price", newPurchase.getPrice().toString())
                                            .param("commission", newPurchase.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?page=LAST"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    newPurchase.setTag("");

                    Mockito.verify(purchaseServiceMock, Mockito.times(1))
                            .save(newPurchase);

                    Mockito.verify(saleServiceMock, Mockito.times(2))
                            .updateProfitsByName(newPurchase.getName());
                }
        );

    }

    @Test
    void deleteTest() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setOwner(user);
        purchase1.setDateTime(LocalDateTime.of(2022, 2, 1, 1, 1));
        purchase1.setName("productName");
        purchase1.setTag("tag");
        purchase1.setAmount(1);
        purchase1.setPrice(1.8);
        purchase1.setCommission(.2);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setOwner(user);
        purchase2.setDateTime(LocalDateTime.of(2022, 4, 1, 1, 1));
        purchase2.setName("productName");
        purchase2.setTag("tag");
        purchase2.setAmount(2);
        purchase2.setPrice(3.8);
        purchase2.setCommission(.2);

        assertAll(
                () -> {
                    var sale = new Sale();
                    sale.setId(1L);
                    sale.setSeller(user);
                    sale.setDateTime(LocalDateTime.of(2022, 5, 1, 1, 1));
                    sale.setName("productName");
                    sale.setTag("tag");
                    sale.setAmount(1);
                    sale.setPrice(4.2);
                    sale.setCommission(0.2);
                    sale.setAbsoluteProfit(2.);
                    sale.setRelativeProfit(100.);

                    List<Purchase> purchaseList = new ArrayList<>();
                    List<Sale> saleList = new ArrayList<>();
                    Collections.addAll(purchaseList, purchase1, purchase2);
                    Collections.addAll(saleList, sale);

                    Mockito.when(purchaseServiceMock.getNameById(purchase1.getId()))
                            .thenReturn(purchase1.getName());
                    Mockito.when(purchaseServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    purchase1.getName()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    purchase1.getName()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/delete/" + purchase1.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user))))
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?page=CURRENT"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    Mockito.verify(purchaseServiceMock, Mockito.times(1))
                            .deleteById(purchase1.getId());
                    Mockito.verify(saleServiceMock, Mockito.times(1))
                            .updateProfitsByName(purchase1.getName());
                },
                () -> {
                    var sale = new Sale();
                    sale.setId(1L);
                    sale.setSeller(user);
                    sale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
                    sale.setName("productName");
                    sale.setTag("tag");
                    sale.setAmount(1);
                    sale.setPrice(4.2);
                    sale.setCommission(0.2);
                    sale.setAbsoluteProfit(2.);
                    sale.setRelativeProfit(100.);

                    List<Purchase> purchaseList = new ArrayList<>();
                    List<Sale> saleList = new ArrayList<>();
                    Collections.addAll(purchaseList, purchase1, purchase2);
                    Collections.addAll(saleList, sale);

                    Mockito.when(purchaseServiceMock.getNameById(purchase1.getId()))
                            .thenReturn(purchase1.getName());
                    Mockito.when(purchaseServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    purchase1.getName()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    purchase1.getName()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/delete/" + purchase1.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user))))
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?error=deletePurchase"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }

    @Test
    void editTest() throws Exception {
        var id = 1;
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/purchase/edit/" + id)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests())))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account?editable_purchase=" + id))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    void saveTest() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        var purchase1 = new Purchase();
        purchase1.setId(1L);
        purchase1.setOwner(user);
        purchase1.setDateTime(LocalDateTime.of(2022, 2, 1, 1, 1));
        purchase1.setName("productName1");
        purchase1.setTag("tag");
        purchase1.setAmount(1);
        purchase1.setPrice(2.);
        purchase1.setCommission(0.);

        var purchase2 = new Purchase();
        purchase2.setId(2L);
        purchase2.setOwner(user);
        purchase2.setDateTime(LocalDateTime.of(2022, 1, 1, 1, 1));
        purchase2.setName("productName2");
        purchase2.setTag("tag");
        purchase2.setAmount(1);
        purchase2.setPrice(4.);
        purchase2.setCommission(0.);

        var purchase3 = new Purchase();
        purchase3.setId(3L);
        purchase3.setOwner(user);
        purchase3.setDateTime(LocalDateTime.of(2022, 2, 1, 1, 1));
        purchase3.setName("productName2");
        purchase3.setTag("tag");
        purchase3.setAmount(1);
        purchase3.setPrice(16.);
        purchase3.setCommission(0.);

        var sale1 = new Sale();
        sale1.setId(1L);
        sale1.setSeller(user);
        sale1.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
        sale1.setName("productName1");
        sale1.setTag("tag");
        sale1.setAmount(1);
        sale1.setPrice(4.);
        sale1.setCommission(0.);
        sale1.setAbsoluteProfit(2.);
        sale1.setRelativeProfit(100.);

        var sale2 = new Sale();
        sale2.setId(2L);
        sale2.setSeller(user);
        sale2.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
        sale2.setName("productName2");
        sale2.setTag("tag");
        sale2.setAmount(1);
        sale2.setPrice(8.);
        sale2.setCommission(0.);
        sale2.setAbsoluteProfit(4.);
        sale2.setRelativeProfit(100.);

        List<Purchase> purchaseList = new ArrayList<>();
        List<Sale> saleList = new ArrayList<>();
        Collections.addAll(purchaseList, purchase1, purchase2, purchase3);
        Collections.addAll(saleList, sale1, sale2);

        assertAll(
                () -> {
                    var editedPurchase1 = (Purchase) purchase1.clone();
                    editedPurchase1.setDateTime(LocalDateTime.of(2022, 2, 2, 2, 2));
                    editedPurchase1.setTag("newTag");
                    editedPurchase1.setAmount(2);
                    editedPurchase1.setPrice(3.8);
                    editedPurchase1.setCommission(0.2);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/save/" + editedPurchase1.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("dateTime", editedPurchase1.getDateTime().toString())
                                            .param("name", editedPurchase1.getName())
                                            .param("tag", editedPurchase1.getTag())
                                            .param("amount", editedPurchase1.getAmount().toString())
                                            .param("price", editedPurchase1.getPrice().toString())
                                            .param("commission", editedPurchase1.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    Mockito.verify(purchaseServiceMock)
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedPurchase1.getName(),
                                    editedPurchase1.getTag());
                    Mockito.verify(saleServiceMock)
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedPurchase1.getName(),
                                    editedPurchase1.getTag());

                    editedPurchase1.setOwner(user);
                    Mockito.verify(purchaseServiceMock)
                            .save(editedPurchase1);

                    Mockito.verify(saleServiceMock)
                            .updateProfitsByName(editedPurchase1.getName());

                },
                () -> {
                    var editedPurchase1 = (Purchase) purchase1.clone();
                    editedPurchase1.setDateTime(LocalDateTime.of(2022, 4, 2, 2, 2));
                    editedPurchase1.setTag("newTag");
                    editedPurchase1.setAmount(2);
                    editedPurchase1.setPrice(3.8);
                    editedPurchase1.setCommission(0.2);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/save/" + editedPurchase1.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("dateTime", editedPurchase1.getDateTime().toString())
                                            .param("name", editedPurchase1.getName())
                                            .param("tag", editedPurchase1.getTag())
                                            .param("amount", editedPurchase1.getAmount().toString())
                                            .param("price", editedPurchase1.getPrice().toString())
                                            .param("commission", editedPurchase1.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?error=editPurchase"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    var editedPurchase2 = (Purchase) purchase2.clone();
                    editedPurchase2.setDateTime(LocalDateTime.of(2022, 1, 1, 1, 1));
                    editedPurchase2.setName(purchase1.getName());
                    editedPurchase2.setTag("newTag");
                    editedPurchase2.setAmount(2);
                    editedPurchase2.setPrice(8.);
                    editedPurchase2.setCommission(0.);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/save/" + editedPurchase2.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("dateTime", editedPurchase2.getDateTime().toString())
                                            .param("name", editedPurchase2.getName())
                                            .param("tag", editedPurchase2.getTag())
                                            .param("amount", editedPurchase2.getAmount().toString())
                                            .param("price", editedPurchase2.getPrice().toString())
                                            .param("commission", editedPurchase2.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    Mockito.verify(purchaseServiceMock, Mockito.times(2))
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedPurchase2.getName(),
                                    editedPurchase2.getTag());
                    Mockito.verify(saleServiceMock, Mockito.times(2))
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedPurchase2.getName(),
                                    editedPurchase2.getTag());

                    editedPurchase2.setOwner(user);
                    Mockito.verify(purchaseServiceMock, Mockito.times(1))
                            .save(editedPurchase2);

                    Mockito.verify(saleServiceMock, Mockito.times(2))
                            .updateProfitsByName(purchase1.getName());
                    Mockito.verify(saleServiceMock)
                            .updateProfitsByName(purchase2.getName());
                },
                () -> {
                    purchase3.setDateTime(LocalDateTime.of(2022, 4, 1, 1, 1));

                    var editedPurchase2 = (Purchase) purchase2.clone();
                    editedPurchase2.setDateTime(LocalDateTime.of(2022, 4, 2, 2, 2));
                    editedPurchase2.setName(purchase1.getName());
                    editedPurchase2.setTag("newTag");
                    editedPurchase2.setAmount(2);
                    editedPurchase2.setPrice(3.8);
                    editedPurchase2.setCommission(0.2);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/save/" + editedPurchase2.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("dateTime", editedPurchase2.getDateTime().toString())
                                            .param("name", editedPurchase2.getName())
                                            .param("tag", editedPurchase2.getTag())
                                            .param("amount", editedPurchase2.getAmount().toString())
                                            .param("price", editedPurchase2.getPrice().toString())
                                            .param("commission", editedPurchase2.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?error=editPurchase"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }
}
