package com.map524s1a.scinvestments.model;

import java.util.List;

public class Cart {
    private int CustId;
    private List<Product> products;

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setCustId(int custId) {
        CustId = custId;
    }

    public int getCustId() {
        return CustId;
    }

    public Cart(){

    }

    public Cart(int custId, List<Product> products){
        this.CustId = custId;
        this.products = products;

    }
}
