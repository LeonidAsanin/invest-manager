package org.lennardjones.investmanager.controller;

import org.lennardjones.investmanager.entity.User;
import org.lennardjones.investmanager.model.Product;
import org.lennardjones.investmanager.model.ProductTotal;
import org.lennardjones.investmanager.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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
    public String getProductPage(@RequestParam(required = false) String editable,
                                 @AuthenticationPrincipal User user,
                                 Model model) {
        if (editable != null) {
            model.addAttribute("editable", true);
        }

        var productSet = productService.getAllByUser(user);
        var sortedProductSet = productSet.stream()
                .sorted(Comparator.comparing(Product::getTag)
                        .thenComparing(Product::getName))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        model.addAttribute("productSet", sortedProductSet);

        var totalPrice = productSet.stream().mapToDouble(p -> p.getAmount() * p.getAveragePrice()).sum();
        var totalCurrentPrice = productSet.stream().mapToDouble(p -> p.getAmount() * p.getCurrentPrice()).sum();
        var totalAbsoluteProfit = productSet.stream().mapToDouble(Product::getAbsoluteProfit).sum();
        var totalRelativeProfit = (totalPrice / (totalPrice - totalAbsoluteProfit) - 1) * 100;
        var productTotal = new ProductTotal(totalPrice, totalCurrentPrice, totalAbsoluteProfit, totalRelativeProfit);
        model.addAttribute(productTotal);

        return "products";
    }

    @PostMapping("/calculate")
    public String setCurrentPricesAndCalculateBenefits(@RequestParam List<String> productName,
                                                       @RequestParam List<Double> currentPrice,
                                                       @AuthenticationPrincipal User user) {
        for (int i = 0; i < productName.size(); i++) {
            productService.calculateProfits(user.getId(), productName.get(i), currentPrice.get(i));
        }

        return "redirect:/product";
    }

    @PostMapping("/edit")
    public String editCurrentPrices() {
        return "redirect:/product?editable";
    }
}
