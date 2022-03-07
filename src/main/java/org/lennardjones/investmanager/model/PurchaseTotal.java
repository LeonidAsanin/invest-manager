package org.lennardjones.investmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class that describes total values for all purchases.
 *
 * @since 1.4
 * @author lennardjones
 */
@Getter
@AllArgsConstructor
public class PurchaseTotal {
    private double price;
    private double commission;
}
