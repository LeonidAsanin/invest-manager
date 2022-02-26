package org.lennardjones.investmanager.model;

import lombok.Getter;
import lombok.Setter;

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
