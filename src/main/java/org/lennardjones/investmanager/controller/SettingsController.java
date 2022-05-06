package org.lennardjones.investmanager.controller;

import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller class for implementing account settings functionality.
 *
 * @since 1.3
 * @author lennardjones
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String getPage(@RequestParam(required = false) String editableUsername,
                          @RequestParam(required = false) String editablePassword,
                          @RequestParam(required = false) String intentionToDeleteAccount,
                          @RequestParam(required = false) String error,
                          @AuthenticationPrincipal User user, Model model) {
        if (editableUsername != null) model.addAttribute("editableUsername", true);
        if (editablePassword != null) model.addAttribute("editablePassword", true);
        if (intentionToDeleteAccount != null) model.addAttribute("intentionToDeleteAccount",
                                                                 true);
        if (error != null) model.addAttribute("error", error);

        model.addAttribute("username", user.getUsername());

        return "settings";
    }

    @PostMapping("/editUsername")
    public String editUsername() {
        return "redirect:/settings?editableUsername";
    }

    @PostMapping("/saveNewUsername")
    public String saveNewUsername(@RequestParam String username, @AuthenticationPrincipal User user) {
        if (username.equals(user.getUsername())) return "redirect:/settings";
        if (userService.existsByUsername(username)) return "redirect:/settings?error=editUsername";

        user.setUsername(username);
        userService.update(user);

        return "redirect:/settings";
    }

    @PostMapping("/editPassword")
    public String editPassword() {
        return "redirect:/settings?editablePassword";
    }

    @PostMapping("/saveNewPassword")
    public String saveNewPassword(@RequestParam String password,
                                  @RequestParam String passwordConfirmation,
                                  @AuthenticationPrincipal User user) {
        if (!password.equals(passwordConfirmation)) return "redirect:/settings?error=editPassword";

        user.setPassword(passwordEncoder.encode(password));
        userService.update(user);

        return "redirect:/settings";
    }

    @GetMapping("/deleteAccount")
    public String deleteAccount(@RequestParam(required = false) String confirmation,
                                @AuthenticationPrincipal User user) {
        if (confirmation == null) return "redirect:/settings?intentionToDeleteAccount";

        userService.deleteById(user.getId());
        SecurityContextHolder.clearContext();

        return "redirect:/login";
    }
}
