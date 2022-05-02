package org.lennardjones.investmanager.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class that describes total values for all purchases.
 *
 * @since 1.4
 * @author lennardjones
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PurchaseTotal {
    private double price;
    private double commission;
}
