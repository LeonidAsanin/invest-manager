package org.lennardjones.investmanager.controllers;

import org.lennardjones.investmanager.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getProductPage(Model model) {
        model.addAttribute("productList", productService.getAll());
        return "products";
    }

    @PostMapping
    public String setCurrentPrice(@RequestParam String productName, @RequestParam double currentPrice) {
        productService.setCurrentPriceByName(productName, currentPrice);
        return "products";
    }
}
