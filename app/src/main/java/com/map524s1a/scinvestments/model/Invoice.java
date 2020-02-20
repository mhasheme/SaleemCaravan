package com.map524s1a.scinvestments.model;

public class Invoice {
    private String invoiceNumber;
    private boolean selected;

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean getSelected(){
        return selected;
    }

    public Invoice(){}
    public  Invoice(String invoiceNumber, boolean selected){
        this.invoiceNumber = invoiceNumber;
        this.selected = selected;
    }
    public boolean isSelected() {
        return selected;
    }
}
