package com.ocbc.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    private double balance;

    private String payDebtTo;

    private double debtBalance;

    private String oweDebtFrom;


    public User() {

    }

    public User(String firstName, double balance, String payDebtTo,double debtBalance , String oweDebtFrom) {
        super();
        this.firstName = firstName;
        this.balance = balance;
        this.payDebtTo = payDebtTo;
        this.oweDebtFrom = oweDebtFrom;
        this.debtBalance = debtBalance;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getPayDebtTo() {
        return payDebtTo;
    }

    public void setPayDebtTo(String payDebtTo) {
        this.payDebtTo = payDebtTo;
    }

    public double getDebtBalance() {
        return debtBalance;
    }

    public void setDebtBalance(double debtBalance) {
        this.debtBalance = debtBalance;
    }

    public String getOweDebtFrom() {
        return oweDebtFrom;
    }

    public void setOweDebtFrom(String oweDebtFrom) {
        this.oweDebtFrom = oweDebtFrom;
    }
}
