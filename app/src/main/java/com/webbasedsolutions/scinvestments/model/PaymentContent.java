package com.webbasedsolutions.scinvestments.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PaymentContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Payment> ITEMS = new ArrayList<Payment>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Payment> ITEM_MAP = new HashMap<String, Payment>();

    private static final int COUNT = 25;


    public static void addItem(Payment item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.getId()), item);
    }

    private static Payment createPayment(int position,String name, int id, double amount, String invoices, String comments) {
        return new Payment(position,name,id,amount,invoices, comments);
    }



}
