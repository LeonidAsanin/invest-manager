package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.lennardjones.investmanager.services.PurchaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountController {
    private final LoggedUserManagementService loggedUserManagementService;
    private final PurchaseService purchaseService;

    public AccountController(LoggedUserManagementService loggedUserManagementService, PurchaseService purchaseService) {
        this.loggedUserManagementService = loggedUserManagementService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/account")
    public String accountPage(
            @RequestParam(name = "editable", required = false) Long editableElementId,
            Model model
    ) {
        if (loggedUserManagementService.getUsername() == null) {
            return "redirect:/";
        }

        model.addAttribute("username", loggedUserManagementService.getUsername());

        var purchaseList = purchaseService.getListByOwnerId(loggedUserManagementService.getUserId());
        model.addAttribute("purchaseList", purchaseList);
        model.addAttribute("editable", editableElementId);

        return "account.html";
    }
}
