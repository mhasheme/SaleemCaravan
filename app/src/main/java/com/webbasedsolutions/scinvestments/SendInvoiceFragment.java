package com.webbasedsolutions.scinvestments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SendInvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SendInvoiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "item_id";
    private static final String ARG_PARAM2 = "param2";
    private EditText txtTo;
    private EditText txtComments;
    private Button btnSend;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private SharedPreferences globalSetting;

    //private OnFragmentInteractionListener mListener;

    public SendInvoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendInvoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SendInvoiceFragment newInstance(String param1, String param2) {
        SendInvoiceFragment fragment = new SendInvoiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        globalSetting = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_invoice, container, false);
        txtTo = view.findViewById(R.id.txtTo);
        txtComments = view.findViewById(R.id.txtComments);
        btnSend = view.findViewById(R.id.btnSend);
        Context context = view.getContext();
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(txtTo.getText().toString().length() > 0 &&  !Helpers.isValidEmail(txtTo.getText().toString())) {
                    txtTo.setError("Invalid Email address.");
                    return;
                }
                else{
                    txtTo.setError(null);
                }

                sendInvoice(context);
            }
        });
        return view;
    }

    private void sendInvoice(Context context){
        try {
            ProgressDialog dialog = new ProgressDialog(context);
            Window window = getActivity().getWindow();
            dialog.setMessage("Loading data....");
            dialog.show();
            Settings settings = ((Settings) context.getApplicationContext());
            String baseURL = settings.getBaseURL();
            JSONArray array = new JSONArray();
            array.put(mParam1);

            String URL = baseURL + "/Invoice/sendinvoicenow";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("Customers",array);
            jsonBody.put("Comments",txtComments.getText());
            jsonBody.put("SendTo",txtTo.getText());


            SharedPreferences.Editor editor = globalSetting.edit();
            String username = globalSetting.getString("username","administrator");
            jsonBody.put("userid", username);


            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    dialog.dismiss();
                    mHandler.post((new Runnable() {
                        public void run() {
                            txtTo.setText("");
                            txtComments.setText("");
                            Toast.makeText(context, "Invoice sent successfully!", Toast.LENGTH_LONG).show();
                        }
                    }));

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
        String message = "Failed to send invoice";
        try {

            if(error.networkResponse == null || error.networkResponse.data == null)
                message = "Failed to send Invoice";
            else{
                String responseBody = new String(error.networkResponse.data, "utf-8");
                message = responseBody;
                if (Helpers.isJSONValid(responseBody)) {
                    try {
                        message = new JSONObject(responseBody).getString("Message");
                    } catch (JSONException ex) {

                    }
                }
            }


        }
        catch (UnsupportedEncodingException errorr) {
        }
        catch (Exception ex){

        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
   // public void onButtonPressed(Uri uri) {
     //   if (mListener != null) {
      //      mListener.onFragmentInteraction(uri);
     //   }
   // }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       // if (context instanceof OnFragmentInteractionListener) {
          //  mListener = (OnFragmentInteractionListener) context;
       // } else {
         //   throw new RuntimeException(context.toString()
            //        + " must implement OnFragmentInteractionListener");
        //}
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
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
    //public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
     //   void onFragmentInteraction(Uri uri);
    //}
}
