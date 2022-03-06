package org.lennardjones.investmanager.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class that describes a user single sale and is used for persisting purposes in the database.
 * Implements Cloneable interface for better integration with
 * {@link org.lennardjones.investmanager.util.PurchaseSaleUtil PurchaseSaleUtil} class.
 *
 * @since 1.0
 * @author lennardjones
 */
@Entity
@Getter
@Setter
@ToString
public class Sale implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "name")
    private String name;

    @Column(name = "amount")
    private int amount;

    @Column(name = "price")
    private double price;

    @Column(name = "commission")
    private double commission;

    @Column(name = "absolute_benefit")
    private double absoluteBenefit;

    @Column(name = "relative_benefit")
    private double relativeBenefit;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}