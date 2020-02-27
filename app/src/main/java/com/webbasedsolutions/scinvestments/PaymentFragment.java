package com.webbasedsolutions.scinvestments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.webbasedsolutions.scinvestments.model.Invoice;
import com.webbasedsolutions.scinvestments.model.Payment;
import com.webbasedsolutions.scinvestments.model.PaymentContent;
import com.webbasedsolutions.scinvestments.util.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PaymentFragment extends Fragment implements ItemSelected {

    public static final String ARG_ITEM_ID = "item_id";
    private Payment mItem;
    private TextView txtCompany;
    private EditText txtAmount;
    private EditText txtIvoices;
    private EditText txtComments;
    private Button btnSave;
    private static final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private Handler mHandler = new Handler(Looper.getMainLooper());
    public static PaymentFragment newInstance() {
        return new PaymentFragment();
    }
    private  Spinner spinner;
    private ArrayList<Invoice> invoices;

    @Override
    public void OnItemSelected() {
        txtIvoices.setText("");
        boolean first = true;
        for (int i = 0; i < invoices.size(); i++) {
            if(invoices.get(i).getSelected()) {
                if(first) {
                    txtIvoices.setText(invoices.get(i).getInvoiceNumber());
                    first = false;
                }
                else
                    txtIvoices.setText(txtIvoices.getText() + "," +invoices.get(i).getInvoiceNumber());
            }
        }
        txtComments.setText("Paying Invoices: " + txtIvoices.getText());
        validate();
    }

    private  void  validate(){
        if(txtIvoices.getText().length() > 0 && txtAmount.getText().length() > 0)
            btnSave.setEnabled(true);
        else
            btnSave.setEnabled(false);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = PaymentContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.payment_fragment, container, false);
        txtCompany = (TextView)rootView.findViewById(R.id.txtCompanyName);
        txtAmount = (EditText) rootView.findViewById(R.id.txtTotal);
        txtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>12) {
                    txtAmount.setError("It allows only 5 character");
                }else{
                    txtAmount.setError(null);
                }
                validate();
            }
        });
        txtIvoices = (EditText) rootView.findViewById(R.id.txtInvoices);
        txtComments = (EditText) rootView.findViewById(R.id.txtComments);
        btnSave = (Button) rootView.findViewById(R.id.btnSavePayment);
        Context context = rootView.getContext();
        String invoicesList ="";
        btnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // your handler code here
                makePayment(context);
            }
        });

        if (mItem != null) {
            txtCompany.setText(mItem.getCustomerName());
           // txtAmount.setText(String.valueOf(mItem.getAmount()));
            //txtIvoices.setText(mItem.getInvoiceNumber());
            txtComments.setText(mItem.getComments());
        }

        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        if(!mItem.getInvoiceNumber().equals("")) {
            String[] select_invoices = null;
            if(mItem.getInvoiceNumber().indexOf(';') > -1) {
                invoicesList = "Select an Invoice;" + mItem.getInvoiceNumber();
                select_invoices = invoicesList.split(";");
            }
            else{
                select_invoices = new String[]{"Select an Invoice", mItem.getInvoiceNumber()};
            }

            invoices = new ArrayList<>();

            for (int i = 0; i < select_invoices.length; i++) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceNumber(select_invoices[i]);
                invoice.setSelected(false);
                if(!invoices.contains(invoice))
                    invoices.add(invoice);
            }
            InvoiceAdapter myAdapter = new InvoiceAdapter(context,0,
                    invoices);
            myAdapter.addListener(this);
            spinner.setAdapter(myAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    // your code here
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here

                }

            });
        }

        return  rootView;
    }



    private void makePayment(Context context){
        try {
            ProgressDialog dialog = new ProgressDialog(context);
            Window window = getActivity().getWindow();
            dialog.setMessage("Loading data....");
            dialog.show();
            Settings settings = ((Settings) context.getApplicationContext());
            String baseURL = settings.getBaseURL();

            String URL = baseURL + "/Payment/makepayment";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("userid", settings.getLoginUser());
            Date date = new Date();
            System.out.println(sdf.format(date));

            jsonBody.put("lastUpdated", sdf.format(date));
            jsonBody.put("customerName", mItem.getCustomerName());
            jsonBody.put("custId", mItem.getCustId());
            jsonBody.put("invoiceNumber", txtIvoices.getText().toString().replace(" ",""));
            jsonBody.put("amount", txtAmount.getText());
            jsonBody.put("comments", txtComments.getText());

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    mHandler.post((new Runnable() {
                        public void run() {
                            //txtCompany.setText("");
                            txtAmount.setText("");
                            txtIvoices.setText("");
                            txtComments.setText("");
                        }
                    }));
                    Toast.makeText(context, "Payment processed successfully!", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    parseVolleyError(error,context);
                    InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

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
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            String message = responseBody;
            if(Helpers.isJSONValid(responseBody)){
                try {
                    message = new JSONObject(responseBody).getString("Message");
                }
                catch (JSONException ex){

                }
            }

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        }
        catch (UnsupportedEncodingException errorr) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

}
