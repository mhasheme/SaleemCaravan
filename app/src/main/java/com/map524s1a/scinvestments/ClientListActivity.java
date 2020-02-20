package com.map524s1a.scinvestments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.map524s1a.scinvestments.model.Client;
import com.map524s1a.scinvestments.model.ClientContent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An activity representing a list of Clients. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ClientDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ClientListActivity extends AppCompatActivity implements CreateCustomer.OnFragmentInteractionListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private EditText editText;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean first = false;

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    private void dissMissKeyboard(){
        try {
            Window window = getWindow();
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(window.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception ex){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);
        Window window = this.getWindow();
        dissMissKeyboard();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        editText = (EditText) findViewById(R.id.company);
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        editText.setOnFocusChangeListener(ofcListener);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_salc3);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        View recyclerView = findViewById(R.id.client_list);
        assert recyclerView != null;

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() > 2) {
                    setupRecyclerView((RecyclerView) recyclerView,editText.getText().toString());
                    dissMissKeyboard();
                    //editText.setText("");
                }
            }
        });


        if (findViewById(R.id.client_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setupRecyclerView((RecyclerView) recyclerView,"a");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sc_menu, menu);
        return true;
    }

    private  void showCreateClient(){

        // Create new fragment and transaction
        Fragment newFragment = new CreateCustomer();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.client_detail_container,newFragment );
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.addCustomer:
               // showHelp();
                showCreateClient();
                return true;

            case R.id.logout:
              // Settings settings = ((Settings) this.getApplication());
              // settings.wipeAll();
               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class MyFocusChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus){

            if(v.getId() == R.id.company && !hasFocus) {

                InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, String company) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading data....");
        dialog.show();
        Settings settings = ((Settings) this.getApplication());
        String baseURL = settings.getBaseURL();
        Object ctx = this;
        Context context = getBaseContext();
        StringRequest request = new StringRequest(
                Request.Method.GET,
                baseURL + "/Customers/Customers/" + company,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            com.map524s1a.scinvestments.model.ClientContent.ITEMS.clear();
                            //ClientContent clientContent = new ClientContent();
                            //JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray =new JSONArray(response);

                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject item = jsonArray.getJSONObject(i);
                                ClientContent.addItem( new Client(item.getInt("id"),item.getString("name"),
                                        item.getString("phone"),item.getString("email"),"",0,0));

                            }
                            Handler h = new Handler(getApplicationContext().getMainLooper());
                            // Although you need to pass an appropriate context
                            h.post(new Runnable() {
                                @Override
                                public void run() {

                                    recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter((ClientListActivity) ctx, com.map524s1a.scinvestments.model.ClientContent.ITEMS, mTwoPane));
                                    //recyclerView.addItemDecoration(new SimpleBlueDivider(context));
                                    dialog.dismiss();

                                }

                                // Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                            });
                        }
                        catch (JSONException ex){
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
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


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ClientListActivity mParentActivity;
        private final List<Client> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client item = (Client) view.getTag();
                Context context = view.getContext();
                getAdditionalInfo(item, context);

            }
        };

        SimpleItemRecyclerViewAdapter(ClientListActivity parent,
                                      List<Client> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        private  void getAdditionalInfo(Client client, Context context){
            ProgressDialog dialog = new ProgressDialog(context);
            dialog.setMessage("Loading data....");
            dialog.show();
            Settings settings = ((Settings) context.getApplicationContext());
            String baseURL = settings.getBaseURL();

            StringRequest request = new StringRequest(
                    Request.Method.GET,
                    baseURL + "/Customers/unpaid/" + String.valueOf(client.getId()),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //ClientContent clientContent = new ClientContent();
                                //JSONObject jsonObject = new JSONObject();
                                JSONArray jsonArray =new JSONArray(response);
                                if(jsonArray.length() > 0){
                                    JSONObject item = jsonArray.getJSONObject(0);
                                    client.setInvoices(item.getString("invoices"));
                                    client.setNumberOfInvoices(item.getInt("numberOfUnpaidInvoices"));
                                    client.setTotalUnpaid(item.getDouble("totalUnpaid"));
                                    client.setBalance(item.getDouble("balance"));
                                    ClientContent.updateItem(client);
                                }
                                dialog.dismiss();
                            }
                            catch (JSONException ex){
                                dialog.dismiss();
                                Toast.makeText(context.getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            mHandler.post((new Runnable() {
                                public void run() {
                                    if (mTwoPane) {
                                        Bundle arguments = new Bundle();
                                        arguments.putString(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(client.getId()));

                                        Fragment newFragment = new ClientDetailFragment();
                                        FragmentTransaction transaction = mParentActivity.getFragmentManager().beginTransaction();
                                        newFragment.setArguments(arguments);
                                        // Replace whatever is in the fragment_container view with this fragment,
                                        // and add the transaction to the back stack
                                        transaction.replace(R.id.client_detail_container,newFragment );
                                        transaction.addToBackStack(null);

                                        // Commit the transaction
                                        transaction.commit();

                                        //Fragment fragment = new ClientDetailFragment();
                                        // fragment.setArguments(arguments);
                                        // mParentActivity.getSupportFragmentManager().beginTransaction()
                                        //         .replace(R.id.client_detail_container, fragment)
                                        //         .commit();
                                    } else {


                                        Intent intent = new Intent(context, ClientDetailActivity.class);
                                        intent.putExtra(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(client.getId()));

                                        context.startActivity(intent);
                                    }

                                }
                            }));

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            dialog.dismiss();
                            Toast.makeText(context.getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
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




        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.client_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
            if(mValues.get(position).getPhone() != "null")
                holder.mContentView.setText(mValues.get(position).getPhone());
            else
                holder.mContentView.setVisibility(View.GONE);

            holder.mName.setText(mValues.get(position).getName());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mName;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
                mName = (TextView) view.findViewById(R.id.Name);
            }
        }
    }

    public class SimpleBlueDivider extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleBlueDivider(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.divider_blue);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            //divider padding give some padding whatever u want or disable
            int left =parent.getPaddingLeft()+80;
            int right = parent.getWidth() - parent.getPaddingRight()-30;

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

    }
}
