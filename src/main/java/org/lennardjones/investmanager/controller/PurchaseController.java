package org.lennardjones.investmanager.controller;

import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Controller for working with {@link org.lennardjones.investmanager.entity.Purchase purchases}.
 *
 * @since 1.0
 * @author lennardjones
 */
@Controller
@RequestMapping("/purchase")
public class PurchaseController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;

    public PurchaseController(SaleService saleService, PurchaseService purchaseService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
    }

    @PostMapping("/add")
    public String add(Purchase purchase, @AuthenticationPrincipal User user) {
        /* Tag assigning */
        var optionalPurchase = purchaseService.getAnyByUsernameAndProductName(user.getUsername(),
                                                                                                purchase.getName());
        optionalPurchase.ifPresentOrElse(p -> purchase.setTag(p.getTag()), () -> purchase.setTag(""));

        purchaseService.save(purchase);

        saleService.updateProfitsByName(purchase.getName());

        return "redirect:/account?page=LAST";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var username = user.getUsername();
        var productName = purchaseService.getNameById(id);

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByUsernameAndProductName(username, productName)
                .stream()
                .filter(p -> !p.getId().equals(id))
                .toList();
        var saleList = saleService.getListByUsernameAndProductName(username, productName);
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName)) {
            return "redirect:/account?error=deletePurchase";
        }

        purchaseService.deleteById(id);

        saleService.updateProfitsByName(productName);

        return "redirect:/account?page=CURRENT";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/account?editable_purchase=" + id;
    }

    @PostMapping("/save/{id}")
    public String save(Purchase purchase, @AuthenticationPrincipal User user) {
        var username = user.getUsername();
        var purchaseId = purchase.getId();
        var productName = purchase.getName(); //product name can be changed

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var previousPurchaseList = purchaseService.getListByUsername(username);
        var purchaseList = previousPurchaseList.stream()
                .filter(p -> !p.getId().equals(purchaseId))
                .collect(Collectors.toList());
        purchaseList.add(purchase);
        var saleList = saleService.getListByUsername(username);
        var unchangedProductName = previousPurchaseList.stream()
                .filter(p -> p.getId().equals(purchaseId))
                .map(Purchase::getName)
                .findFirst().orElseThrow();
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName) ||
            PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, unchangedProductName)) {
            return "redirect:/account?error=editPurchase";
        }

        /* Tags updating */
        var tag = purchase.getTag();
        purchaseService.updateTagsByUsernameAndProductName(username, productName, tag);
        saleService.updateTagsByUsernameAndProductName(username, productName, tag);

        /* Saving of edited purchase */
        purchase.setOwner(user);
        purchaseService.save(purchase);

        /* Calculating and setting up refreshed profits to the sales */
        saleService.updateProfitsByName(productName);
        if (!unchangedProductName.equals(productName)) {
            saleService.updateProfitsByName(unchangedProductName);
        }

        return "redirect:/account";
    }
}
