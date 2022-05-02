package org.lennardjones.investmanager.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.AccountPageService;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.mockito.ArgumentMatchers;
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
import org.springframework.ui.Model;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

@SpringBootTest
class AccountPageControllerTests {

    @MockBean
    LoggedUserManagementService loggedUserManagementServiceMock;

    @MockBean
    AccountPageService accountPageServiceMock;

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
    void testNullRequestParamsAndDefaultModelAttribute() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                        .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                        .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                        .thenReturn(filterByTag);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/account")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(SecurityMockMvcRequestPostProcessors
                                .authentication(new AuthenticationForControllerTests(user)))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                                                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                                                       ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                             ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                             ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                              ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                                           ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                                           ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                                 ArgumentMatchers.any(Model.class));
    }

    @Test
    void testChosenTableToSeeRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.SALE;
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("chosenTableToSee", chosenTableToSee.name())
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.eq(chosenTableToSee.name()),
                                                  ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testPageRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var page = "NEXT";
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("page", page)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), page, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testEditablePurchaseIdRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("editable_purchase", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", 1L))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testEditableSaleIdRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("editable_sale", "1")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", 1L))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testSortTypeRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var sortType = "TAG_DATE";
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("sortType", sortType)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.eq(sortType),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testSortOrderTypeRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var sortOrderType = "ASC";
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("sortOrderType", sortOrderType)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(sortOrderType), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testFilterByNameStringRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var filterByName = "filter";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("filterName", filterByName)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testFilterByTagStringRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var filterByName = "";
        var filterByTag = "tag";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("filterTag", filterByTag)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testErrorRequestParam() throws Exception {
        var chosenTableToSee = ChosenTableToSee.SALE;
        var error = "error";
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .param("error", error)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", error))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }

    @Test
    void testInvalidSaleModelAttribute() throws Exception {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        var filterByName = "";
        var filterByTag = "";
        var invalidSale = new Sale();
        invalidSale.setName("name");
        invalidSale.setSeller(user);
        invalidSale.setAmount(1);
        invalidSale.setPrice(1.);
        invalidSale.setCommission(.1);
        invalidSale.setDateTime(LocalDateTime.now());

        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByName);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTag);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/account")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .with(SecurityMockMvcRequestPostProcessors
                                        .authentication(new AuthenticationForControllerTests(user)))
                                .flashAttr("invalidSale", invalidSale)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.view().name("account"))
                .andExpect(MockMvcResultMatchers.model().attribute("username", user.getUsername()))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_purchase", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("editable_sale", "null"))
                .andExpect(MockMvcResultMatchers.model().attribute("error", "null"))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());


        Mockito.verify(accountPageServiceMock)
                .updateSortingParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateFilterParametersAndAddItToModel(ArgumentMatchers.isNull(),
                        ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .updateChosenTableAndAddItToModel(ArgumentMatchers.isNull(), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .definePage(user.getUsername(), null, chosenTableToSee, filterByName, filterByTag);
        Mockito.verify(accountPageServiceMock)
                .defineTable(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.eq(filterByName), ArgumentMatchers.eq(filterByTag),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineTotals(ArgumentMatchers.eq(user.getUsername()), ArgumentMatchers.eq(chosenTableToSee),
                        ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .defineCurrentAndLastPages(ArgumentMatchers.eq(user.getUsername()),
                        ArgumentMatchers.eq(chosenTableToSee), ArgumentMatchers.eq(filterByName),
                        ArgumentMatchers.eq(filterByTag), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addPurchaseTemplate(ArgumentMatchers.eq(user), ArgumentMatchers.any(Model.class));
        Mockito.verify(accountPageServiceMock)
                .addSaleTemplate(ArgumentMatchers.eq(invalidSale), ArgumentMatchers.eq(user),
                        ArgumentMatchers.any(Model.class));
    }
}
