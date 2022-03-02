package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for displaying login page.
 *
 * @since 1.0
 * @author lennardjones
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping
    public String getLoginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) model.addAttribute("error", "Invalid Credentials");
        model.addAttribute("user", new User());
        return "login";
    }
}
