package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public RegistrationController(AccountService accountService, LoggedUserManagementService loggedUserManagementService) {
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @GetMapping
    public String getRegister() {
        return "registration.html";
    }

    @PostMapping
    public String postRegister(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String passwordConfirmation,
            Model model
    ) {
        if (!password.equals(passwordConfirmation)) {
            model.addAttribute("username", username);
            model.addAttribute("passwordError", "true");
            return "registration.html";
        }

        if (accountService.existsByUsername(username)) {
            model.addAttribute("usernameError", "true");
            return "registration.html";
        }

        accountService.registerNewAccount(Account.of(username, password));
        loggedUserManagementService.setUsername(username);
        loggedUserManagementService.setUserId(accountService.getUserIdByUsername(username));

        return "redirect:/account";
    }
}
