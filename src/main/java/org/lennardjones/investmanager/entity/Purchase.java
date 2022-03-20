package org.lennardjones.investmanager.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class that describes a user single purchase and is used for persisting purposes in the database.
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
public class Purchase implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

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

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}