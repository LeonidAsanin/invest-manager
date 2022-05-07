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
import org.lennardjones.investmanager.util.SortType;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class UpdateSortingParametersAndAddItToModelTests {
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
    void setup() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                                                    purchaseServiceMock, saleServiceMock);
    }

    @ParameterizedTest
    @EnumSource(SortType.class)
    void testAllNonNullInputParameters(SortType sortType) {
        //given
        var sortTypeString = sortType.name();

        assertAll(
                () -> {
                    //and given
                    var orderTypeString = Sort.Direction.ASC.name();

                    //when
                    Mockito.when(loggedUserManagementServiceMock.getSortType())
                            .thenReturn(sortType);
                    Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                            .thenReturn(Sort.Direction.ASC);
                    accountPageService.updateSortingParametersAndAddItToModel(sortTypeString, orderTypeString, modelMock);

                    //then
                    Mockito.verify(loggedUserManagementServiceMock)
                            .setSortingParametersIfNotNull(sortTypeString, orderTypeString);
                    Mockito.verify(modelMock)
                            .addAttribute("sortType", sortTypeString);
                    Mockito.verify(modelMock)
                            .addAttribute("sortOrderType", orderTypeString);
                },
                () -> {
                    //and given
                    var orderTypeString = Sort.Direction.DESC.name();

                    //when
                    Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                            .thenReturn(Sort.Direction.DESC);
                    accountPageService.updateSortingParametersAndAddItToModel(sortTypeString, orderTypeString, modelMock);

                    //then
                    Mockito.verify(loggedUserManagementServiceMock)
                            .setSortingParametersIfNotNull(sortTypeString, orderTypeString);
                    Mockito.verify(modelMock, Mockito.times(2))
                            .addAttribute("sortType", sortTypeString);
                    Mockito.verify(modelMock)
                            .addAttribute("sortOrderType", orderTypeString);
                }
        );
    }

    @Test
    void testAllNullInputParameters() {
        //given
        var sortType = SortType.NONE;
        var orderType = Sort.Direction.ASC;

        //when
        Mockito.when(loggedUserManagementServiceMock.getSortType())
                .thenReturn(sortType);
        Mockito.when(loggedUserManagementServiceMock.getSortOrderType())
                .thenReturn(orderType);
        accountPageService.updateSortingParametersAndAddItToModel(null, null, modelMock);

        //then
        Mockito.verify(loggedUserManagementServiceMock)
                .setSortingParametersIfNotNull(null, null);
        Mockito.verify(modelMock)
                .addAttribute("sortType", sortType.name());
        Mockito.verify(modelMock)
                .addAttribute("sortOrderType", orderType.name());
    }
}
