package com.webbasedsolutions.scinvestments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.map524s1a.scinvestments.R;
import com.webbasedsolutions.scinvestments.model.Invoice;

import java.util.ArrayList;
import java.util.List;

interface ItemSelected {
    void OnItemSelected();
}

public class InvoiceAdapter extends ArrayAdapter<Invoice> {

    private Context mContext;
    private ArrayList<Invoice> listState;
    private InvoiceAdapter myAdapter;
    private boolean isFromView = false;

    public InvoiceAdapter(Context context, int resource, List<Invoice> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<Invoice>) objects;
        this.myAdapter = this;
    }

    private List<ItemSelected> listeners = new ArrayList<ItemSelected>();

    public void addListener(ItemSelected toAdd) {
        listeners.add(toAdd);
    }

    public void OnItemSelected() {
        System.out.println("Hello!");

        // Notify everybody that may be interested.
        for (ItemSelected hl : listeners)
            hl.OnItemSelected();
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_invoice_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getInvoiceNumber());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if ((position == 0)) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                    OnItemSelected();
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}
