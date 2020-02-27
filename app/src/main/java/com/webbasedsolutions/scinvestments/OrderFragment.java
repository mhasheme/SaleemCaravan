package com.webbasedsolutions.scinvestments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.map524s1a.scinvestments.R;
import com.webbasedsolutions.scinvestments.collections.GlobalCollectionsSinglton;
import com.webbasedsolutions.scinvestments.model.Cart;
import com.webbasedsolutions.scinvestments.model.LiteCustomer;
import com.webbasedsolutions.scinvestments.model.Product;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "item_id";

    private List<Cart> cart;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Spinner spinner;
    private Button btnAdd;
    private Button btnNext;
    private EditText quantity;
    private Button btnRemove;
    private GlobalCollectionsSinglton singlton = GlobalCollectionsSinglton.getInstance();
    private LiteCustomer mItem;
    private TextView txtCompany;
    private TextView txtProducts;
    private TextView txtQuanitities;

    public OrderFragment() {
        // Required empty public constructor
        cart = new ArrayList<Cart>();
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
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderFragment newInstance(String param1, String param2) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mItem = singlton.getCustomer(Integer.parseInt(mParam1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        spinner = view.findViewById(R.id.spProducts);
        dismissKeyboard();
        Settings settings = ((Settings) getContext().getApplicationContext());
        try {
           List<String> data = settings.getGlobalCollectionsSinglton().getProductListName();
           data.add(0,"Select a Product");
           if(data != null) {
               ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, data);

               dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

               spinner.setAdapter(dataAdapter);
           }
        }
        catch (Exception ex){

        }
        quantity = view.findViewById(R.id.txtQuantity);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnNext = view.findViewById(R.id.btNext);
        txtCompany = view.findViewById(R.id.txtCompany);
        txtQuanitities = view.findViewById(R.id.lblQuantities);
        txtProducts = view.findViewById(R.id.lblProducts);
        txtCompany.setText(mItem.getName());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(spinner.getSelectedItemPosition() == 0){
                    Toast.makeText(getContext(), "Please select a product.", Toast.LENGTH_LONG).show();

                    return;
                }

                if(quantity.getText().length() < 1 ){
                    quantity.setError("Quanity is required!");
                    return;
                }


                String pname =  spinner.getSelectedItem().toString();
                Product product = singlton.getProductByName(pname);
                product.setQuantity(Integer.parseInt(quantity.getText().toString()));
                List<Product> products = new ArrayList<Product>();
                products.add(product);

                List<Cart> cartList = new ArrayList<Cart>();
                synchronized(this) {
                    int i = 0;
                    boolean found = false;
                    for (Cart c : cart
                    ) {
                        //if(c.getProducts() != null)
                            for (Product p : c.getProducts()
                            ) {
                                if (p.getName().equals(product.getName())) {
                                   found = true;
                                   // c.setProducts(null);
                                    break;
                                }
                                else {
                                    found = false;

                                }
                            }
                            if(!found){
                                cartList.add(c);
                            }

                    }
                }

                cartList.add(new Cart(mItem.getId(),products));

                // Showing selected spinner item
                spinner.setSelection(0);
                quantity.setText("");
                btnNext.setEnabled(true);
                txtProducts.setText("");
                txtQuanitities.setText("");
                cart.clear();
                for (Cart c: cartList
                ) {
                    //if(c.getProducts() != null)
                        for (Product p: c.getProducts()
                        ) {
                                txtProducts.setText(txtProducts.getText() + "\n\n" + p.getName());
                                txtQuanitities.setText(txtQuanitities.getText() + "\n\n" + p.getQuantity());
                        }
                        cart.add(c);
                }
                settings.setTransferObject(cartList);
                Toast.makeText(getContext(), "Added " + product.getName() + " to cart.", Toast.LENGTH_LONG).show();
                btnRemove.setEnabled(true);
                dismissKeyboard();
            }
        });
        btnRemove = view.findViewById(R.id.btnRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtProducts.setText("");
                txtQuanitities.setText("");
                cart.clear();
                settings.setTransferObject(cart);
                Toast.makeText(getContext(), "Cart Cleared", Toast.LENGTH_LONG).show();
                btnRemove.setEnabled(false);
                dismissKeyboard();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(cart.size() == 0){
                    Toast.makeText(getContext(), "You must add a few product to cart.", Toast.LENGTH_LONG).show();
                    btnNext.setEnabled(false);
                    return;
                }

                Bundle bundle=new Bundle();
                bundle.putString(ClientDetailFragment.ARG_ITEM_ID, String.valueOf(mItem.getId()));

                // your handler code here
                // Create new fragment and transaction
                Fragment newFragment = new PurchaseOrderFragment();
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
        spinner.setOnItemSelectedListener(this);
        return  view;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        quantity.setText("");
        dismissKeyboard();
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

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
