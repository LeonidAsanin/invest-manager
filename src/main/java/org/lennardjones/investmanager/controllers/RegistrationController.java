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

/**
 * Controller for registration procedure.
 *
 * @since 1.0
 * @author lennardjones
 */
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
    public String getRegisterPage() {
        return "registration";
    }

    @PostMapping
    public String registerNewUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String passwordConfirmation,
            Model model
    ) {
        if (accountService.existsByUsername(username)) {
            model.addAttribute("error", "Sorry, but this username already exists");
            return "registration";
        }

        if (!password.equals(passwordConfirmation)) {
            model.addAttribute("error", "Please, type in your desired password twice correctly");
            model.addAttribute("username", username);
            return "registration";
        }

        var account = new Account();
        account.setUsername(username);
        account.setPassword(password);

        accountService.registerNewAccount(account);
        loggedUserManagementService.setUserId(accountService.getUserIdByUsername(username));
        loggedUserManagementService.setUsername(username);
        loggedUserManagementService.setLoggedIn(true);

        return "redirect:/account";
    }
}
