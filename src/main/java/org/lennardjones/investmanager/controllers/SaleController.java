package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.lennardjones.investmanager.services.SaleService;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sale")
public class SaleController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public SaleController(SaleService saleService,
                          PurchaseService purchaseService,
                          AccountService accountService,
                          LoggedUserManagementService loggedUserManagementService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @PostMapping("/add")
    public String add(Sale sale, Model model) {
        var userId = loggedUserManagementService.getUserId();
        var productName = sale.getName();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByOwnerId(userId);
        var saleList = saleService.getListBySellerId(userId);
        saleList.add(sale);
        if (!PurchaseSaleUtil.isQueueCorrect(purchaseList, saleList, productName)) {
            return "redirect:/account?error=addSale";
        }

        /* Calculating and setting up benefits to the sale */
        saleList = PurchaseSaleUtil.calculateBenefitsFromSales(purchaseList, saleList, productName);
        var saleWithCalculatedBenefits = saleList.stream()
                .filter(s -> s.getId() == null)
                .findFirst().orElseThrow();
        sale.setAbsoluteBenefit(saleWithCalculatedBenefits.getAbsoluteBenefit());
        sale.setRelativeBenefit(saleWithCalculatedBenefits.getRelativeBenefit());

        saleService.save(sale);

        return "redirect:/account";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        saleService.deleteById(id);
        return "redirect:/account";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/account?editable_sale=" + id;
    }

    @PostMapping("/save/{id}")
    public String save(Sale sale, Model model) {
        var userId = loggedUserManagementService.getUserId();
        var saleId = sale.getId();
        var productName = sale.getName();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByOwnerId(userId);
        var saleList = saleService.getListBySellerId(userId)
                .stream()
                .filter(s -> !s.getId().equals(saleId))
                .collect(Collectors.toList());
        saleList.add(sale);
        if (!PurchaseSaleUtil.isQueueCorrect(purchaseList, saleList, productName)) {
            return "redirect:/account?error=editSale&errorId=" + saleId;
        }

        /* Calculating and setting up benefits to the sale */
        saleList = saleList.stream() //refreshing sort to place edited sale at the right place
                .sorted(Comparator.comparing(Sale::getDate))
                .collect(Collectors.toList());
        saleList = PurchaseSaleUtil.calculateBenefitsFromSales(purchaseList, saleList, productName);
        var saleWithCalculatedBenefits = saleList.stream()
                .filter(s -> s.getId().equals(saleId))
                .findFirst().orElseThrow();
        sale.setAbsoluteBenefit(saleWithCalculatedBenefits.getAbsoluteBenefit());
        sale.setRelativeBenefit(saleWithCalculatedBenefits.getRelativeBenefit());

        sale.setSeller(accountService.getAccountById(userId));
        saleService.save(sale);
        return "redirect:/account";
    }
}
