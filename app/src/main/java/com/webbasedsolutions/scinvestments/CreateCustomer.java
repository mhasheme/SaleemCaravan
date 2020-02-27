package com.webbasedsolutions.scinvestments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.map524s1a.scinvestments.R;
import com.webbasedsolutions.scinvestments.util.Helpers;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateCustomer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateCustomer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateCustomer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button btnSave;
    private EditText txtCompanyName;
    private EditText txtPhone;
    private EditText txtEmail;
    private EditText txtStreetAddress;
    private EditText txtUnit;
    private EditText txtPostal;
    private EditText txtCity;
    private Spinner spProvince;
    private Spinner spCountry;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean processing = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CreateCustomer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateCustomer.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateCustomer newInstance(String param1, String param2) {
        CreateCustomer fragment = new CreateCustomer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private  boolean validate(){
        if(processing){
            return true;
        }
        boolean isValid = true;
        if(txtCompanyName.getText().length() < 1 || txtPhone.getText().length() < 1 || txtEmail.getText().length() < 1
            || !Helpers.isValidEmail(txtEmail.getText().toString())){
            btnSave.setEnabled(false);
            isValid = false;
            if(txtCompanyName.getText().length() < 1){
                txtCompanyName.setError("Company Name is required!");
            }
            else {
                txtCompanyName.setError(null);
            }

            if(txtPhone.getText().length() < 1){
                txtPhone.setError("Phone is required!");
            }
            else {
                txtPhone.setError(null);
            }
            if(txtEmail.getText().length() < 1){
                txtEmail.setError("Email is required!");
            }
            else {
                txtEmail.setError(null);
            }

            if(!Helpers.isValidEmail(txtEmail.getText().toString())){
                txtEmail.setError("Invalid email address!");
            }
            else {
                txtEmail.setError(null);
            }
        }


        else{
            btnSave.setEnabled(true);
            isValid = true;
        }
        return  isValid;




    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_createcustomer, container, false);

         txtCompanyName =  ((EditText) rootView.findViewById(R.id.custName));
         txtCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        });
         txtPhone =   ((EditText) rootView.findViewById(R.id.PhoneText));

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        });

         txtEmail = ((EditText) rootView.findViewById(R.id.EmailText));
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                validate();
            }
        });
         txtStreetAddress =  ((EditText) rootView.findViewById(R.id.streetAddressText));
         txtUnit =  ((EditText) rootView.findViewById(R.id.UnitText));
         txtPostal =  ((EditText) rootView.findViewById(R.id.PostalText));
         txtCity =  ((EditText) rootView.findViewById(R.id.CityText));
         txtCity.setText("Toronto");
         spProvince =  ((Spinner) rootView.findViewById(R.id.province));
         spCountry =  ((Spinner) rootView.findViewById(R.id.countryList));
         spProvince.setSelection(7);
         spCountry.setSelection(37);
        Context context = rootView.getContext();
         btnSave = ((Button) rootView.findViewById(R.id.btnSave));
            btnSave.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if(validate())
                        createCustomer(context);
                }
            });
        return rootView;
    }

    private void createCustomer(Context context){
        try {
            processing = true;
            ProgressDialog dialog = new ProgressDialog(context);
            Window window = getActivity().getWindow();
            dialog.setMessage("Loading data....");
            dialog.show();
            Settings settings = ((Settings) context.getApplicationContext());
            String baseURL = settings.getBaseURL();

            String URL = baseURL + "/Customers";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("name", txtCompanyName.getText());
            jsonBody.put("phone", txtPhone.getText());
            jsonBody.put("email", txtEmail.getText());
            jsonBody.put("streetAddress", txtStreetAddress.getText());
            jsonBody.put("unit", txtUnit.getText());
            jsonBody.put("city", txtCity.getText());
            jsonBody.put("postalCode", txtPostal.getText());
            jsonBody.put("province", spProvince.getSelectedItem());
            jsonBody.put("country", spCountry.getSelectedItem());

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    mHandler.post((new Runnable() {
                        public void run() {
                            txtCompanyName.setText("");
                            txtPhone.setText("");
                            txtEmail.setText("");
                            txtStreetAddress.setText("");
                            txtUnit.setText("");
                            txtCity.setText("");
                            txtPostal.setText("");
                            processing =false;
                        }
                    }));
                    Toast.makeText(context, "Customer profile created successfully!", Toast.LENGTH_LONG).show();
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
           // JSONObject data = new JSONObject(responseBody);
           // JSONArray errors = data.getJSONArray("errors");
          //  JSONObject jsonMessage = errors.getJSONObject(0);
            String message = responseBody; //jsonMessage.getString("message");
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        //} catch (JSONException e) {
        }
        catch (UnsupportedEncodingException errorr) {
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
