package org.lennardjones.investmanager.controllers;

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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String accountPage(
            @RequestParam(name = "editable_purchase", required = false) Long editablePurchaseId,
            @RequestParam(name = "editable_sale", required = false) Long editableSaleId,
            @RequestParam(name = "sortType", required = false) String sortType,
            @RequestParam(name = "sortOrderType", required = false) String sortOrderType,
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

        var usedId = loggedUserManagementService.getUserId();

        /* For purchase table */
        var purchaseList = purchaseService.getListByOwnerId(usedId);
        var saleList = saleService.getListBySellerId(usedId);
        var typeOfSort = loggedUserManagementService.getSortType();
        var typeOfSortOrder = loggedUserManagementService.getSortOrderType();
        purchaseList = PurchaseSaleUtil.sortPurchaseList(purchaseList, typeOfSort, typeOfSortOrder);
        saleList = PurchaseSaleUtil.sortSaleList(saleList, typeOfSort, typeOfSortOrder);
        model.addAttribute("purchaseList", purchaseList);
        model.addAttribute("saleList", saleList);

        /* For editing table values */
        model.addAttribute("editable_purchase", editablePurchaseId);
        model.addAttribute("editable_sale", editableSaleId);

        /* For "Add new purchase" button */
        var purchaseTemplate = new Purchase();
        purchaseTemplate.setOwner(accountService.getAccountById(usedId));
        model.addAttribute("purchase", purchaseTemplate);

        /* For "Add new sale" button */
        var saleTemplate = new Sale();
        saleTemplate.setSeller(accountService.getAccountById(usedId));
        model.addAttribute("sale", saleTemplate);

        /* For adding error messages */
        model.addAttribute("error", error);
        model.addAttribute("errorId", errorId);

        return "account";
    }
}
