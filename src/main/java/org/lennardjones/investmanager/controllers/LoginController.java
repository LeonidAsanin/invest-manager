package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.Account;
import org.lennardjones.investmanager.services.AccountService;
import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for login procedure.
 *
 * @since 1.0
 * @author lennardjones
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private final AccountService accountService;
    private final LoggedUserManagementService loggedUserManagementService;

    public LoginController(AccountService accountService, LoggedUserManagementService loggedUserManagementService) {
        this.accountService = accountService;
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @GetMapping
    public String getLoginPage(
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

        return "login";
    }

    @PostMapping
    public String login(
            Account account,
            BindingResult bindingResult
    ) {
        if (!accountService.exists(account)) {
            var error = new ObjectError("login", "Invalid credentials");
            bindingResult.addError(error);
            return "login";
        }

        var username = account.getUsername();
        var userId = accountService.getUserIdByUsername(username);
        loggedUserManagementService.setUserId(userId);
        loggedUserManagementService.setUsername(username);
        loggedUserManagementService.setLoggedIn(true);

        return "redirect:/account";
    }
}
