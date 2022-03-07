package org.lennardjones.investmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class that describes total values for all current user's products.
 *
 * @since 1.4
 * @author lennardjones
 */
@Getter
@AllArgsConstructor
public class ProductTotal {
    private double price;
    private double currentPrice;
    private double absoluteProfit;
    private double relativeProfit;
}
