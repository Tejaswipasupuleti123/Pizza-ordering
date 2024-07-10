package com.example.tejaswi;

import javafx.beans.property.*;

public class PizzaOrder {

    private final IntegerProperty id;
    private final StringProperty customer;
    private final StringProperty mobile;
    private final StringProperty size;
    private final IntegerProperty toppings;
    private final DoubleProperty totalBill;

    public PizzaOrder(int id, String customer, String mobile, String size, int toppings, double totalBill) {
        this.id = new SimpleIntegerProperty(id);
        this.customer = new SimpleStringProperty(customer);
        this.mobile = new SimpleStringProperty(mobile);
        this.size = new SimpleStringProperty(size);
        this.toppings = new SimpleIntegerProperty(toppings);
        this.totalBill = new SimpleDoubleProperty(totalBill);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getCustomer() {
        return customer.get();
    }

    public void setCustomer(String customer) {
        this.customer.set(customer);
    }

    public StringProperty customerProperty() {
        return customer;
    }

    public String getMobile() {
        return mobile.get();
    }

    public void setMobile(String mobile) {
        this.mobile.set(mobile);
    }

    public StringProperty mobileProperty() {
        return mobile;
    }

    public String getSize() {
        return size.get();
    }

    public void setSize(String size) {
        this.size.set(size);
    }

    public StringProperty sizeProperty() {
        return size;
    }

    public int getToppings() {
        return toppings.get();
    }

    public void setToppings(int toppings) {
        this.toppings.set(toppings);
    }

    public IntegerProperty toppingsProperty() {
        return toppings;
    }

    public double getTotalBill() {
        return totalBill.get();
    }

    public void setTotalBill(double totalBill) {
        this.totalBill.set(totalBill);
    }

    public DoubleProperty totalBillProperty() {
        return totalBill;
    }
}
