package com.og.medicare.ui.order;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.og.medicare.R;
import com.og.medicare.adapter.CommonListAdapter;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentInventoryBinding;
import com.og.medicare.databinding.FragmentOrderBinding;
import com.og.medicare.model.CommonList;
import com.og.medicare.model.Inventory;
import com.og.medicare.model.Order;
import com.og.medicare.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Response;

public class OrderFragment extends Fragment {

    private FragmentOrderBinding binding;

    ListView listView;
    private static CommonListAdapter adapter;

    private ArrayList<CommonList> dataModels;
    private ArrayList<CommonList> filteredDataModels;

    private HashMap<Integer, Inventory> inventoryData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.itemList;
        EditText search = binding.search;

        dataModels = new ArrayList<>();
        filteredDataModels = new ArrayList<>();

        // getting inventory
        Response inventoryResponse = APIUtil.getInventory();
        inventoryData = new HashMap<>();
        try {
            JSONArray inventory = new JSONArray(inventoryResponse.body().string());
            for (int i = 0; i < inventory.length(); i++) {
                ZonedDateTime zonedDateTime = null;
                Date epxiryDate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    zonedDateTime = ZonedDateTime.parse(inventory.getJSONObject(i).getString("expiry_date"),
                            DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
                    epxiryDate = Date.from(localDateTime.atZone(zonedDateTime.getZone()).toInstant());
                }

                inventoryData.put(inventory.getJSONObject(i).getInt("id"), new Inventory(
                        inventory.getJSONObject(i).getInt("id"),
                        1,
                        inventory.getJSONObject(i).getInt("quantity"),
                        inventory.getJSONObject(i).getString("generic_name"),
                        inventory.getJSONObject(i).getString("brand_name"),
                        inventory.getJSONObject(i).getString("lot_number"),
                        inventory.getJSONObject(i).getString("unit_of_measure"),
                        epxiryDate
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new CommonListAdapter(filteredDataModels,getContext());

        Response response = APIUtil.getOrders();

        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                ZonedDateTime zonedDateTime = null;
                Date created_at = getDate(jsonArray.getJSONObject(i).getString("created_at"));
                Date date_requested = getDate(jsonArray.getJSONObject(i).getString("date_requested"));

                Order order = Order.builder()
                        .id(jsonArray.getJSONObject(i).getInt("id"))
                        .mtid(jsonArray.getJSONObject(i).getInt("mtid"))
                        .quantity_requested(jsonArray.getJSONObject(i).getInt("quantity_requested"))
                        .requested_by(jsonArray.getJSONObject(i).getString("requested_by"))
                        .health_station_name(jsonArray.getJSONObject(i).getString("health_station_name"))
                        .date_requested(date_requested)
                        .created_at(created_at).build();

                Inventory inventory = inventoryData.get(order.getMtid());
                dataModels.add(CommonList.builder()
                        .id(jsonArray.getJSONObject(i).getInt("id"))
                        .title(inventory != null ? inventory.getMedicineName() : "Unknown")
                        .subTitle(jsonArray.getJSONObject(i).getString("requested_by"))
                        .obj(order).build());
            }
            filteredDataModels.addAll(dataModels); // Initially, filtered list contains all items
            adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CommonList dataModel= dataModels.get(position);
//
                Inventory inventory = inventoryData.get(((Order) dataModel.getObj()).getMtid());

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create(); //Read Update
                alertDialog.setTitle("Order information");
                alertDialog.setMessage(
                        "ID: " + ((Order) dataModel.getObj()).getId() + "\n" +
                        "Medicine: " + inventory.getMedicineName() + "\n" +
                        "Quantity Requested: " + ((Order) dataModel.getObj()).getQuantity_requested() + "\n" +
                        "Requested By: " + ((Order) dataModel.getObj()).getRequested_by() + "\n" +
                        "Health Station Name: " + ((Order) dataModel.getObj()).getHealth_station_name() + "\n" +
                        "Date Requested: " + ((Order) dataModel.getObj()).getDate_requested() + "\n" +
                        "Created At: " + ((Order) dataModel.getObj()).getCreated_at()
                );

                alertDialog.setButton("Continue..", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });

                alertDialog.show();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        return root;
    }

    private void filter(String text) {
        filteredDataModels.clear();
        if (text.isEmpty()) {
            filteredDataModels.addAll(dataModels);
        } else {
            text = text.toLowerCase();
            for (CommonList item : dataModels) {
                if (item.getTitle().toLowerCase().contains(text) ||
                        item.getSubTitle().toLowerCase().contains(text)) {
                    filteredDataModels.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private Date getDate(String date) {
        ZonedDateTime zonedDateTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            zonedDateTime = ZonedDateTime.parse(date,
                    DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
            return Date.from(localDateTime.atZone(zonedDateTime.getZone()).toInstant());
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}