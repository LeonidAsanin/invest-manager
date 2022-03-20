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

        return "redirect:/account?page=LAST";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var username = user.getUsername();
        var productName = purchaseService.getNameById(id);

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByUsername(username)
                .stream()
                .filter(p -> !p.getId().equals(id))
                .toList();
        var saleList = saleService.getListByUsername(username);
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName)) {
            return "redirect:/account?error=deletePurchase";
        }

        /* Calculating and setting up refreshed benefits to the sales */
        saleList = PurchaseSaleUtil.calculateProfitsFromSales(purchaseList, saleList, productName);
        for (var sale : saleList) {
            saleService.save(sale);
        }

        purchaseService.deleteById(id);
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
        var productName = purchase.getName();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByUsername(username)
                .stream()
                .filter(p -> !p.getId().equals(purchaseId))
                .collect(Collectors.toList());
        purchaseList.add(purchase);
        var saleList = saleService.getListByUsername(username);
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName)) {
            return "redirect:/account?error=editPurchase";
        }

        /* Calculating and setting up refreshed benefits to the sales */
        saleList = PurchaseSaleUtil.calculateProfitsFromSales(purchaseList, saleList, productName);
        for (var sale : saleList) {
            saleService.save(sale);
        }

        /* Tags updating */
        var tag = purchase.getTag();
        purchaseService.updateTagsByUsernameAndProductName(username, productName, tag);
        saleService.updateTagsByUsernameAndProductName(username, productName, tag);

        purchase.setOwner(user);
        purchaseService.save(purchase);
        return "redirect:/account";
    }
}
