package org.lennardjones.investmanager.controller;

import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.model.PurchaseTotal;
import org.lennardjones.investmanager.model.SaleTotal;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.lennardjones.investmanager.service.PurchaseService;
import org.lennardjones.investmanager.service.SaleService;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.lennardjones.investmanager.util.PageNavigation;
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
            @RequestParam(name = "filterName", required = false) String filterByNameString,
            @RequestParam(name = "filterTag", required = false) String filterByTagString,
            @RequestParam(name = "error", required = false) String error,
            @ModelAttribute Sale invalidSale, // if a sale entry error occurs, the invalid sale redirected here
            @AuthenticationPrincipal User user,
            Model model
    ) {
        var username = user.getUsername();

        /* For greeting message */
        model.addAttribute("username", username);

        updateSortingParametersAndAddItToModel(sortType, sortOrderType, model);
        updateFilterParametersAndAddItToModel(filterByNameString, filterByTagString, model);
        updateChosenTableAndAddItToModel(chosenTableToSee, model);

        var chosenTable = loggedUserManagementService.getChosenTableToSee();
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var filterByTag = loggedUserManagementService.getFilterByTagString();

        definePage(username, page, chosenTable, filterByName, filterByTag);
        defineTable(username, chosenTable, filterByName, filterByTag, model);
        defineTotals(username, chosenTable, model);
        defineCurrentAndLastPages(username, chosenTable, filterByName, filterByTag, model);

        /* For editing table values */
        model.addAttribute("editable_purchase", editablePurchaseId);
        model.addAttribute("editable_sale", editableSaleId);

        /* For adding error messages */
        model.addAttribute("error", error);

        addSaleTemplate(invalidSale, user, model);
        addPurchaseTemplate(user, model);

        return "account";
    }

    private void updateSortingParametersAndAddItToModel(String sortType, String sortOrderType, Model model) {
        loggedUserManagementService.setSortingParametersIfNotNull(sortType, sortOrderType);
        model.addAttribute("sortType", loggedUserManagementService.getSortType().toString());
        model.addAttribute("sortOrderType", loggedUserManagementService.getSortOrderType().toString());
    }

    private void updateFilterParametersAndAddItToModel(String filterByNameString, String filterByTagString, Model model) {
        loggedUserManagementService.setFilterParametersIfNotNull(filterByNameString, filterByTagString);
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var filterByTag = loggedUserManagementService.getFilterByTagString();
        model.addAttribute("filterByNameString", filterByName);
        model.addAttribute("filterByTagString", filterByTag);
    }

    private void updateChosenTableAndAddItToModel(String chosenTableToSee, Model model) {
        loggedUserManagementService.setChosenTableIfNotNull(chosenTableToSee);
        model.addAttribute("chosenTableToSee",
                loggedUserManagementService.getChosenTableToSee().toString());
    }

    private void definePage(String username, String page, ChosenTableToSee chosenTable, String filterByName,
                            String filterByTag) {
        if (page != null) {
            var pageNavigation = PageNavigation.valueOf(page);
            switch (chosenTable) {
                case PURCHASE -> {
                    List<Purchase> purchaseList;
                    if (!filterByName.equals("")) {
                        purchaseList = purchaseService.getListByUsernameContainingSubstring(username, filterByName);
                    } else {
                        purchaseList = purchaseService.getListByUsername(username);
                    }
                    if (!filterByTag.equals("")) {
                        purchaseList = purchaseList.stream().filter(p -> p.getTag().equals(filterByTag)).toList();
                    }
                    var maxPurchasePage = (int) Math.ceil((double) purchaseList.size() / 10 - 1);

                    var purchasePage = loggedUserManagementService.getPurchasePage();
                    switch (pageNavigation) {
                        case FIRST -> loggedUserManagementService.setPurchasePage(0);
                        case LAST -> loggedUserManagementService.setPurchasePage(maxPurchasePage);
                        case NEXT -> {
                            if (purchasePage != maxPurchasePage) loggedUserManagementService.setPurchasePage(purchasePage + 1);
                        }
                        case PREVIOUS -> {
                            if (purchasePage != 0) loggedUserManagementService.setPurchasePage(purchasePage - 1);
                        }
                        case CURRENT -> {
                            if (purchasePage > maxPurchasePage) loggedUserManagementService.setPurchasePage(maxPurchasePage);
                        }
                    }
                }
                case SALE -> {
                    List<Sale> saleList;
                    if (!filterByName.equals("")) {
                        saleList = saleService.getListByUsernameContainingSubstring(username, filterByName);
                    } else {
                        saleList = saleService.getListByUsername(username);
                    }
                    if (!filterByTag.equals("")) {
                        saleList = saleList.stream().filter(s -> s.getTag().equals(filterByTag)).toList();
                    }
                    var maxSalePage = (int) Math.ceil((double) saleList.size() / 10 - 1);

                    var salePage = loggedUserManagementService.getSalePage();
                    switch (pageNavigation) {
                        case FIRST -> loggedUserManagementService.setSalePage(0);
                        case LAST -> loggedUserManagementService.setSalePage(maxSalePage);
                        case PREVIOUS -> {
                            if (salePage != maxSalePage) loggedUserManagementService.setSalePage(salePage + 1);
                        }
                        case NEXT -> {
                            if (salePage != 0) loggedUserManagementService.setSalePage(salePage - 1);
                        }
                        case CURRENT -> {
                            if (salePage > maxSalePage) loggedUserManagementService.setSalePage(maxSalePage);
                        }
                    }
                }
            }
        }
    }

    private void defineTable(String username, ChosenTableToSee chosenTable, String filterByName, String filterByTag,
                             Model model) {
        List<Purchase> purchaseList = Collections.emptyList();
        List<Sale> saleList = Collections.emptyList();
        var typeOfSort = loggedUserManagementService.getSortType();
        var sortDirection = loggedUserManagementService.getSortOrderType();
        switch (chosenTable) {
            case PURCHASE -> {
                var pageToSee = loggedUserManagementService.getPurchasePage();
                if (filterByName.equals("")) {
                    purchaseList = purchaseService.getListByUsername(username, pageToSee, typeOfSort, sortDirection);
                } else {
                    purchaseList = purchaseService.getListByUsernameContainingSubstring(username, filterByName, pageToSee,
                            typeOfSort, sortDirection);
                }
                if (!filterByTag.equals("")) {
                    purchaseList = purchaseList.stream().filter(p -> p.getTag().equals(filterByTag)).toList();
                }
            }
            case SALE -> {
                var pageToSee = loggedUserManagementService.getSalePage();
                if (filterByName.equals("")) {
                    saleList = saleService.
                            getListByUsername(username, pageToSee, typeOfSort, sortDirection);
                } else {
                    saleList = saleService.getListByUsernameContainingSubstring(username, filterByName, pageToSee,
                            typeOfSort, sortDirection);
                }
                if (!filterByTag.equals("")) {
                    saleList = saleList.stream().filter(s -> s.getTag().equals(filterByTag)).toList();
                }
            }
        }
        model.addAttribute("purchaseList", purchaseList);
        model.addAttribute("saleList", saleList);
    }

    private void defineTotals(String username, ChosenTableToSee chosenTable, Model model) {
        var purchaseList = purchaseService.getListByUsername(username);
        var saleList = saleService.getListByUsername(username);
        var purchaseTotal = new PurchaseTotal(0, 0);
        var saleTotal = new SaleTotal(0, 0, 0, 0);
        switch (chosenTable) {
            case PURCHASE -> {
                if (!purchaseList.isEmpty()) {
                    var totalPurchasePrice = purchaseList.stream().mapToDouble(p -> p.getPrice() * p.getAmount()).sum();
                    var totalPurchaseCommission = purchaseList.stream().mapToDouble(p -> p.getCommission() * p.getAmount())
                            .sum();
                    purchaseTotal = new PurchaseTotal(totalPurchasePrice, totalPurchaseCommission);
                }
            }
            case SALE -> {
                if (!saleList.isEmpty()) {
                    var totalSalePrice = saleList.stream().mapToDouble(s -> s.getPrice() * s.getAmount()).sum();
                    var totalSaleCommission = saleList.stream().mapToDouble(s -> s.getCommission() * s.getAmount()).sum();
                    var totalSaleAbsoluteProfit = saleList.stream().mapToDouble(Sale::getAbsoluteProfit).sum();
                    var fullTotalSalePrice = totalSalePrice - totalSaleCommission;
                    var totalSaleRelativeProfit = (fullTotalSalePrice / (fullTotalSalePrice - totalSaleAbsoluteProfit) - 1) * 100;
                    saleTotal = new SaleTotal(totalSalePrice, totalSaleCommission,
                            totalSaleAbsoluteProfit, totalSaleRelativeProfit);
                }
            }
        }
        model.addAttribute("purchaseTotal", purchaseTotal);
        model.addAttribute("saleTotal", saleTotal);
    }

    private void defineCurrentAndLastPages(String username, ChosenTableToSee chosenTable, String filterByName,
                                           String filterByTag, Model model) {
        /* Setting the current and last page values */
        switch (chosenTable) {
            case PURCHASE -> {
                model.addAttribute("currentPage", loggedUserManagementService.getPurchasePage() + 1);

                var purchaseList = purchaseService.getListByUsername(username);
                if (!filterByName.equals("")) {
                    purchaseList = purchaseService.getListByUsernameContainingSubstring(username, filterByName);
                }
                if (!filterByTag.equals("")) {
                    purchaseList = purchaseList.stream().filter(p -> p.getTag().equals(filterByTag)).toList();
                }
                model.addAttribute("lastPage", (int) Math.ceil((double) purchaseList.size() / 10 - 1) + 1);
            }
            case SALE -> {
                model.addAttribute("currentPage", loggedUserManagementService.getSalePage() + 1);

                var saleList = saleService.getListByUsername(username);
                if (!filterByName.equals("")) {
                    saleList = saleService.getListByUsernameContainingSubstring(username, filterByName);
                }
                if (!filterByTag.equals("")) {
                    saleList = saleList.stream().filter(s -> s.getTag().equals(filterByTag)).toList();
                }
                model.addAttribute("lastPage", (int) Math.ceil((double) saleList.size() / 10 - 1) + 1);
            }
        }
    }

    private void addSaleTemplate(Sale invalidSale, User user, Model model) {
        if (invalidSale.getName() != null) {
            model.addAttribute("sale", invalidSale);
        } else {
            var saleTemplate = new Sale();
            saleTemplate.setSeller(user);
            saleTemplate.setDateTime(LocalDateTime.now());
            model.addAttribute("sale", saleTemplate);
        }
    }

    private void addPurchaseTemplate(User user, Model model) {
        var purchaseTemplate = new Purchase();
        purchaseTemplate.setOwner(user);
        purchaseTemplate.setDateTime(LocalDateTime.now());
        model.addAttribute("purchase", purchaseTemplate);
    }
}
