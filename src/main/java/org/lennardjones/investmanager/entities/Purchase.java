package org.lennardjones.investmanager.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Entity class that describes a user single purchase and is used for persisting purposes in the database.
 * Implements Cloneable interface for better integration with
 * {@link org.lennardjones.investmanager.util.PurchaseSaleUtil PurchaseSaleUtil} class.
 *
 * @since 1.0
 * @author lennardjones
 */
@Entity
@Table(name = "purchase")
@Getter
@Setter
@ToString
public class Purchase implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date")
    private LocalDate date;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "amount")
    private int amount;

    @Column(name = "commission")
    private double commission;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}