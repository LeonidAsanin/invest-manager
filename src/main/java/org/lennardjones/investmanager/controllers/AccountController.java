package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.User;
import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.model.PurchaseTotal;
import org.lennardjones.investmanager.model.SaleTotal;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.lennardjones.investmanager.services.SaleService;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Controller for working with Account page.
 *
 * @since 1.0
 * @author lennardjones
 */
@Controller
@RequestMapping("/account")
public class AccountController {
    private final LoggedUserManagementService loggedUserManagementService;
    private final PurchaseService purchaseService;
    private final SaleService saleService;

    public AccountController(LoggedUserManagementService loggedUserManagementService,
                             PurchaseService purchaseService,
                             SaleService saleService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.purchaseService = purchaseService;
        this.saleService = saleService;
    }

    @GetMapping
    public String getAccountPage(
            @RequestParam(name = "chosenTableToSee", required = false) String chosenTableToSee,
            @RequestParam(name = "page", required = false) String page,
            @RequestParam(name = "editable_purchase", required = false) Long editablePurchaseId,
            @RequestParam(name = "editable_sale", required = false) Long editableSaleId,
            @RequestParam(name = "sortType", required = false) String sortType,
            @RequestParam(name = "sortOrderType", required = false) String sortOrderType,
            @RequestParam(name = "filter", required = false) String filterByNameString,
            @RequestParam(name = "error", required = false) String error,
            @ModelAttribute Sale invalidSale, // if a sale entry error occurs, the invalid sale redirected here
            @AuthenticationPrincipal User user,
            Model model
    ) {
        var username = user.getUsername();

        /* For greeting message */
        model.addAttribute("username", username);

        /* For sort type chooser */
        if (sortType != null) {
            model.addAttribute("sortType", sortType);
            loggedUserManagementService.setSortType(SortType.valueOf(sortType));
        } else {
            model.addAttribute("sortType", loggedUserManagementService.getSortType().toString());
        }
        if (sortOrderType != null) {
            model.addAttribute("sortOrderType", sortOrderType);
            loggedUserManagementService.setSortOrderType(Sort.Direction.valueOf(sortOrderType));
        } else {
            model.addAttribute("sortOrderType", loggedUserManagementService.getSortOrderType().toString());
        }

        /* For filtering by name */
        if (filterByNameString != null) {
            if (filterByNameString.equals("")){
                model.addAttribute("filterByNameString", "");
                loggedUserManagementService.setFilterByNameString("");
            } else{
                model.addAttribute("filterByNameString", filterByNameString);
                loggedUserManagementService.setFilterByNameString(filterByNameString);
            }
        } else {
            model.addAttribute("filterByNameString", loggedUserManagementService.getFilterByNameString());
        }

        /* For choosing table to see */
        if (chosenTableToSee != null) {
            model.addAttribute("chosenTableToSee", chosenTableToSee);
            loggedUserManagementService.setChosenTableToSee(ChosenTableToSee.valueOf(chosenTableToSee));
        } else {
            model.addAttribute("chosenTableToSee", loggedUserManagementService.getChosenTableToSee()
                                                                                           .toString());
        }

        /* For purchase or sale table */
        List<Purchase> purchaseList = Collections.emptyList();
        List<Sale> saleList = Collections.emptyList();
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var typeOfSort = loggedUserManagementService.getSortType();
        var typeOfSortOrder = loggedUserManagementService.getSortOrderType();
        switch (loggedUserManagementService.getChosenTableToSee()) {
            case PURCHASE -> {
                if (filterByName.equals("")) {
                    purchaseList = purchaseService.getListByUsername(username);
                } else {
                    purchaseList = purchaseService.getListByUsernameContainingSubstring(username, filterByName);
                }
                purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, typeOfSort, typeOfSortOrder);
            }
            case SALE -> {
                if (filterByName.equals("")) {
                    saleList = saleService.getListByUsername(username);
                } else {
                    saleList = saleService.getListByUsernameContainingSubstring(username, filterByName);
                }
                saleList = PurchaseSaleUtil.sortSaleList(saleList, typeOfSort, typeOfSortOrder);
            }
        }
        model.addAttribute("purchaseList", purchaseList);
        model.addAttribute("saleList", saleList);

        /* For purchase totals */
        var purchaseTotal = new PurchaseTotal(0, 0);
        if (!purchaseList.isEmpty()) {
            var totalPurchasePrice = purchaseList.stream().mapToDouble(p -> p.getPrice() * p.getAmount()).sum();
            var totalPurchaseCommission = purchaseList.stream().mapToDouble(p -> p.getCommission() * p.getAmount())
                    .sum();
            purchaseTotal = new PurchaseTotal(totalPurchasePrice, totalPurchaseCommission);
        }
        model.addAttribute("purchaseTotal", purchaseTotal);

        /* For sale totals */
        var saleTotal = new SaleTotal(0, 0, 0, 0);
        if (!saleList.isEmpty()) {
            var totalSalePrice = saleList.stream().mapToDouble(s -> s.getPrice() * s.getAmount()).sum();
            var totalSaleCommission = saleList.stream().mapToDouble(s -> s.getCommission() * s.getAmount()).sum();
            var totalSaleAbsoluteProfit = saleList.stream().mapToDouble(Sale::getAbsoluteProfit).sum();
            var fullTotalSalePrice = totalSalePrice - totalSaleCommission;
            var totalSaleRelativeProfit = (fullTotalSalePrice / (fullTotalSalePrice - totalSaleAbsoluteProfit) - 1) * 100;
            saleTotal = new SaleTotal(totalSalePrice, totalSaleCommission,
                    totalSaleAbsoluteProfit, totalSaleRelativeProfit);
        }
        model.addAttribute("saleTotal", saleTotal);

        /* For editing table values */
        model.addAttribute("editable_purchase", editablePurchaseId);
        model.addAttribute("editable_sale", editableSaleId);

        /* For adding error messages */
        model.addAttribute("error", error);

        /* For "Add new sale" button */
        if (invalidSale.getName() != null) {
            model.addAttribute("sale", invalidSale);
        } else {
            var saleTemplate = new Sale();
            saleTemplate.setSeller(user);
            saleTemplate.setDateTime(LocalDateTime.now());
            model.addAttribute("sale", saleTemplate);
        }

        return "account";
    }

    @ModelAttribute("purchase")
    Purchase addPurchaseTemplate(@AuthenticationPrincipal User user) {
        var purchaseTemplate = new Purchase();
        purchaseTemplate.setOwner(user);
        purchaseTemplate.setDateTime(LocalDateTime.now());
        return purchaseTemplate;
    }
}
