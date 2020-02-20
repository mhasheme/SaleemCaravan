package com.map524s1a.scinvestments.model;


public class Payment {

    private int Id;
    private String CustomerName;
    private  int CustId;
    private String InvoiceNumber;
    private double Amount;
    private String Comments;

    public  Payment(){

    }
    public void setId(int id) {
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double total) {
        Amount = total;
    }

    public int getCustId() {
        return CustId;
    }

    public void setCustId(int custId) {
        CustId = custId;
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

    public Payment(int id, String name, int custId, double amount, String invoiceNumber, String comments) {
        this.Id = id;
        this.CustId = custId;
        this.CustomerName = name;
        this.Amount = amount;
        this.InvoiceNumber = invoiceNumber;
        this.Comments = comments;
    }
}
