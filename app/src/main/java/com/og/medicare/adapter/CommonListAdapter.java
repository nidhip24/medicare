package com.og.medicare.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.og.medicare.model.CommonList;
import com.og.medicare.model.Inventory;

import java.util.ArrayList;

public class CommonListAdapter extends ArrayAdapter<CommonList> implements View.OnClickListener{

    private int lastPosition = -1;

    private ArrayList<CommonList> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtSubTitle;
    }

    public CommonListAdapter(ArrayList<CommonList> data, Context context) {
        super(context, R.layout.common_row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        CommonList dataModel = getItem(position);

        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create(); //Read Update
        alertDialog.setTitle("hi");
        alertDialog.setMessage("this is my app");

        alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // here you can add functions
            }
        });

        alertDialog.show();

        Log.d("onClick", "onClick: ");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CommonList dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.common_row_item, parent, false);
            viewHolder.txtTitle = convertView.findViewById(R.id.title);
            viewHolder.txtSubTitle = convertView.findViewById(R.id.subtitle);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        lastPosition = position;

        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtSubTitle.setText(dataModel.getSubTitle());

        // Return the completed view to render on screen
        return convertView;
    }
}
