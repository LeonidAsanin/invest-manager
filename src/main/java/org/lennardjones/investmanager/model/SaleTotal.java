package org.lennardjones.investmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class that describes total values for all sales.
 *
 * @since 1.4
 * @author lennardjones
 */
@Getter
@AllArgsConstructor
public class SaleTotal {
    private int amount;
    private double price;
    private double commission;
    private double absoluteProfit;
    private double relativeProfit;
}
