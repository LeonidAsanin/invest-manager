package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Purchase;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.lennardjones.investmanager.util.SortType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;

@Controller
public class AccountController {
    private final LoggedUserManagementService loggedUserManagementService;
    private final PurchaseService purchaseService;
    private final AccountService accountService;

    public AccountController(LoggedUserManagementService loggedUserManagementService,
                             PurchaseService purchaseService,
                             AccountService accountService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.purchaseService = purchaseService;
        this.accountService = accountService;
    }

    @GetMapping("/account")
    public String accountPage(
            @RequestParam(name = "editable", required = false) Long editableElementId,
            @RequestParam(name = "sortType", required = false) String sortType,
            Model model
    ) {
        if (!loggedUserManagementService.isLoggedIn()) {
            return "redirect:/";
        }

        /* For greeting message */
        model.addAttribute("username", loggedUserManagementService.getUsername());

        /* For sort type chooser */
        if (sortType != null) {
            System.out.println("sortType != null : " + sortType);
            model.addAttribute("sortType", sortType);
            loggedUserManagementService.setSortType(SortType.valueOf(sortType));
        } else {
            System.out.println("sortType == null : " + loggedUserManagementService.getSortType());
            model.addAttribute("sortType", loggedUserManagementService.getSortType().toString());
        }

        /* For purchase table */
        var purchaseList = purchaseService.getListByOwnerId(loggedUserManagementService.getUserId());
        purchaseList = switch (loggedUserManagementService.getSortType()) {
            case NONE -> purchaseList.stream()
                    .sorted(Comparator.comparing(Purchase::getId))
                    .toList();
            case NAME -> purchaseList.stream()
                    .sorted(Comparator.comparing(Purchase::getName)
                            .thenComparing(Purchase::getDate))
                    .toList();
            case DATE -> purchaseList.stream()
                    .sorted(Comparator.comparing(Purchase::getDate)
                            .thenComparing(Purchase::getName))
                    .toList();
        };
        model.addAttribute("purchaseList", purchaseList);

        /* For editing table values */
        model.addAttribute("editable", editableElementId);

        /* For "Add new purchase" button */
        var purchaseTemplate = new Purchase();
        purchaseTemplate.setOwner(accountService.getAccountById(loggedUserManagementService.getUserId()));
        model.addAttribute("purchase", purchaseTemplate);

        return "account.html";
    }
}
