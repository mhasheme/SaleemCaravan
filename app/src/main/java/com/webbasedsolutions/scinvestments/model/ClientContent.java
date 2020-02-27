package com.webbasedsolutions.scinvestments.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<Client> ITEMS = new ArrayList<Client>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, Client> ITEM_MAP = new HashMap<String, Client>();

    private static final int COUNT = 25;

    public void LoadContent(String startsWith){

    }

    public static void addItem(Client item) {
        ITEMS.add(item);
        ITEM_MAP.put(String.valueOf(item.getId()), item);
    }
    public static void updateItem(Client item){
        ITEMS.remove(item);
        ITEM_MAP.remove(String.valueOf(item.getId()),item);
        addItem(item);
    }

    private static Client createClient(int position,String name, String email, String phone) {
        return new Client(position,name,email,phone,"",0,0);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }


}
