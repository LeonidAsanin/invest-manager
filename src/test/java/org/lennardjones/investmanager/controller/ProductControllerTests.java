package org.lennardjones.investmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.model.Product;
import org.lennardjones.investmanager.model.ProductTotal;
import org.lennardjones.investmanager.service.ProductService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class ProductControllerTests {

    @MockBean
    ProductService productServiceMock;

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
    void getProductPageTest() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        Set<Product> productSet = new HashSet<>();
        var product1 = new Product();
        product1.setName("product1");
        product1.setTag("tag1");
        product1.setAmount(1);
        product1.setAveragePrice(1.);
        product1.setCurrentPrice(2.);
        product1.setAbsoluteProfit(1.);
        product1.setRelativeProfit(100.);
        var product2 = new Product();
        product2.setName("product2");
        product2.setTag("tag1");
        product2.setAmount(2);
        product2.setAveragePrice(2.);
        product2.setCurrentPrice(2.);
        product2.setAbsoluteProfit(0.);
        product2.setRelativeProfit(0.);
        var product3 = new Product();
        product3.setName("product3");
        product3.setTag("tag2");
        product3.setAmount(3);
        product3.setAveragePrice(2.);
        product3.setCurrentPrice(1.);
        product3.setAbsoluteProfit(-1.);
        product3.setRelativeProfit(-100.);
        var product4 = new Product();
        product4.setName("product4");
        product4.setTag("tag2");
        product4.setAmount(4);
        product4.setAveragePrice(100.);
        product4.setCurrentPrice(150.);
        product4.setAbsoluteProfit(50.);
        product4.setRelativeProfit(50.);
        Collections.addAll(productSet, product1, product2, product3, product4);

        var sortedProductSet = new LinkedHashSet<Product>();
        Collections.addAll(sortedProductSet, product1, product2, product3, product4);

        var totalPrice = product1.getAveragePrice() * product1.getAmount() +
                product2.getAveragePrice() * product2.getAmount() +
                product3.getAveragePrice() * product3.getAmount() +
                product4.getAveragePrice() * product4.getAmount();
        var totalCurrentPrice = product1.getCurrentPrice() * product1.getAmount() +
                product2.getCurrentPrice() * product2.getAmount() +
                product3.getCurrentPrice() * product3.getAmount() +
                product4.getCurrentPrice() * product4.getAmount();
        var totalAbsoluteProfit = product1.getAbsoluteProfit() + product2.getAbsoluteProfit() +
                product3.getAbsoluteProfit() + product4.getAbsoluteProfit();
        var totalRelativeProfit = (totalPrice / (totalPrice - totalAbsoluteProfit) - 1) * 100;
        var productTotal = new ProductTotal(totalPrice, totalCurrentPrice,
                totalAbsoluteProfit, totalRelativeProfit);
        assertAll(
                () -> {
                    Mockito.when(productServiceMock.getAllByUser(user))
                            .thenReturn(productSet);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/product")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                            )
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                            .andExpect(MockMvcResultMatchers.view().name("products"))
                            .andExpect(MockMvcResultMatchers.model().attribute("productSet", sortedProductSet))
                            .andExpect(MockMvcResultMatchers.model().attribute("productTotal", productTotal))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    Mockito.when(productServiceMock.getAllByUser(user))
                            .thenReturn(productSet);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/product")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                                            .param("editable", "")
                            )
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                            .andExpect(MockMvcResultMatchers.view().name("products"))
                            .andExpect(MockMvcResultMatchers.model().attribute("productSet", sortedProductSet))
                            .andExpect(MockMvcResultMatchers.model().attribute("productTotal", productTotal))
                            .andExpect(MockMvcResultMatchers.model().attribute("editable", true))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                },
                () -> {
                    Set<Product> emptyProductSet = new HashSet<>();
                    var defaultProductTotal = new ProductTotal(0, 0, 0, 0);

                    Mockito.when(productServiceMock.getAllByUser(user))
                            .thenReturn(emptyProductSet);

                    mockMvc.perform(
                                    MockMvcRequestBuilders
                                            .get("/product")
                                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                                            .with(SecurityMockMvcRequestPostProcessors
                                                    .authentication(new AuthenticationForControllerTests(user)))
                            )
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                            .andExpect(MockMvcResultMatchers.view().name("products"))
                            .andExpect(MockMvcResultMatchers.model().attribute("productSet", emptyProductSet))
                            .andExpect(MockMvcResultMatchers.model().attribute("productTotal", defaultProductTotal))
                            .andExpect(SecurityMockMvcResultMatchers.authenticated());
                }
        );
    }

    @Test
    void setCurrentPricesAndCalculateProfitsTest() throws Exception {
        var user = new User();
            user.setId(1L);
            user.setUsername("username");
            user.setPassword("password");

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
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .params(multiValueMap)
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/product"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());

        Mockito.verify(productServiceMock, Mockito.times(1))
                .calculateProfits(user.getId(), productName.get(0), Double.parseDouble(currentPrice.get(0)));
        Mockito.verify(productServiceMock, Mockito.times(1))
                .calculateProfits(user.getId(), productName.get(1), Double.parseDouble(currentPrice.get(1)));
        Mockito.verify(productServiceMock, Mockito.times(1))
                .calculateProfits(user.getId(), productName.get(2), Double.parseDouble(currentPrice.get(2)));
    }

    @Test
    void editCurrentPricesTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/product/edit")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .authentication(new AuthenticationForControllerTests()))
                )
                .andExpect(MockMvcResultMatchers.redirectedUrl("/product?editable"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }
}
