package org.lennardjones.investmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main controller for handling requests on "/" address.
 *
 * @since 1.2
 * @author lennardjones
 */
@Controller
@RequestMapping("/")
public class MainController {
    @GetMapping
    public String index() {
        return "redirect:/account";
    }
}
