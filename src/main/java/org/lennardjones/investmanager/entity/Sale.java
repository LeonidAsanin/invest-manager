package org.lennardjones.investmanager.entity;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
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

    @Column(name = "tag")
    private String tag;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "price")
    private Double price;

    @Column(name = "commission")
    private Double commission;

    @Column(name = "absolute_profit")
    private Double absoluteProfit;

    @Column(name = "relative_profit")
    private Double relativeProfit;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("CloneNotSupportedException");
        }
    }
}