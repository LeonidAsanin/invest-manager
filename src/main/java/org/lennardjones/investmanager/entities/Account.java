package org.lennardjones.investmanager.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

/**
 * Entity class that describes a user account and is used for persisting purposes in the database.
 *
 * @since 1.0
 * @author lennardjones
 */
@Entity
@Table(name = "account")
@Getter
@Setter
@ToString
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Purchase> purchaseList;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Sale> saleList;
}