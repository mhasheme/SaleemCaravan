package com.webbasedsolutions.scinvestments.model;

public class Client {
    private int Id;
    private String Name;
    private String Phone;
    private String Email;
    private String Details;
    private int NumberOfInvoices;
    private double TotalUnpaid;
    private String Invoices;
    private double Balance;

    public void setBalance(double balance) {
        Balance = balance;
    }

    public double getBalance() {
        return Balance;
    }

    public Client(){

    }
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getInvoices() {
        return Invoices;
    }

    public void setInvoices(String invoices) {
        Invoices = invoices;
    }

    public double getTotalUnpaid() {
        return TotalUnpaid;
    }

    public void setTotalUnpaid(double totalUnpaid) {
        TotalUnpaid = totalUnpaid;
    }

    public int getNumberOfInvoices() {
        return NumberOfInvoices;
    }

    public void setNumberOfInvoices(int numberOfInvoices) {
        NumberOfInvoices = numberOfInvoices;
    }

    public Client(int id, String name, String phone, String email,  String invoices, int numberOfInvoices, double totalUnpaid) {
        this.Id = id;
        this.Name = name;
        this.Email = email;
        this.Phone = phone;
        this.NumberOfInvoices = numberOfInvoices;
        this.Invoices = invoices;
        this.TotalUnpaid = totalUnpaid;
    }

}
