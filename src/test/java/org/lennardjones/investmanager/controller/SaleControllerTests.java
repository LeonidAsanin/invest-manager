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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SaleControllerTests {

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

        var newSale = new Sale();
        newSale.setSeller(user);
        newSale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
        newSale.setName("productName");
        newSale.setTag(null);
        newSale.setAmount(1);
        newSale.setPrice(4.4);
        newSale.setCommission(0.);

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
        sale.setDateTime(LocalDateTime.of(2022, 4, 1, 1, 1));
        sale.setName("productName");
        sale.setTag("tag");
        sale.setAmount(1);
        sale.setPrice(9.);
        sale.setCommission(.2);
        sale.setAbsoluteProfit(4.4);
        sale.setRelativeProfit(100.);

        assertAll(
                () -> {
                    List<Purchase> purchaseList = new ArrayList<>();
                    purchaseList.add(purchase);

                    List<Sale> saleList = new ArrayList<>();
                    saleList.add(sale);

                    Mockito.when(purchaseServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    newSale.getName()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsernameAndProductName(user.getUsername(), newSale.getName()))
                            .thenReturn(saleList);
                    Mockito.when(saleServiceMock.getAnyByUsernameAndProductName(user.getUsername(), newSale.getName()))
                            .thenReturn(Optional.of(sale));

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/add")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("seller.id", user.getId().toString())
                                            .param("seller.username", user.getUsername())
                                            .param("seller.password", user.getPassword())
                                            .param("dateTime", newSale.getDateTime().toString())
                                            .param("name", newSale.getName())
                                            .param("amount", newSale.getAmount().toString())
                                            .param("price", newSale.getPrice().toString())
                                            .param("commission", newSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?page=LAST"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    newSale.setTag(sale.getTag());

                    Mockito.verify(saleServiceMock)
                            .save(newSale);
                    Mockito.verify(saleServiceMock)
                            .updateProfitsByName(newSale.getName());
                },
                () -> {
                    List<Purchase> purchaseList = new ArrayList<>();
                    purchaseList.add(purchase);

                    List<Sale> saleList = new ArrayList<>();
                    saleList.add(sale);

                    Mockito.when(purchaseServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    newSale.getName()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsernameAndProductName(user.getUsername(), newSale.getName()))
                            .thenReturn(saleList);
                    Mockito.when(saleServiceMock.getAnyByUsernameAndProductName(user.getUsername(), newSale.getName()))
                            .thenReturn(Optional.empty());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/add")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("seller.id", user.getId().toString())
                                            .param("seller.username", user.getUsername())
                                            .param("seller.password", user.getPassword())
                                            .param("dateTime", newSale.getDateTime().toString())
                                            .param("name", newSale.getName())
                                            .param("amount", newSale.getAmount().toString())
                                            .param("price", newSale.getPrice().toString())
                                            .param("commission", newSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?page=LAST"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    newSale.setTag("");

                    Mockito.verify(saleServiceMock)
                            .save(newSale);
                    Mockito.verify(saleServiceMock, Mockito.times(2))
                            .updateProfitsByName(newSale.getName());

                    newSale.setTag(null);
                },
                () -> {
                    List<Purchase> purchaseList = new ArrayList<>();

                    List<Sale> saleList = new ArrayList<>();
                    saleList.add(sale);

                    Mockito.when(purchaseServiceMock.getListByUsernameAndProductName(user.getUsername(),
                                    newSale.getName()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsernameAndProductName(user.getUsername(), newSale.getName()))
                            .thenReturn(saleList);
                    Mockito.when(saleServiceMock.getAnyByUsernameAndProductName(user.getUsername(), newSale.getName()))
                            .thenReturn(Optional.empty());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/add")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("seller.id", user.getId().toString())
                                            .param("seller.username", user.getUsername())
                                            .param("seller.password", user.getPassword())
                                            .param("dateTime", newSale.getDateTime().toString())
                                            .param("name", newSale.getName())
                                            .param("amount", newSale.getAmount().toString())
                                            .param("price", newSale.getPrice().toString())
                                            .param("commission", newSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?error=addSale"))
                            .andExpect(MockMvcResultMatchers.flash().attribute("sale", newSale))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }

    @Test
    void deleteTest() throws Exception {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        var sale = new Sale();
        sale.setId(1L);
        sale.setSeller(user);
        sale.setDateTime(LocalDateTime.of(2022, 4, 1, 1, 1));
        sale.setName("productName");
        sale.setTag("tag");
        sale.setAmount(1);
        sale.setPrice(9.);
        sale.setCommission(.2);
        sale.setAbsoluteProfit(4.4);
        sale.setRelativeProfit(100.);

        List<Sale> saleList = new ArrayList<>();
        saleList.add(sale);

        Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                        .thenReturn(saleList);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/sale/delete/" + sale.getId())
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .authentication(new AuthenticationForControllerTests(user)))
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account?page=CURRENT"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());

        Mockito.verify(saleServiceMock)
                .deleteById(sale.getId());
        Mockito.verify(saleServiceMock)
                .updateProfitsByName(sale.getName());
    }

    @Test
    void editTest() throws Exception {
        var id = 1;
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/sale/edit/" + id)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests())))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/account?editable_sale=" + id))
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
        purchase1.setAmount(2);
        purchase1.setPrice(4.);
        purchase1.setCommission(.4);

        var purchase2 = new Purchase();
        purchase2.setId(1L);
        purchase2.setOwner(user);
        purchase2.setDateTime(LocalDateTime.of(2022, 2, 1, 1, 1));
        purchase2.setName("productName2");
        purchase2.setTag("tag");
        purchase2.setAmount(2);
        purchase2.setPrice(4.);
        purchase2.setCommission(.4);

        var sale = new Sale();
        sale.setId(1L);
        sale.setSeller(user);
        sale.setDateTime(LocalDateTime.of(2022, 4, 1, 1, 1));
        sale.setName("productName1");
        sale.setTag("tag");
        sale.setAmount(1);
        sale.setPrice(9.);
        sale.setCommission(.2);
        sale.setAbsoluteProfit(4.4);
        sale.setRelativeProfit(100.);

        assertAll(
                () -> {
                    var editedSale = (Sale) sale.clone();
                    editedSale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
                    editedSale.setTag("editedTag");
                    editedSale.setAmount(2);
                    editedSale.setPrice(10.);
                    editedSale.setCommission(1.1);

                    List<Purchase> purchaseList = new ArrayList<>();
                    purchaseList.add(purchase1);
                    purchaseList.add(purchase2);

                    List<Sale> saleList = new ArrayList<>();
                    saleList.add(sale);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                            MockMvcRequestBuilders
                                    .post("/sale/save/" + sale.getId())
                                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                                    .with(SecurityMockMvcRequestPostProcessors
                                            .authentication(new AuthenticationForControllerTests(user)))
                                    .param("dateTime", editedSale.getDateTime().toString())
                                    .param("name", editedSale.getName())
                                    .param("tag", editedSale.getTag())
                                    .param("amount", editedSale.getAmount().toString())
                                    .param("price", editedSale.getPrice().toString())
                                    .param("commission", editedSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    Mockito.verify(purchaseServiceMock)
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedSale.getName(),
                                    editedSale.getTag());
                    Mockito.verify(saleServiceMock)
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedSale.getName(),
                                    editedSale.getTag());

                    editedSale.setSeller(user);
                    editedSale.setAbsoluteProfit(null);
                    editedSale.setRelativeProfit(null);
                    Mockito.verify(saleServiceMock)
                            .save(editedSale);

                    Mockito.verify(saleServiceMock)
                            .updateProfitsByName(editedSale.getName());
                },
                () -> {
                    var editedSale = (Sale) sale.clone();
                    editedSale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
                    editedSale.setName(purchase2.getName());
                    editedSale.setTag("editedTag");
                    editedSale.setAmount(2);
                    editedSale.setPrice(10.);
                    editedSale.setCommission(1.1);

                    List<Purchase> purchaseList = new ArrayList<>();
                    purchaseList.add(purchase1);
                    purchaseList.add(purchase2);

                    List<Sale> saleList = new ArrayList<>();
                    saleList.add(sale);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/save/" + sale.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("dateTime", editedSale.getDateTime().toString())
                                            .param("name", editedSale.getName())
                                            .param("tag", editedSale.getTag())
                                            .param("amount", editedSale.getAmount().toString())
                                            .param("price", editedSale.getPrice().toString())
                                            .param("commission", editedSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());

                    Mockito.verify(purchaseServiceMock)
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedSale.getName(),
                                    editedSale.getTag());
                    Mockito.verify(saleServiceMock)
                            .updateTagsByUsernameAndProductName(user.getUsername(), editedSale.getName(),
                                    editedSale.getTag());

                    editedSale.setSeller(user);
                    editedSale.setAbsoluteProfit(null);
                    editedSale.setRelativeProfit(null);
                    Mockito.verify(saleServiceMock)
                            .save(editedSale);

                    Mockito.verify(saleServiceMock)
                            .updateProfitsByName(editedSale.getName());
                    Mockito.verify(saleServiceMock, Mockito.times(2))
                            .updateProfitsByName(sale.getName());
                },
                () -> {
                    var editedSale = (Sale) sale.clone();
                    editedSale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
                    editedSale.setName(purchase2.getName());
                    editedSale.setTag("editedTag");
                    editedSale.setAmount(20);
                    editedSale.setPrice(10.);
                    editedSale.setCommission(1.1);

                    List<Purchase> purchaseList = new ArrayList<>();
                    purchaseList.add(purchase1);
                    purchaseList.add(purchase2);

                    List<Sale> saleList = new ArrayList<>();
                    saleList.add(sale);

                    Mockito.when(purchaseServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(purchaseList);
                    Mockito.when(saleServiceMock.getListByUsername(user.getUsername()))
                            .thenReturn(saleList);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/save/" + sale.getId())
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("dateTime", editedSale.getDateTime().toString())
                                            .param("name", editedSale.getName())
                                            .param("tag", editedSale.getTag())
                                            .param("amount", editedSale.getAmount().toString())
                                            .param("price", editedSale.getPrice().toString())
                                            .param("commission", editedSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/account?error=editSale"))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }
}
