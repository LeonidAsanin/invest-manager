package org.lennardjones.investmanager.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.controller.AuthenticationForControllerTests;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class SecurityConfigTests {
    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    User user;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
    }

    @Test
    void logoutTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/logout")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .authentication(new AuthenticationForControllerTests()))
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @Test
    void unauthenticatedCallsTest() {
        Assertions.assertAll(
                //get calls
                () -> {
                    String[] urlsForGetRequests = {"/account", "/product", "/settings"};
                    for (var url : urlsForGetRequests) {
                        mockMvc.perform(
                                        MockMvcRequestBuilders
                                                .get(url)
                                )
                                .andExpect(MockMvcResultMatchers.status().is(302))
                                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("**/login"))
                                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                    }
                    urlsForGetRequests = new String[]{"/error", "/login", "/register"};
                    for (var url : urlsForGetRequests) {
                        mockMvc.perform(
                                        MockMvcRequestBuilders
                                                .get(url)
                                )
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                    }
                },

                //get settings
                () -> {

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings/deleteAccount")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(302))
                            .andExpect(MockMvcResultMatchers.redirectedUrlPattern("**/login"))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/settings/deleteAccount")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("confirmation", "confirmation")
                            )
                            .andExpect(MockMvcResultMatchers.redirectedUrl("/login"))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                },

                //post settings
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/editUsername")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewUsername")
                                            .param("username", "newUsername")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/editPassword")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/settings/saveNewPassword")
                                            .param("password", "newPassword")
                                            .param("passwordConfirmation", "newPassword")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                },

                //post product
                () -> {
                    List<String> productName = new ArrayList<>();
                    Collections.addAll(productName, "product1", "product1", "product3");
                    List<String> currentPrice = new ArrayList<>();
                    Collections.addAll(currentPrice, "1.1", "2.2", "3.");

                    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
                    multiValueMap.addAll("productName", productName);
                    multiValueMap.addAll("currentPrice", currentPrice);
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/product/calculate")
                                            .params(multiValueMap)
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/product/edit")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                },

                //post purchase
                () -> {
                    var newPurchase = new Purchase();
                    newPurchase.setOwner(user);
                    newPurchase.setDateTime(LocalDateTime.of(2022, 1, 1, 1, 1));
                    newPurchase.setName("productName");
                    newPurchase.setTag(null);
                    newPurchase.setAmount(1);
                    newPurchase.setPrice(2.);
                    newPurchase.setCommission(.2);
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/add")
                                            .param("owner.id", user.getId().toString())
                                            .param("owner.username", user.getUsername())
                                            .param("owner.password", user.getPassword())
                                            .param("dateTime", newPurchase.getDateTime().toString())
                                            .param("name", newPurchase.getName())
                                            .param("amount", newPurchase.getAmount().toString())
                                            .param("price", newPurchase.getPrice().toString())
                                            .param("commission", newPurchase.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/delete/" + 1)
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/edit/" + 1)
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    var editedPurchase = new Purchase();
                    editedPurchase.setId(1L);
                    editedPurchase.setOwner(user);
                    editedPurchase.setName("productName1");
                    editedPurchase.setDateTime(LocalDateTime.of(2022, 2, 2, 2, 2));
                    editedPurchase.setTag("newTag");
                    editedPurchase.setAmount(2);
                    editedPurchase.setPrice(3.8);
                    editedPurchase.setCommission(0.2);
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/purchase/save/" + editedPurchase.getId())
                                            .param("dateTime", editedPurchase.getDateTime().toString())
                                            .param("name", editedPurchase.getName())
                                            .param("tag", editedPurchase.getTag())
                                            .param("amount", editedPurchase.getAmount().toString())
                                            .param("price", editedPurchase.getPrice().toString())
                                            .param("commission", editedPurchase.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                },

                //post sale
                () -> {
                    var newSale = new Sale();
                    newSale.setSeller(user);
                    newSale.setDateTime(LocalDateTime.of(2022, 3, 1, 1, 1));
                    newSale.setName("productName");
                    newSale.setTag(null);
                    newSale.setAmount(1);
                    newSale.setPrice(4.4);
                    newSale.setCommission(0.);
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/add")
                                            .param("seller.id", user.getId().toString())
                                            .param("seller.username", user.getUsername())
                                            .param("seller.password", user.getPassword())
                                            .param("dateTime", newSale.getDateTime().toString())
                                            .param("name", newSale.getName())
                                            .param("amount", newSale.getAmount().toString())
                                            .param("price", newSale.getPrice().toString())
                                            .param("commission", newSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/delete/" + 1)
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/edit/" + 1)
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());

                    var editedSale = new Sale();
                    editedSale.setId(1L);
                    editedSale.setSeller(user);
                    editedSale.setDateTime(LocalDateTime.of(2022, 4, 1, 1, 1));
                    editedSale.setName("productName1");
                    editedSale.setTag("tag");
                    editedSale.setAmount(1);
                    editedSale.setPrice(9.);
                    editedSale.setCommission(.2);
                    editedSale.setAbsoluteProfit(4.4);
                    editedSale.setRelativeProfit(100.);
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/sale/save/" + editedSale.getId())
                                            .param("dateTime", editedSale.getDateTime().toString())
                                            .param("name", editedSale.getName())
                                            .param("tag", editedSale.getTag())
                                            .param("amount", editedSale.getAmount().toString())
                                            .param("price", editedSale.getPrice().toString())
                                            .param("commission", editedSale.getCommission().toString())
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                },

                //post register
                () -> {
                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .post("/register")
                                            .param("username", "username")
                                            .param("password", "password")
                                            .param("passwordConfirmation", "password")
                            )
                            .andExpect(MockMvcResultMatchers.status().is(403))
                            .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
                }
        );
    }
}
