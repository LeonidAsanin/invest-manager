package org.lennardjones.investmanager.service.accountPageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.lennardjones.investmanager.service.AccountPageService;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

@ExtendWith(MockitoExtension.class)
class UpdateChosenTableAndAddItToModelTests {
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

    @ParameterizedTest
    @EnumSource(ChosenTableToSee.class)
    void testNonNullInputParameters(ChosenTableToSee chosenTableToSee) {
        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                        .thenReturn(chosenTableToSee);
        accountPageService.updateChosenTableAndAddItToModel(chosenTableToSee.name(), modelMock);
        Mockito.verify(loggedUserManagementServiceMock)
                .setChosenTableIfNotNull(chosenTableToSee.name());
        Mockito.verify(modelMock)
                .addAttribute("chosenTableToSee", chosenTableToSee.name());
    }

    @Test
    void testNullInputParameter() {
        var chosenTableToSee = ChosenTableToSee.PURCHASE;
        Mockito.when(loggedUserManagementServiceMock.getChosenTableToSee())
                .thenReturn(chosenTableToSee);
        accountPageService.updateChosenTableAndAddItToModel(null, modelMock);
        Mockito.verify(loggedUserManagementServiceMock)
                .setChosenTableIfNotNull(null);
        Mockito.verify(modelMock)
                .addAttribute("chosenTableToSee", chosenTableToSee.name());
    }
}
