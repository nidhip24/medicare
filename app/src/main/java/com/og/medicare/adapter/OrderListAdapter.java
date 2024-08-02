package com.og.medicare.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.og.medicare.R;
import com.og.medicare.api.APIUtil;
import com.og.medicare.model.CommonList;
import com.og.medicare.model.Order;
import com.og.medicare.model.OrderList;
import com.og.medicare.util.DataStorage;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Response;

public class OrderListAdapter extends ArrayAdapter<OrderList> implements View.OnClickListener{

    private int lastPosition = -1;

    private ArrayList<OrderList> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtTitle;
        TextView txtSubTitle;
        TextView txtAddedBy;
        TextView txtStatus;
        ImageView accept, reject;
    }

    public OrderListAdapter(ArrayList<OrderList> data, Context context) {
        super(context, R.layout.order_row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        OrderList dataModel = getItem(position);

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
        OrderList dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_row_item, parent, false);
            viewHolder.txtTitle = convertView.findViewById(R.id.title);
            viewHolder.txtSubTitle = convertView.findViewById(R.id.subtitle);
            viewHolder.txtAddedBy = convertView.findViewById(R.id.added_by);
            viewHolder.txtStatus = convertView.findViewById(R.id.status);

            viewHolder.accept = convertView.findViewById(R.id.accept);
            viewHolder.reject = convertView.findViewById(R.id.reject);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = (Order) dataModel.getObj();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", order.getId());
                    jsonObject.put("uid", Integer.parseInt(DataStorage.getInstance(getContext()).getS("id")));
                    jsonObject.put("status", "accepted");

                    try (Response response = APIUtil.updateOrder(order.getId(), jsonObject)) {
                        if (response.code() == 200) {
                            Toast.makeText(mContext, "Order accepted successfully", Toast.LENGTH_SHORT).show();
                            viewHolder.txtStatus.setText("Status: accepted");
                            dataModel.setStatus("accepted");
                        } else {
                            Toast.makeText(mContext, "Failed to accept order", Toast.LENGTH_SHORT).show();
                        }
                    }
                    handleIcons(viewHolder, dataModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        viewHolder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = (Order) dataModel.getObj();

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id", order.getId());
                    jsonObject.put("uid", Integer.parseInt(DataStorage.getInstance(getContext()).getS("id")));
                    jsonObject.put("status", "rejected");

                    try (Response response = APIUtil.updateOrder(order.getId(), jsonObject)) {
                        if (response.code() == 200) {
                            Toast.makeText(mContext, "Order rejected successfully", Toast.LENGTH_SHORT).show();
                            viewHolder.txtStatus.setText("Status: rejected");
                            dataModel.setStatus("rejected");
                        } else {
                            Toast.makeText(mContext, "Failed to reject order", Toast.LENGTH_SHORT).show();
                        }
                    }
                    handleIcons(viewHolder, dataModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        lastPosition = position;

        viewHolder.txtTitle.setText(dataModel.getTitle());
        viewHolder.txtSubTitle.setText(dataModel.getSubTitle());
        viewHolder.txtAddedBy.setText("Added by: " + dataModel.getAddedBy());
        viewHolder.txtStatus.setText("Status: " + dataModel.getStatus());

        handleIcons(viewHolder, dataModel);
        // Return the completed view to render on screen
        return convertView;
    }

    private void handleIcons(ViewHolder viewHolder, OrderList dataModel) {
        if (dataModel.getStatus().equalsIgnoreCase("pending") && dataModel.isAdmin()) {
            viewHolder.accept.setVisibility(View.VISIBLE);
            viewHolder.reject.setVisibility(View.VISIBLE);
        } else {
            viewHolder.accept.setVisibility(View.INVISIBLE);
            viewHolder.reject.setVisibility(View.INVISIBLE);
        }
    }
}
