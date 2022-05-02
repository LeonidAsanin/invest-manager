package org.lennardjones.investmanager.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class that describes total values for all current user's products.
 *
 * @since 1.4
 * @author lennardjones
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ProductTotal {
    private double price;
    private double currentPrice;
    private double absoluteProfit;
    private double relativeProfit;
}
