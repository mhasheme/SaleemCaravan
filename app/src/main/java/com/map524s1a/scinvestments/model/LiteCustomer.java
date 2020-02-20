package com.map524s1a.scinvestments.model;

public class LiteCustomer {
    private int Id;
    private String Name;

    public void setName(String name) {
        Name = name;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public int getId() {
        return Id;
    }

    public  LiteCustomer(int id, String name){
        Id = id;
        Name = name;
    }

    @Override
    public String toString() {
        return "Id: " + Id + " Name: " + Name;
    }
}
