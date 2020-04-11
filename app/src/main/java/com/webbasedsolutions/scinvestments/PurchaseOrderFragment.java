package com.webbasedsolutions.scinvestments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.map524s1a.scinvestments.R;
import com.webbasedsolutions.scinvestments.collections.GlobalCollectionsSinglton;
import com.webbasedsolutions.scinvestments.model.Cart;
import com.webbasedsolutions.scinvestments.model.LiteCustomer;
import com.webbasedsolutions.scinvestments.model.Product;
import com.webbasedsolutions.scinvestments.model.Purchase;
import com.webbasedsolutions.scinvestments.util.Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PurchaseOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchaseOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "item_id";

    // TODO: Rename and change types of parameters
    private String customerId;
    private LiteCustomer liteCustomer;
    private Settings settings;
    private TextView txtCustomer;
    private TextView txtDOP;
    private TextView txtRice;
    private TextView txtTea;
    private EditText txtRiceQuantity;
    private EditText txtTeaQuantity;
    private EditText txtTotal;
    private EditText txtComments;
    private Button btnPlaceOrder;
    private Purchase purchase;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final DateFormat sdf = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private EditText txtInvoiceNumbers;
    private TextView vldDOP;
    private GlobalCollectionsSinglton singlton;
    public PurchaseOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PurchaseOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PurchaseOrderFragment newInstance(String param1) {
        PurchaseOrderFragment fragment = new PurchaseOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (getArguments() != null) {
            customerId = getArguments().getString(ARG_PARAM1);
             settings = ((Settings) this.getActivity().getApplicationContext());
             try {
                 liteCustomer = settings.getGlobalCollectionsSinglton().getCustomer(Integer.parseInt(customerId));
             }
             catch (Exception ex){
                 Log.e("PurchaseOrderFramgment", ex.getMessage());
             }
        }
        singlton = GlobalCollectionsSinglton.getInstance();
    }

    private boolean isDate(String date){
        try
        {
            new SimpleDateFormat("MM/dd/yyyy").parse(date);
            return true;
        }
        catch (Exception ex)
        {
            return  false;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_purchase_order, container, false);
        Context context = view.getContext();
        Window window = getActivity().getWindow();
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        txtCustomer = view.findViewById(R.id.txtCustomer);
        txtDOP = view.findViewById(R.id.txtDOP);
        vldDOP = view.findViewById(R.id.vldDate);
        txtDOP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        if (txtDOP.getText().toString().equals("") || !isDate(txtDOP.getText().toString())) {
                            //txtDOP.requestFocus();
                            //txtInvoiceNumbers.setSelection(1);
                            Toast.makeText(context, "You must provide a validate Date", Toast.LENGTH_LONG).show();

                            vldDOP.setVisibility(View.VISIBLE);
                        }
                    }
                    catch (Exception ex){

                    }
                }
            }
        });
        txtCustomer.setText(liteCustomer.getName());
        String pattern = "MM/dd/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        txtDOP.setText( simpleDateFormat.format(new Date()));

        txtTotal = view.findViewById(R.id.txtTotal);
        txtComments = view.findViewById(R.id.txtComments);
        btnPlaceOrder = view.findViewById(R.id.btnPlaceOrder);

        txtInvoiceNumbers = view.findViewById(R.id.txtInvoiceNumber);
        txtInvoiceNumbers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    try {
                        if (!settings.getGlobalCollectionsSinglton().isUniqueInvoiceNumber(txtInvoiceNumbers.getText().toString(),context)) {
                               // txtInvoiceNumbers.requestFocus();
                            //txtInvoiceNumbers.setSelection(1);
                            Toast.makeText(context, "You must provide a unique invoice number.", Toast.LENGTH_LONG).show();

                        }
                    }
                    catch (Exception ex){

                    }
                }
            }
        });
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(validate())
                    processOrder(context);
            }
        });

        txtTotal.setText(String.valueOf(getTotal()));
        return  view;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private double getTotal(){
        double total = 0.0;
        List<Cart> carts = (List<Cart>)settings.getTransferObject();
        for (Cart cart: carts
             ) {
            for (Product product: cart.getProducts()
                 ) {
                total += product.getPrice() * product.getQuantity();
            }
        }

        return  total;

    }

    private void dismissKeyboard(){
        try {
            Window window = getActivity().getWindow();
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception ex){

        }
    }
    private boolean validate(){
        if(txtTotal.getText().length() == 0){
            txtTotal.setError("Total is required.");
            return false;
        }

        if(txtDOP.getText().length() == 0 || !Helpers.isValidDate(txtDOP.getText().toString())){
            txtDOP.setError("Transaction date is required");
            return false;
        }
        if(txtInvoiceNumbers.getText().length() == 0){
            txtInvoiceNumbers.setError("Invoice number is required.");
            return false;
        }
        if(!singlton.isUniqueInvoiceNumber(txtInvoiceNumbers.getText().toString(),getContext())){
            txtInvoiceNumbers.setError("Invoice number must be unique.");
            return false;
        }

        return  true;

    }


    private void processOrder(Context context){
        try {
            ProgressDialog dialog = new ProgressDialog(context);
            Window window = getActivity().getWindow();
            dialog.setMessage("Loading data....");
            dialog.show();
            Settings settings = ((Settings) context.getApplicationContext());
            String baseURL = settings.getBaseURL();

            String URL = baseURL + "/api/Purchase";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userid", settings.getLoginUser());
            jsonBody.put("id", "-1");
            jsonBody.put("CustId", liteCustomer.getId());
            jsonBody.put("CustomerName",liteCustomer.getName());
            jsonBody.put("InvoiceNumber", txtInvoiceNumbers.getText());
            jsonBody.put("Total", txtTotal.getText());
            List<Cart> carts = (List<Cart>)settings.getTransferObject();

            JSONArray products = new JSONArray();
            for (Cart cart:carts
                 ) {
                for (Product product: cart.getProducts()){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",product.getId());
                    jsonObject.put("Name", product.getName());
                    jsonObject.put("Price", product.getPrice());
                    jsonObject.put("Quantity", product.getQuantity());
                    jsonObject.put("Tax", product.getTax());
                    jsonObject.put("UnitId", product.getUnitId());
                    products.put(jsonObject);

                }
            }

            jsonBody.put("Products",products);

            jsonBody.put("comments", txtComments.getText());

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    mHandler.post((new Runnable() {
                        public void run() {
                            txtCustomer.setText("");
                            //txtDOP.setText("");
                            txtCustomer.setText(liteCustomer.getName());
                            String pattern = "MM/dd/yyyy";
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                            txtDOP.setText( simpleDateFormat.format(new Date()));
                            //txtRice = view.findViewById(R.id.txtRice);
                            //txtTea = view.findViewById(R.id.txtTea);

                            txtTotal.setText("");
                            txtComments.setText("");
                            txtInvoiceNumbers.setText("");
                            txtComments.setText("");
                            Toast.makeText(context, "Order processed successfully!", Toast.LENGTH_LONG).show();

                            Bundle bundle=new Bundle();
                            bundle.putString(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(liteCustomer.getId()));

                            // your handler code here
                            // Create new fragment and transaction
                            Fragment newFragment = new ClientDetailFragment();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            newFragment.setArguments(bundle);
                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack
                            transaction.replace(R.id.client_detail_container,newFragment );
                            transaction.addToBackStack(null);

                            // Commit the transaction
                            transaction.commit();

                        }
                    }));
                    try {
                        Toast.makeText(context, response.getString("message").toString(), Toast.LENGTH_LONG).show();
                    }
                    catch (Exception ex){

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    dismissKeyboard();
                    parseVolleyError(error,context);
                    //InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                   // inputManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }) {
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", " bearer " + settings.getSecurityToken());
                    return params;
                }
            };
            // VolleyApplication.getInstance().
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonOblect);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
   

    public void parseVolleyError(VolleyError error, Context context) {
        String message = "";
        dismissKeyboard();
        try {
            if( error.networkResponse == null || error.networkResponse.data == null){
                message = error.getMessage();
            }
            else {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                message = responseBody;
                if (Helpers.isJSONValid(responseBody)) {
                    try {
                        message = new JSONObject(responseBody).getString("message");
                    } catch (JSONException ex) {

                    }
                }
            }
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        }
        catch (UnsupportedEncodingException errorr) {
            Log.e("PurchaseOrderFrangment","Failed to Process Order: " + errorr.getMessage());
            Toast.makeText(context, "Failed to Process Order: " + errorr.getMessage(), Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            Log.e("PurchaseOrderFrangment","Failed to Process Order: " + ex.getMessage());
            Toast.makeText(context, "Failed to Process Order: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
