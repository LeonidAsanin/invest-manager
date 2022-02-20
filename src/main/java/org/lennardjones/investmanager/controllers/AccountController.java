package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.entities.Sale;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.lennardjones.investmanager.services.SaleService;
import org.lennardjones.investmanager.util.PurchaseSaleUtil;
import org.lennardjones.investmanager.util.SortOrderType;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AccountController {
    private final LoggedUserManagementService loggedUserManagementService;
    private final PurchaseService purchaseService;
    private final SaleService saleService;
    private final AccountService accountService;

    public AccountController(LoggedUserManagementService loggedUserManagementService,
                             PurchaseService purchaseService,
                             SaleService saleService,
                             AccountService accountService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.purchaseService = purchaseService;
        this.saleService = saleService;
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public String getAccountPage(
            @RequestParam(name = "editable_purchase", required = false) Long editablePurchaseId,
            @RequestParam(name = "editable_sale", required = false) Long editableSaleId,
            @RequestParam(name = "sortType", required = false) String sortType,
            @RequestParam(name = "sortOrderType", required = false) String sortOrderType,
            @RequestParam(name = "filter", required = false) String filterByNameString,
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "errorId", required = false) Long errorId,
            Model model
    ) {
        if (!loggedUserManagementService.isLoggedIn()) {
            return "redirect:/";
        }

        /* For greeting message */
        model.addAttribute("username", loggedUserManagementService.getUsername());

        /* For sort type chooser */
        if (sortType != null) {
            model.addAttribute("sortType", sortType);
            loggedUserManagementService.setSortType(SortType.valueOf(sortType));
        } else {
            model.addAttribute("sortType", loggedUserManagementService.getSortType().toString());
        }
        if (sortOrderType != null) {
            model.addAttribute("sortOrderType", sortOrderType);
            loggedUserManagementService.setSortOrderType(SortOrderType.valueOf(sortOrderType));
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

        var userId = loggedUserManagementService.getUserId();

        /* For purchase table */
        List<Purchase> purchaseList;
        List<Sale> saleList;
        var filterByName = loggedUserManagementService.getFilterByNameString();
        if (filterByName.equals("")) {
            purchaseList = purchaseService.getListByOwnerId(userId);
            saleList = saleService.getListBySellerId(userId);
        } else {
            purchaseList = purchaseService.getListByOwnerIdContainingSubstring(userId, filterByName);
            saleList = saleService.getListBySellerIdContainingSubstring(userId, filterByName);
        }
        var typeOfSort = loggedUserManagementService.getSortType();
        var typeOfSortOrder = loggedUserManagementService.getSortOrderType();
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, typeOfSort, typeOfSortOrder);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, typeOfSort, typeOfSortOrder);
        model.addAttribute("purchaseList", purchaseList);
        model.addAttribute("saleList", saleList);

        /* For editing table values */
        model.addAttribute("editable_purchase", editablePurchaseId);
        model.addAttribute("editable_sale", editableSaleId);

        /* For adding error messages */
        model.addAttribute("error", error);
        model.addAttribute("errorId", errorId);

        return "account";
    }

    @ModelAttribute
    public void addModelAttributes(Model model) {
        var userId = loggedUserManagementService.getUserId();
        Account userAccount;
        try {
            userAccount = accountService.getAccountById(userId);
        } catch (Exception e) {
            return;
        }

        /* For "Add new purchase" button */
        var purchaseTemplate = new Purchase();
        purchaseTemplate.setOwner(userAccount);
        purchaseTemplate.setDate(LocalDate.now());
        purchaseTemplate.setAmount(1);
        model.addAttribute("purchase", purchaseTemplate);

        /* For "Add new sale" button */
        var saleTemplate = new Sale();
        saleTemplate.setSeller(userAccount);
        saleTemplate.setDate(LocalDate.now());
        saleTemplate.setAmount(1);
        model.addAttribute("sale", saleTemplate);
    }
}
