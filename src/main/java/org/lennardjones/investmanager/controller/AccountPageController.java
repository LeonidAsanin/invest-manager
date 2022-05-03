package org.lennardjones.investmanager.controller;

import org.lennardjones.investmanager.entity.Sale;
import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.AccountPageService;
import org.lennardjones.investmanager.service.LoggedUserManagementService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Objects;

/**
 * Controller for working with Account page.
 *
 * @since 1.0
 * @author lennardjones
 */
@Controller
@SessionScope
@RequestMapping("/account")
public class AccountPageController {
    private final LoggedUserManagementService loggedUserManagementService;
    private final AccountPageService accountPageService;

    public AccountPageController(LoggedUserManagementService loggedUserManagementService,
                                 AccountPageService accountPageService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.accountPageService = accountPageService;
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
            @ModelAttribute(name = "invalidSale") Sale invalidSale, // if a sale entry error occurs,
                                                                    // the invalid sale redirected here
            @AuthenticationPrincipal User user,
            Model model
    ) {
        var username = user.getUsername();

        /* For greeting message */
        model.addAttribute("username", username);

        accountPageService.updateSortingParametersAndAddItToModel(sortType, sortOrderType, model);
        accountPageService.updateFilterParametersAndAddItToModel(filterByNameString, filterByTagString, model);
        accountPageService.updateChosenTableAndAddItToModel(chosenTableToSee, model);

        var chosenTable = loggedUserManagementService.getChosenTableToSee();
        var filterByName = loggedUserManagementService.getFilterByNameString();
        var filterByTag = loggedUserManagementService.getFilterByTagString();

        accountPageService.definePage(username, page, chosenTable, filterByName, filterByTag);
        accountPageService.defineTable(username, chosenTable, filterByName, filterByTag, model);
        accountPageService.defineTotals(username, chosenTable, model);
        accountPageService.defineCurrentAndLastPages(username, chosenTable, filterByName, filterByTag, model);

        /* For editing table values */
        model.addAttribute("editable_purchase", Objects.requireNonNullElse(editablePurchaseId, "null"));
        model.addAttribute("editable_sale", Objects.requireNonNullElse(editableSaleId, "null"));

        /* For adding error messages */
        model.addAttribute("error", Objects.requireNonNullElse(error, "null"));

        accountPageService.addPurchaseTemplate(user, model);
        accountPageService.addSaleTemplate(invalidSale, user, model);

        return "account";
    }
}
