package org.lennardjones.investmanager.service.accountPageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lennardjones.investmanager.entity.Sale;
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
class AddSaleTemplateTests {
    @Mock
    LoggedUserManagementService loggedUserManagementServiceMock;
    @Mock
    PurchaseService purchaseServiceMock;
    @Mock
    SaleService saleServiceMock;
    @Mock
    Model modelMock;

    @Captor
    ArgumentCaptor<Sale> saleCaptor;
    @Captor
    ArgumentCaptor<String> stringCaptor;

    AccountPageService accountPageService;
    User user;
    Sale invalidSale;

    @BeforeEach()
    void before() {
        accountPageService = new AccountPageService(loggedUserManagementServiceMock,
                purchaseServiceMock, saleServiceMock);

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");

        invalidSale = new Sale();
    }

    @Test
    void testInvalidSaleIsNotEmpty() {
        invalidSale.setName("name");
        invalidSale.setSeller(user);
        invalidSale.setPrice(1.);
        invalidSale.setCommission(.1);
        invalidSale.setAmount(1);
        invalidSale.setDateTime(LocalDateTime.now());

        accountPageService.addSaleTemplate(invalidSale, user, modelMock);

        Mockito.verify(modelMock)
                .addAttribute("sale", invalidSale);
    }

    @Test
    void testInvalidSaleIsEmpty() {
        accountPageService.addSaleTemplate(invalidSale, user, modelMock);

        Mockito.verify(modelMock)
                .addAttribute(stringCaptor.capture(), saleCaptor.capture());

        var string = stringCaptor.getValue();
        var sale = saleCaptor.getValue();

        assertAll(
                () -> assertEquals("sale", string),
                () -> assertNull(sale.getId()),
                () -> assertNull(sale.getPrice()),
                () -> assertNull(sale.getCommission()),
                () -> assertNull(sale.getAmount()),
                () -> assertNull(sale.getName()),
                () -> assertNull(sale.getTag()),
                () -> assertNull(sale.getRelativeProfit()),
                () -> assertNull(sale.getAbsoluteProfit()),
                () -> assertEquals(user, sale.getSeller()),
                () -> assertEquals(LocalDateTime.now().getSecond(), sale.getDateTime().getSecond()),
                () -> assertEquals(LocalDateTime.now().getMinute(), sale.getDateTime().getMinute()),
                () -> assertEquals(LocalDateTime.now().getHour(), sale.getDateTime().getHour()),
                () -> assertEquals(LocalDateTime.now().getDayOfMonth(), sale.getDateTime().getDayOfMonth()),
                () -> assertEquals(LocalDateTime.now().getMonth(), sale.getDateTime().getMonth()),
                () -> assertEquals(LocalDateTime.now().getYear(), sale.getDateTime().getYear())
        );
    }
}
