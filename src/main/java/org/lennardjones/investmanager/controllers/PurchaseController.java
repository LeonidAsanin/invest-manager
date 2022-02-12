package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {
    private final PurchaseService purchaseService;
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public PurchaseController(
            PurchaseService purchaseService,
            AccountService accountService,
            LoggedUserManagementService loggedUserManagementService) {
        this.purchaseService = purchaseService;
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @PostMapping("/add")
    public String add(Purchase purchase) {
        purchase.setOwner(accountService.getAccountById(loggedUserManagementService.getUserId()));
        purchaseService.save(purchase);
        return "redirect:/account";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        purchaseService.deleteById(id);
        return "redirect:/account";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/account?editable=" + id;
    }

    @PostMapping("/save/{id}")
    public String save(@PathVariable Long id, Purchase purchase) {
        purchase.setOwner(accountService.getAccountById(loggedUserManagementService.getUserId()));
        purchaseService.save(purchase);
        return "redirect:/account";
    }
}
