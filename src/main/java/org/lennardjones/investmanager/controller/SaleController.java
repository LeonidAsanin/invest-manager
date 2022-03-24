package org.lennardjones.investmanager.controller;

import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

/**
 * Controller for working with {@link org.lennardjones.investmanager.entity.Sale sales}.
 *
 * @since 1.0
 * @author lennardjones
 */
@Controller
@RequestMapping("/sale")
public class SaleController {
    private final SaleService saleService;
    private final PurchaseService purchaseService;

    public SaleController(SaleService saleService, PurchaseService purchaseService) {
        this.saleService = saleService;
        this.purchaseService = purchaseService;
    }

    @PostMapping("/add")
    public String add(Sale sale, @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
        var username = user.getUsername();
        var productName = sale.getName();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByUsername(username);
        var saleList = saleService.getListByUsername(username);
        saleList.add(sale);
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName)) {
            redirectAttributes.addFlashAttribute("sale", sale);
            return "redirect:/account?error=addSale";
        }

        /* Calculating and setting up benefits to the sale */
        saleList = PurchaseSaleUtil.calculateProfitsFromSales(purchaseList, saleList, productName);
        var saleWithCalculatedBenefits = saleList.stream()
                .filter(s -> s.getId() == null)
                .findFirst().orElseThrow();
        sale.setAbsoluteProfit(saleWithCalculatedBenefits.getAbsoluteProfit());
        sale.setRelativeProfit(saleWithCalculatedBenefits.getRelativeProfit());

        /* Tag assigning */
        var optionalSale = saleService.getAnyByUsernameAndProductName(user.getUsername(),
                sale.getName());
        optionalSale.ifPresentOrElse(p -> sale.setTag(p.getTag()), () -> sale.setTag(""));

        saleService.save(sale);

        return "redirect:/account?page=LAST";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        saleService.deleteById(id);
        return "redirect:/account?page=CURRENT";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/account?editable_sale=" + id;
    }

    @PostMapping("/save/{id}")
    public String save(Sale sale, @AuthenticationPrincipal User user) {
        var username = user.getUsername();
        var saleId = sale.getId();
        var productName = sale.getName();

        /* Validating presence of enough amount products to sell considering purchase and sale dates */
        var purchaseList = purchaseService.getListByUsername(username);
        var saleList = saleService.getListByUsername(username)
                .stream()
                .filter(s -> !s.getId().equals(saleId))
                .collect(Collectors.toList());
        saleList.add(sale);
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName)) {
            return "redirect:/account?error=editSale";
        }

        /* Calculating and setting up benefits to the sale */
        saleList = PurchaseSaleUtil.calculateProfitsFromSales(purchaseList, saleList, productName);
        var saleWithCalculatedBenefits = saleList.stream()
                .filter(s -> s.getId().equals(saleId))
                .findFirst().orElseThrow();
        sale.setAbsoluteProfit(saleWithCalculatedBenefits.getAbsoluteProfit());
        sale.setRelativeProfit(saleWithCalculatedBenefits.getRelativeProfit());

        /* Tags updating */
        var tag = sale.getTag();
        purchaseService.updateTagsByUsernameAndProductName(username, productName, tag);
        saleService.updateTagsByUsernameAndProductName(username, productName, tag);

        sale.setSeller(user);
        saleService.save(sale);
        return "redirect:/account";
    }
}