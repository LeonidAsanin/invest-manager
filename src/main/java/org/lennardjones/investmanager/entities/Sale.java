package org.lennardjones.investmanager.entities;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "sale")
public class Sale implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Account seller;

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

    @Column(name = "absolute_benefit")
    private double absoluteBenefit;

    @Column(name = "relative_benefit")
    private double relativeBenefit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getSeller() {
        return seller;
    }

    public void setSeller(Account seller) {
        this.seller = seller;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public double getAbsoluteBenefit() {
        return absoluteBenefit;
    }

    public void setAbsoluteBenefit(double absoluteBenefit) {
        this.absoluteBenefit = absoluteBenefit;
    }

    public double getRelativeBenefit() {
        return relativeBenefit;
    }

    public void setRelativeBenefit(double relativeBenefit) {
        this.relativeBenefit = relativeBenefit;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", seller=" + seller +
                ", date=" + date +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", amount=" + amount +
                ", commission=" + commission +
                ", absoluteBenefit=" + absoluteBenefit +
                ", relativeBenefit=" + relativeBenefit +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}