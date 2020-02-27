package com.webbasedsolutions.scinvestments.model;

import java.util.List;

public class Purchase {
    private int Id;
    private String CustomerName;
    private  int CustId;
    private List<Product> Products;
    private String InvoiceNumber;
    private double Total;
    private String Comments;

    public  Purchase(){

    }
    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public double getTotal() {
        return Total;
    }

    public void setTotal(double total) {
        Total = total;
    }

    public int getCustId() {
        return CustId;
    }

    public void setCustId(int custId) {
        CustId = custId;
    }

    public List<Product> getProducts() {
        return Products;
    }

    public void setProducts(List<Product> products) {
        Products = products;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        InvoiceNumber = invoiceNumber;
    }

}
