package com.webbasedsolutions.scinvestments.model;

public class Product {
    private int Id;
    private String Name;
    private double Price;
    private int Quantity;
    private  double Tax;
    private  int UnitId;

    public  Product(){

    }

    public Product(int id, String name, double price, int quantity, double tax, int unitId){
        this.Id = id;
        this.Name = name;
        this.Price = price;
        this.Quantity = quantity;
        this.Tax = tax;
        this.UnitId = unitId;
    }

    public int getUnitId() {
        return UnitId;
    }

    public void setUnitId(int unitId) {
        UnitId = unitId;
    }

    public int getId(){
        return Id;
    }
    public void setId(int id){
        this.Id = id;
    }

    public String getName(){
        return Name;
    }

    public void setName(String name){
        this.Name = name;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getPrice() {
        return Price;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public double getTax() {
        return Tax;
    }

    public void setTax(double tax) {
        Tax = tax;
    }
}
