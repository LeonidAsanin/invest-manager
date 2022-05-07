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
        var purchaseList = purchaseService.getListByUsernameAndProductName(username, productName);
        var saleList = saleService.getListByUsernameAndProductName(username, productName);
        saleList.add(sale);
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName)) {
            redirectAttributes.addFlashAttribute("sale", sale);
            return "redirect:/account?error=addSale";
        }

        /* Tag assigning */
        var optionalSale = saleService.getAnyByUsernameAndProductName(user.getUsername(),
                                                                                    sale.getName());
        optionalSale.ifPresentOrElse(p -> sale.setTag(p.getTag()), () -> sale.setTag(""));

        saleService.save(sale);

        saleService.updateProfitsByName(productName);

        return "redirect:/account?page=LAST";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var username = user.getUsername();

        var saleList = saleService.getListByUsername(username);

        var saleToDelete = saleList.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow();

        saleService.deleteById(id);

        saleService.updateProfitsByName(saleToDelete.getName());

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
        var previousSaleList = saleService.getListByUsername(username);
        var saleList = previousSaleList.stream()
                .filter(s -> !s.getId().equals(saleId))
                .collect(Collectors.toList());
        saleList.add(sale);
        var unchangedProductName = previousSaleList.stream()
                .filter(s -> s.getId().equals(saleId))
                .map(Sale::getName)
                .findFirst()
                .orElseThrow();
        if (PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, productName) ||
            PurchaseSaleUtil.isQueueIncorrect(purchaseList, saleList, unchangedProductName)) {
            return "redirect:/account?error=editSale";
        }

        /* Tags updating */
        var tag = sale.getTag();
        purchaseService.updateTagsByUsernameAndProductName(username, productName, tag);
        saleService.updateTagsByUsernameAndProductName(username, productName, tag);

        /* Saving of edited sale */
        sale.setSeller(user);
        saleService.save(sale);

        /* Calculating and setting up refreshed profits to the sales */
        saleService.updateProfitsByName(productName);
        if (!unchangedProductName.equals(productName)) {
            saleService.updateProfitsByName(unchangedProductName);
        }

        return "redirect:/account";
    }
}
