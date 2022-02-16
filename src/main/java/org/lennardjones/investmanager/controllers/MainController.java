package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.services.LoggedUserManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final LoggedUserManagementService loggedUserManagementService;

    public MainController(LoggedUserManagementService loggedUserManagementService) {
        this.loggedUserManagementService = loggedUserManagementService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String logout) {
        if (logout != null) {
            loggedUserManagementService.setLoggedIn(false);
        }

        if (loggedUserManagementService.isLoggedIn()) {
            return "redirect:/account";
        }

        return "index";
    }
}
