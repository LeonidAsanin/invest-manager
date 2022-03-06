package org.lennardjones.investmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This class represents product that user currently have.
 *
 * @since 1.1
 * @author lennardjones
 */
@Getter
@Setter
@ToString
public class Product {
    private String name;
    private int amount;
    private double averagePrice;
    private double currentPrice;
    private double absoluteProfit;
    private double relativeProfit;
}
