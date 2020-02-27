package com.webbasedsolutions.scinvestments.collections;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webbasedsolutions.scinvestments.Settings;
import com.webbasedsolutions.scinvestments.model.LiteCustomer;
import com.webbasedsolutions.scinvestments.model.Product;
import com.webbasedsolutions.scinvestments.model.Unit;
import com.webbasedsolutions.scinvestments.util.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class GlobalCollectionsSinglton implements ItemsLoaded  {
    private static GlobalCollectionsSinglton instance = null;
    private List<Product> products = null;
    private List<Unit> units = null;
    private List<LiteCustomer> liteCustomers = null;
    private boolean isReady;
    private final static String Tag = "GlobalCollectionsSinglton";
    private Settings settings;
    private List<String> invoiceNumbers;

    public void wipeAll(){
        products = null;
        liteCustomers = null;
        isReady =false;
        invoiceNumbers = null;
        units = null;
    }
    public void addListener(ItemsLoaded toAdd) {
        listeners.add(toAdd);
    }
    private List<ItemsLoaded> listeners = new ArrayList<ItemsLoaded>();
    // Private constructor.
    private GlobalCollectionsSinglton() {
        intiatialize();
    }

    public List<Product> getProducts() {
        return products;
    }
    public List<String> getProductListName(){
        List<String> prods = new ArrayList<String>();
        for (Product product : products
             ) {
            prods.add(product.getName());
        }
        return prods;
    }

    public List<String> getProductListId(){
        List<String> prods = new ArrayList<String>();
        for (Product product : products
        ) {
            prods.add(String.valueOf(product.getId()));
        }
        return prods;
    }

    public Product getProductByName(String name){
        for (Product product : products
        ) {
            if(product.getName().equals(name))
                return  product;

        }
        return null;
    }


    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    public List<Unit> getUnits() {
        return units;
    }

    private void setReady(boolean ready) {
        isReady = ready;
    }
    public boolean getReady(){
        return  isReady;
    }

    public List<String> getInvoiceNumbers() {
        return invoiceNumbers;
    }

    public void setInvoiceNumbers(List<String> invoiceNumbers) {
        this.invoiceNumbers = invoiceNumbers;
    }

    public void OnItemsLoaded() {

        // Notify everybody that may be interested.
        for (ItemsLoaded hl : listeners)
            hl.OnItemsLoaded();
    }


    public boolean isUniqueInvoiceNumber(String invoice, Context context){
        if(!isReady)
            return false;

        if(invoiceNumbers.size() > 0) {

            for (String item :
                    invoiceNumbers) {
                if (item.trim().equals(invoice))
                    return false;
            }
            return true;
        }
        else{


            String baseURL = settings.getBaseURL();

            RequestFuture<JSONObject> future = RequestFuture.newFuture();
            JsonObjectRequest request = new JsonObjectRequest(baseURL + "/invoice/isunique/" + invoice, new JSONObject(), future, future){
                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", " bearer " + settings.getSecurityToken());
                    return params;
                }
            };;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(request);
            try {
                JSONObject response = future.get(); // this will block
                return response.getBoolean("value");
            } catch (InterruptedException e) {
                // exception handling
            } catch (ExecutionException e) {
                // exception handling
            }
            catch (JSONException ex){

            }




        }
        return  false;
    }


    public List<LiteCustomer> getLiteCustomers() {
        return liteCustomers;
    }

    public void setLiteCustomers(List<LiteCustomer> liteCustomers) {
        this.liteCustomers = liteCustomers;
    }

    public Settings getSettings() {
        return settings;
    }

    public  LiteCustomer getCustomer(int customerId){
        for (LiteCustomer customer: getLiteCustomers()
             ) {
            if(customer.getId() == customerId)
                return customer;

        }
        return  null;
    }

    public  Product getProduct(int id){
        for (Product product: getProducts()
             ) {
            if(product.getId() == id)
                return product;
        }
        return null;
    }

    public Unit getUnit(int id){
        for (Unit unit:getUnits()
             ) {
            if(unit.getId() == id)
                return  unit;

        }
        return  null;
    }

    public static GlobalCollectionsSinglton getInstance() {
        // Double check locking principle.
        // If there is no instance available, create new one (i.e. lazy initialization).
        if (instance == null) {

            // To provide thread-safe implementation.
            synchronized(GlobalCollectionsSinglton.class) {

                // Check again as multiple threads can reach above step.
                if (instance == null) {
                    instance = new GlobalCollectionsSinglton();
                }
            }
        }
        return instance;
    }

    public void loadCollections(Context context, Settings settings){
        this.settings = settings;
        if(products.size() > 0 && units.size() > 0 && liteCustomers.size() > 0 && invoiceNumbers.size() > 0){
            isReady = true;
            return;
        }

        if(!isReady) {
            initProducts(context);
        }
    }

    private void intiatialize(){
        products = new ArrayList<Product>();
        units = new ArrayList<Unit>();
        liteCustomers = new ArrayList<LiteCustomer>();
        invoiceNumbers = new ArrayList<String>();
        Log.i(Tag,"Initialization is completed.");
    }

    private void initProducts(Context context){

        String baseURL = settings.getBaseURL();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + "/Products/allproducts",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //ClientContent clientContent = new ClientContent();
                            //JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray =new JSONArray(response);
                            if(jsonArray.length() > 0){
                                for (int i = 0; i<jsonArray.length();i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    Product product = new Product();
                                    product.setId(item.getInt("id"));
                                    product.setName(item.getString("name"));
                                    product.setPrice(item.getDouble("price"));
                                    product.setQuantity(item.getInt("quantity"));
                                    product.setTax(item.getDouble("tax"));
                                    product.setUnitId(item.getInt("unitId"));
                                    products.add(product);
                                }
                                Log.i(Tag,"Products loaded.");
                                initUnits(context);
                            }

                        }
                        catch (JSONException ex){
                            Log.e(Tag,ex.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parseVolleyError(error,context);
                    }
                }) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", " bearer " + settings.getSecurityToken());
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private void initUnits(Context context){

        String baseURL = settings.getBaseURL();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + "/Units/allunits",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //ClientContent clientContent = new ClientContent();
                            //JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray =new JSONArray(response);
                            if(jsonArray.length() > 0){
                                for (int i = 0; i<jsonArray.length();i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    Unit unit = new Unit(item.getInt("id"), item.getString("name"));
                                    units.add(unit);
                                }
                            }
                            Log.i(Tag,"Units loaded.");
                            initLiteCustomer(context);
                        }
                        catch (JSONException ex){
                            Log.e(Tag,ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parseVolleyError(error,context);
                        //Log.e(Tag,error.getMessage());
                    }
                }) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", " bearer " + settings.getSecurityToken());
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private void initLiteCustomer(Context context){

        String baseURL = settings.getBaseURL();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + "/Customers/allcustomers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //ClientContent clientContent = new ClientContent();
                            //JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray =new JSONArray(response);
                            if(jsonArray.length() > 0){
                                for (int i = 0; i<jsonArray.length();i++) {
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    LiteCustomer liteCustomer = new LiteCustomer(item.getInt("id"),item.getString("name"));
                                    liteCustomers.add(liteCustomer);
                                }
                                Log.i(Tag,"Customers loaded");
                                setReady(true);
                                initInvoiceNumbers(context);

                            }
                        }
                        catch (JSONException ex){
                            Log.e(Tag,ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parseVolleyError(error,context);
                        //Log.e(Tag,error.getMessage());
                    }
                }) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", " bearer " + settings.getSecurityToken());
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


    private void initInvoiceNumbers(Context context){

        String baseURL = settings.getBaseURL();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + "/Invoice/getinvoices",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //ClientContent clientContent = new ClientContent();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("invoices");
                            if(jsonArray.length() > 0){
                                for (int i = 0; i<jsonArray.length();i++) {
                                    String item = jsonArray.getString(i);

                                    invoiceNumbers.add(item);
                                }
                                Log.i(Tag,"Invoice numbers loaded.");
                              //  Log.i(Tag,String.format("Loaded %d invoices, %d customers, %d products",units.size(),liteCustomers.size(),products.size()));

                                OnItemsLoaded();
                                Log.i(Tag,String.format("Loaded %d units, %d customers, %d products, %d invoices",units.size(),liteCustomers.size(),products.size(),invoiceNumbers.size()));

                            }
                        }
                        catch (JSONException ex){
                            Log.e(Tag,ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        parseVolleyError(error,context);
                       // Log.e(Tag,error.getMessage());
                    }
                }) {
            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders () throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", " bearer " + settings.getSecurityToken());
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public void parseVolleyError(VolleyError error, Context context) {
        String message = "Failed to read data. ";
        try {
            if(error.networkResponse == null || error.networkResponse.data == null)
            {
                message = error.getMessage();
            }
            else {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                message = responseBody;
                if (Helpers.isJSONValid(responseBody)) {
                    try {
                        message = new JSONObject(responseBody).getString("Message");
                    } catch (JSONException ex) {
                        Log.e(Tag, "Error diplaying error: " + ex.getMessage());
                    }
                }


            }
            Log.d(Tag,"Failed to load something: " + message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
        catch (UnsupportedEncodingException errorr) {
            Log.e(Tag,"Failed to read data. " + errorr.getMessage());
        }
        catch (Exception ex){
            Log.e(Tag,"Failed to read data. " + ex.getMessage());
        }
    }
}
