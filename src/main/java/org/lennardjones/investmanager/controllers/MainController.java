package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final LoggedUserManagementService loggedUserManagementService;

    public MainController(LoggedUserManagementService loggedUserManagementService) {
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @GetMapping("/")
    public String getMainPage(
            @RequestParam(required = false) String logout,
            Model model
    ) {
        if (logout != null) {
            loggedUserManagementService.setLoggedIn(false);
        }

        if (loggedUserManagementService.isLoggedIn()) {
            return "redirect:/account";
        }

        model.addAttribute("account", new Account());

        return "index";
    }
}
