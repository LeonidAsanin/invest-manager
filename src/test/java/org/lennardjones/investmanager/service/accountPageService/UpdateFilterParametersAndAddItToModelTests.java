package org.lennardjones.investmanager.service.accountPageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.service.AccountPageService;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class UpdateFilterParametersAndAddItToModelTests {

    @Mock
    LoggedUserManagementService loggedUserManagementServiceMock;
    @Mock
    PurchaseService purchaseServiceMock;
    @Mock
    SaleService saleServiceMock;
    @Mock
    Model modelMock;

    AccountPageService accountPageService;

    @BeforeEach
    void before() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                                                    purchaseServiceMock, saleServiceMock);
    }

    @Test
    void testNonNullInputParameters() {
        var filterByNameString = "filterByNameString";
        var filterByTagString = "filterByTagString";
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                        .thenReturn(filterByNameString);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                        .thenReturn(filterByTagString);
        accountPageService.updateFilterParametersAndAddItToModel(filterByNameString, filterByTagString, modelMock);
        Mockito.verify(loggedUserManagementServiceMock)
                .setFilterParametersIfNotNull(filterByNameString, filterByTagString);
        Mockito.verify(modelMock)
                .addAttribute("filterByNameString", filterByNameString);
        Mockito.verify(modelMock)
                .addAttribute("filterByTagString", filterByTagString);
    }

    @Test
    void testNullInputParameters() {
        var filterByNameString = "filterByNameString";
        var filterByTagString = "filterByTagString";
        Mockito.when(loggedUserManagementServiceMock.getFilterByNameString())
                .thenReturn(filterByNameString);
        Mockito.when(loggedUserManagementServiceMock.getFilterByTagString())
                .thenReturn(filterByTagString);
        accountPageService.updateFilterParametersAndAddItToModel(null, null, modelMock);
        Mockito.verify(loggedUserManagementServiceMock)
                .setFilterParametersIfNotNull(null, null);
        Mockito.verify(modelMock)
                .addAttribute("filterByNameString", filterByNameString);
        Mockito.verify(modelMock)
                .addAttribute("filterByTagString", filterByTagString);
    }
}
