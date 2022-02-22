package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public LoginController(AccountService accountService, LoggedUserManagementService loggedUserManagementService) {
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @GetMapping()
    public String backToLoginForm() {
        return "redirect:/";
    }

    @PostMapping()
    public String login(
            Account account,
            BindingResult bindingResult
    ) {
        if (!accountService.exists(account)) {
            var error = new ObjectError("login", "Invalid credentials");
            bindingResult.addError(error);
            return "index";
        }

        var username = account.getUsername();
        var userId = accountService.getUserIdByUsername(username);
        loggedUserManagementService.setUserId(userId);
        loggedUserManagementService.setUsername(username);
        loggedUserManagementService.setLoggedIn(true);

        return "redirect:/account";
    }
}
