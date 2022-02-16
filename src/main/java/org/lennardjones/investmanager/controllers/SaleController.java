package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.lennardjones.investmanager.services.SaleService;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    public String add(@ModelAttribute Sale sale, Model model) {
        var userId = loggedUserManagementService.getUserId();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByOwnerId(userId)
                .stream()
                .filter(p -> p.getName().equals(sale.getName()))
                .toList();
        var saleList = saleService.getListBySellerId(userId)
                .stream()
                .filter(s -> s.getName().equals(sale.getName()))
                .collect(Collectors.toList());
        saleList.add(sale);
        if (!PurchaseSaleUtil.isQueueCorrect(purchaseList, saleList)) {
            return "redirect:/account?error=addSale";
        }

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

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByOwnerId(userId)
                .stream()
                .filter(p -> p.getName().equals(sale.getName()))
                .toList();
        var saleList = saleService.getListBySellerId(userId)
                .stream()
                .filter(s -> s.getName().equals(sale.getName()) && !s.getId().equals(sale.getId()))
                .collect(Collectors.toList());
        saleList.add(sale);
        if (!PurchaseSaleUtil.isQueueCorrect(purchaseList, saleList)) {
            return "redirect:/account?error=editSale&errorId=" + sale.getId();
        }

        sale.setSeller(accountService.getAccountById(userId));
        saleService.save(sale);
        return "redirect:/account";
    }
}
