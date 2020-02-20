package com.map524s1a.scinvestments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.map524s1a.scinvestments.collections.GlobalCollectionsSinglton;
import com.map524s1a.scinvestments.collections.ItemsLoaded;
import com.map524s1a.scinvestments.model.Client;
import com.map524s1a.scinvestments.model.ClientContent;
import com.map524s1a.scinvestments.model.Payment;
import com.map524s1a.scinvestments.model.PaymentContent;
import com.map524s1a.scinvestments.util.Helpers;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * A fragment representing a single Client detail screen.
 * This fragment is either contained in a {@link ClientListActivity}
 * in two-pane mode (on tablets) or a {@link ClientDetailActivity}
 * on handsets.
 */
public class ClientDetailFragment extends Fragment{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private TextView invoices;
    private TextView unpaid;
    private TextView numberOfInvoices;
    private Button btnSendInvoice;
    private Button btnPrintInvoice;
    private Button btnPlaceOrder;
    private Button btnMakePayment;
    private SharedPreferences globalSetting;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isReady = false;
    private Settings settings;
    private final String Tag = "ClientDetailFragment";
    /**
     * The dummy content this fragment is presenting.
     */
    private Client mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ClientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ClientContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getPhone());
            }
        }
        globalSetting = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        settings = ((Settings) getContext().getApplicationContext());


    }



    private void dissMissKeyboard(){
        try {
            Window window = getActivity().getWindow();
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception ex){}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.client_detail, container, false);
        dissMissKeyboard();
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.PhoneText)).setText(mItem.getPhone());
            ((TextView) rootView.findViewById(R.id.companyNameText)).setText(mItem.getName());
            ((TextView) rootView.findViewById(R.id.EmailText)).setText(mItem.getEmail());
        }

        btnPlaceOrder = rootView.findViewById(R.id.btnPlaceOrder);
        btnPrintInvoice = rootView.findViewById(R.id.btnPrintInvoice);
        btnSendInvoice = rootView.findViewById(R.id.btnSendInvoice);
        btnMakePayment = rootView.findViewById(R.id.btnMakePayment);
        Context context = rootView.getContext();

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(mItem.getId()));

                // your handler code here
                // Create new fragment and transaction
                Fragment newFragment = new OrderFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                newFragment.setArguments(bundle);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.client_detail_container,newFragment );
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        btnSendInvoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(mItem.getId()));

                // your handler code here
                // Create new fragment and transaction
                Fragment newFragment = new SendInvoiceFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                newFragment.setArguments(bundle);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.client_detail_container,newFragment );
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });


        btnPrintInvoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //getInvoice(context);
                downloadPDF(context);
            }
        });

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                com.map524s1a.scinvestments.model.PaymentContent.ITEMS.clear();
                Payment payment = new Payment();
                payment.setAmount(mItem.getTotalUnpaid());
                payment.setComments("");
                payment.setId(mItem.getId());
                payment.setCustId(mItem.getId());
                payment.setCustomerName(mItem.getName());
                payment.setInvoiceNumber(mItem.getInvoices());
                PaymentContent.addItem(payment);
                bundle.putString(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(mItem.getId()));

                // your handler code here
                // Create new fragment and transaction
                Fragment newFragment = new PaymentFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                newFragment.setArguments(bundle);
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.client_detail_container,newFragment );
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });






        numberOfInvoices =  ((TextView) rootView.findViewById(R.id.NumberOfInvoicesText));
        unpaid = ((TextView) rootView.findViewById(R.id.TotalUnpaidText));
        invoices = ((TextView) rootView.findViewById(R.id.InvoicesText));
        numberOfInvoices.setText(String.valueOf(mItem.getNumberOfInvoices()));
        unpaid.setText(Helpers.formatDecimal((float) mItem.getBalance()));
        invoices.setText(mItem.getInvoices().replace(";", ", "));


        btnPlaceOrder.setEnabled(false);

        if (mItem != null && mItem.getTotalUnpaid() == 0) {
            btnMakePayment.setEnabled(false);
            btnSendInvoice.setEnabled(false);
            btnPrintInvoice.setEnabled(false);
        }

        if(!GlobalCollectionsSinglton.getInstance().getReady()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Running: " + new java.util.Date());
                    try {
                        if (settings.getGlobalCollectionsSinglton().getReady()) {
                            mHandler.post((new Runnable() {
                                public void run() {
                                    btnPlaceOrder.setEnabled(true);

                                    if (mItem != null && mItem.getTotalUnpaid() > 0) {
                                        btnMakePayment.setEnabled(true);
                                        btnSendInvoice.setEnabled(true);
                                        btnPrintInvoice.setEnabled(true);
                                    }
                                }
                            }));

                            timer.cancel();
                            timer.purge();
                        } else {
                            mHandler.post((new Runnable() {
                                public void run() {
                                    btnPlaceOrder.setEnabled(false);

                                    btnMakePayment.setEnabled(false);
                                    btnSendInvoice.setEnabled(false);
                                    btnPrintInvoice.setEnabled(false);
                                }
                            }));
                        }
                    } catch (Exception ex) {
                        Log.e(Tag, ex.getMessage());
                    }
                }
            }, 0, 1000);
        }
        else{
            btnPlaceOrder.setEnabled(true);

            if (mItem != null && mItem.getTotalUnpaid() == 0) {
                btnMakePayment.setEnabled(false);
                btnSendInvoice.setEnabled(false);
                btnPrintInvoice.setEnabled(false);
            }
        }

        //setAccountInfo(mItem, context );
        return rootView;
    }



    private void downloadPDF(Context context){
        try {
            ProgressDialog dialog = new ProgressDialog(context);
            Window window = getActivity().getWindow();
            dialog.setMessage("Loading data....");
            dialog.show();
            settings = ((Settings) context.getApplicationContext());
            String baseURL = settings.getBaseURL();

            String URL = baseURL + "/Invoice/getinvoice";
            JSONObject jsonBody = new JSONObject();
            SharedPreferences.Editor editor = globalSetting.edit();
            String username = globalSetting.getString("username","administrator");

            jsonBody.put("userid",username );



            jsonBody.put("custId", mItem.getId());


            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    try {
                        if (response!=null) {

                            FileOutputStream outputStream;
                            String name="bill" + mItem.getId() + ".pdf";
                            outputStream = context.openFileOutput(name, Context.MODE_PRIVATE);
                            //byte[] encoded = Base64.encode(response.getString("Data").getBytes(),0,response.getString("Data").getBytes().length,NO_CLOSE);
                            //byte[] decodedString = Base64.decodeBase64(new String(name).getBytes("UTF-8"));

                            outputStream.write(Base64.decode(response.getString("data"), Base64.NO_WRAP)); //.toString().getBytes(StandardCharsets.UTF_8));
                            outputStream.close();
                            Toast.makeText(context, "Download complete.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                        e.printStackTrace();
                    }
                    mHandler.post((new Runnable() {
                        public void run() {
                            Bundle bundle=new Bundle();
                            bundle.putString("custId", String.valueOf(mItem.getId()));

                            // your handler code here
                            // Create new fragment and transaction
                            Fragment newFragment = new ViewPDFFragment();
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
                    Toast.makeText(context, "Downloaded invoice successfully!", Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    parseVolleyError(error,context);
                   dissMissKeyboard();

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

            jsonOblect.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // VolleyApplication.getInstance().
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonOblect);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }




    public void parseVolleyError(VolleyError error, Context context) {
        String message = "Failed to get client data. ";
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
            Log.d(Tag,"Failed to get client data: " + message);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
        catch (UnsupportedEncodingException errorr) {
            Log.e(Tag,"Failed to get client data. " + errorr.getMessage());
        }
        catch (Exception ex){
            Log.e(Tag,"Failed to get client data " + ex.getMessage());
        }
    }


}
