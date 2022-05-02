package org.lennardjones.investmanager.service;

import org.lennardjones.investmanager.entity.Purchase;
import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.model.PurchaseTotal;
import org.lennardjones.investmanager.model.SaleTotal;
import org.lennardjones.investmanager.util.ChosenTableToSee;
import org.lennardjones.investmanager.util.PageNavigation;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.annotation.SessionScope;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Service which provides methods for managing of account page model and logged user data.
 *
 * @since 1.6
 * @author lennardjones
 */
@Service
@SessionScope
public class AccountPageService {
    LoggedUserManagementService loggedUserManagementService;
    PurchaseService purchaseService;
    SaleService saleService;

    public AccountPageService(LoggedUserManagementService loggedUserManagementService,
                              PurchaseService purchaseService,
                              SaleService saleService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.purchaseService = purchaseService;
        this.saleService = saleService;
    }

    public void updateSortingParametersAndAddItToModel(String sortType, String sortOrderType, Model model) {
        loggedUserManagementService.setSortingParametersIfNotNull(sortType, sortOrderType);
        model.addAttribute("sortType", loggedUserManagementService.getSortType().toString());
        model.addAttribute("sortOrderType", loggedUserManagementService.getSortOrderType().toString());
    }

    public void updateFilterParametersAndAddItToModel(String filterByNameString, String filterByTagString, Model model) {
        loggedUserManagementService.setFilterParametersIfNotNull(filterByNameString, filterByTagString);
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var filterByTag = loggedUserManagementService.getFilterByTagString();
        model.addAttribute("filterByNameString", filterByName);
        model.addAttribute("filterByTagString", filterByTag);
    }

    public void updateChosenTableAndAddItToModel(String chosenTableToSee, Model model) {
        loggedUserManagementService.setChosenTableIfNotNull(chosenTableToSee);
        model.addAttribute("chosenTableToSee",
                loggedUserManagementService.getChosenTableToSee().toString());
    }

    public void definePage(String username, String page, ChosenTableToSee chosenTable, String filterByName,
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
                        case NEXT -> {
                            if (salePage != maxSalePage) loggedUserManagementService.setSalePage(salePage + 1);
                        }
                        case PREVIOUS -> {
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

    public void defineTable(String username, ChosenTableToSee chosenTable, String filterByName, String filterByTag,
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

    public void defineTotals(String username, ChosenTableToSee chosenTable, Model model) {
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

    public void defineCurrentAndLastPages(String username, ChosenTableToSee chosenTable, String filterByName,
                                           String filterByTag, Model model) {
        var currentPage = 0;
        var lastPage = 0;

        switch (chosenTable) {
            case PURCHASE -> {
                currentPage = loggedUserManagementService.getPurchasePage() + 1;
                List<Purchase> purchaseList;
                if (!filterByName.equals("")) {
                    purchaseList = purchaseService.getListByUsernameContainingSubstring(username, filterByName);
                } else {
                    purchaseList = purchaseService.getListByUsername(username);
                }
                if (!filterByTag.equals("")) {
                    purchaseList = purchaseList.stream().filter(p -> p.getTag().equals(filterByTag)).toList();
                }
                lastPage = (int) Math.ceil((double) purchaseList.size() / 10 - 1) + 1;
            }
            case SALE -> {
                currentPage = loggedUserManagementService.getSalePage() + 1;
                List<Sale> saleList;
                if (!filterByName.equals("")) {
                    saleList = saleService.getListByUsernameContainingSubstring(username, filterByName);
                } else {
                    saleList = saleService.getListByUsername(username);
                }
                if (!filterByTag.equals("")) {
                    saleList = saleList.stream().filter(s -> s.getTag().equals(filterByTag)).toList();
                }
                lastPage = (int) Math.ceil((double) saleList.size() / 10 - 1) + 1;
            }
        }
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", lastPage);
    }

    public void addPurchaseTemplate(User user, Model model) {
        var purchaseTemplate = new Purchase();
        purchaseTemplate.setOwner(user);
        purchaseTemplate.setDateTime(LocalDateTime.now());
        model.addAttribute("purchase", purchaseTemplate);
    }

    public void addSaleTemplate(Sale invalidSale, User user, Model model) {
        if (invalidSale.getName() != null) {
            model.addAttribute("sale", invalidSale);
        } else {
            var saleTemplate = new Sale();
            saleTemplate.setSeller(user);
            saleTemplate.setDateTime(LocalDateTime.now());
            model.addAttribute("sale", saleTemplate);
        }
    }
}
