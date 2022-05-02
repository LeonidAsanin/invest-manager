package org.lennardjones.investmanager.service.accountPageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.AccountPageService;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddPurchaseTemplateTests {
    @Mock
    LoggedUserManagementService loggedUserManagementServiceMock;
    @Mock
    PurchaseService purchaseServiceMock;
    @Mock
    SaleService saleServiceMock;
    @Mock
    Model modelMock;

    @Captor
    ArgumentCaptor<Purchase> purchaseCaptor;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    AccountPageService accountPageService;

    @BeforeEach()
    void before() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                                                    purchaseServiceMock, saleServiceMock);
    }

    @Test
    void test() {
        var user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        accountPageService.addPurchaseTemplate(user, modelMock);
        Mockito.verify(modelMock)
                .addAttribute(stringCaptor.capture(), purchaseCaptor.capture());
        var string = stringCaptor.getValue();
        var purchase = purchaseCaptor.getValue();

        assertAll(
                () -> assertEquals("purchase", string),
                () -> assertNull(purchase.getId()),
                () -> assertNull(purchase.getPrice()),
                () -> assertNull(purchase.getCommission()),
                () -> assertNull(purchase.getAmount()),
                () -> assertNull(purchase.getName()),
                () -> assertNull(purchase.getTag()),
                () -> assertEquals(user, purchase.getOwner()),
                () -> assertEquals(LocalDateTime.now().getSecond(), purchase.getDateTime().getSecond()),
                () -> assertEquals(LocalDateTime.now().getMinute(), purchase.getDateTime().getMinute()),
                () -> assertEquals(LocalDateTime.now().getHour(), purchase.getDateTime().getHour()),
                () -> assertEquals(LocalDateTime.now().getDayOfMonth(), purchase.getDateTime().getDayOfMonth()),
                () -> assertEquals(LocalDateTime.now().getMonth(), purchase.getDateTime().getMonth()),
                () -> assertEquals(LocalDateTime.now().getYear(), purchase.getDateTime().getYear())
        );
    }
}
