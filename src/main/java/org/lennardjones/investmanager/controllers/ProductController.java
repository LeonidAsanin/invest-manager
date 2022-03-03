package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller for working with products that user currently have.
 *
 * @since 1.1
 * @author lennardjones
 */
@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getProductPage(@RequestParam(required = false) String editable, Model model) {
        if (editable != null) {
            model.addAttribute("editable", true);
        }

        model.addAttribute("productSet", productService.getAll());
        return "products";
    }

    @PostMapping("/calculate")
    public String setCurrentPricesAndCalculateBenefits(@RequestParam List<String> productName,
                                                      @RequestParam List<Double> currentPrice) {
        for (int i = 0; i < productName.size(); i++) {
            productService.calculateBenefitsByName(productName.get(i), currentPrice.get(i));
        }

        return "redirect:/product";
    }

    @PostMapping("/edit")
    public String editCurrentPrices() {
        return "redirect:/product?editable";
    }
}
