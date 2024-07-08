package com.og.medicare.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.og.medicare.R;
import com.og.medicare.model.Inventory;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<Inventory> implements View.OnClickListener{

    private int lastPosition = -1;

    private ArrayList<Inventory> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtBrand;
        TextView qty;
        ImageView info;
    }

    public ListAdapter(ArrayList<Inventory> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Inventory dataModel=(Inventory) object;

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Inventory dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtBrand = (TextView) convertView.findViewById(R.id.brand);
            viewHolder.qty = (TextView) convertView.findViewById(R.id.qty);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_info);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getMedicineName());
        viewHolder.txtBrand.setText(dataModel.getBrandName());
        viewHolder.qty.setText(dataModel.getQuantity() + "");
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        // check if dataModel.getExpiryDate is less than 3 days
        Log.d("ListAdapter", dataModel.getMedicineName() + " Expiry Date: " + dataModel.getExpiryDate().getTime() + " Current Time: " + System.currentTimeMillis() + " Diff: " + (dataModel.getExpiryDate().getTime() - System.currentTimeMillis()));
        if (dataModel.getExpiryDate().getTime() < System.currentTimeMillis())
            viewHolder.info.setColorFilter(ContextCompat.getColor(mContext, R.color.light_red), PorterDuff.Mode.SRC_IN);
        else if (dataModel.getExpiryDate().getTime() < (System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3))
            viewHolder.info.setColorFilter(ContextCompat.getColor(mContext, R.color.light_orange), PorterDuff.Mode.SRC_IN);
        else
            viewHolder.info.setColorFilter(ContextCompat.getColor(mContext, R.color.light_blue), PorterDuff.Mode.SRC_IN);

        // Return the completed view to render on screen
        return convertView;
    }
}
