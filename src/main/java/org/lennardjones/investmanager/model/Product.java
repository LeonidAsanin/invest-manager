package org.lennardjones.investmanager.model;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class Product {
    private String name;
    private String tag;
    private int amount;
    private double averagePrice;
    private double currentPrice;
    private double absoluteProfit;
    private double relativeProfit;
}
