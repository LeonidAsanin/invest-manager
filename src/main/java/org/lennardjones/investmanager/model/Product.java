package org.lennardjones.investmanager.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents product that user currently have.
 *
 * @since 1.1
 * @author lennardjones
 */
@Getter
@Setter
public class Product {
    private String name;
    private int amount;
    private double averagePrice;
    private double currentPrice;
    private double absoluteBenefit;
    private double relativeBenefit;
}
