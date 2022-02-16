package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public LoginController(AccountService accountService, LoggedUserManagementService loggedUserManagementService) {
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model
    ) {
        if (accountService.exists(Account.of(username, password))) {
            loggedUserManagementService.setUserId(accountService.getUserIdByUsername(username));
            loggedUserManagementService.setUsername(username);
            loggedUserManagementService.setLoggedIn(true);
            return "redirect:/account";
        }

        model.addAttribute("loginError", true);
        return "index";
    }
}
