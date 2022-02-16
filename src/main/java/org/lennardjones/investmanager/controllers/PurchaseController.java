package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.lennardjones.investmanager.services.SaleService;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public PurchaseController(SaleService saleService,
                              PurchaseService purchaseService,
                              AccountService accountService,
                              LoggedUserManagementService loggedUserManagementService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Purchase purchase) {
        purchaseService.save(purchase);
        return "redirect:/account";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model) {
        var userId = loggedUserManagementService.getUserId();
        var productName = purchaseService.getNameById(id);

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByOwnerId(userId)
                .stream()
                .filter(p -> p.getName().equals(productName) && !p.getId().equals(id))
                .toList();
        var saleList = saleService.getListBySellerId(userId)
                .stream()
                .filter(s -> s.getName().equals(productName))
                .toList();
        if (!PurchaseSaleUtil.isQueueCorrect(purchaseList, saleList)) {
            return "redirect:/account?error=deletePurchase&errorId=" + id;
        }

        purchaseService.deleteById(id);
        return "redirect:/account";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/account?editable_purchase=" + id;
    }

    @PostMapping("/save/{id}")
    public String save(Purchase purchase, Model model) {
        var userId = loggedUserManagementService.getUserId();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByOwnerId(userId)
                .stream()
                .filter(p -> p.getName().equals(purchase.getName()) && !p.getId().equals(purchase.getId()))
                .collect(Collectors.toList());
        purchaseList.add(purchase);
        var saleList = saleService.getListBySellerId(userId)
                .stream()
                .filter(s -> s.getName().equals(purchase.getName()))
                .toList();
        if (!PurchaseSaleUtil.isQueueCorrect(purchaseList, saleList)) {
            return "redirect:/account?error=editPurchase&errorId=" + purchase.getId();
        }

        purchase.setOwner(accountService.getAccountById(loggedUserManagementService.getUserId()));
        purchaseService.save(purchase);
        return "redirect:/account";
    }
}
