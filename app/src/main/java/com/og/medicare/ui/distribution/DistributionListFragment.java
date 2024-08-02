package com.og.medicare.ui.distribution;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.og.medicare.R;
import com.og.medicare.adapter.CommonListAdapter;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentDistributionBinding;
import com.og.medicare.databinding.FragmentDistributionListBinding;
import com.og.medicare.model.CommonList;
import com.og.medicare.model.Distribution;
import com.og.medicare.model.Inventory;
import com.og.medicare.model.Order;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Response;


public class DistributionListFragment extends Fragment {

    private FragmentDistributionListBinding binding;

    ListView listView;
    private static CommonListAdapter adapter;

    private ArrayList<CommonList> dataModels;
    private ArrayList<CommonList> filteredDataModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDistributionListBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        listView = binding.itemList;
        EditText search = binding.search;

        dataModels = new ArrayList<>();
        filteredDataModels = new ArrayList<>();

        adapter = new CommonListAdapter(filteredDataModels,getContext());

        // getting inventory
        Response response = APIUtil.getDistribution();

        try {
            JSONArray jsonArray = new JSONArray(response.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {

                Date date_dispensed = getDate(jsonArray.getJSONObject(i).getString("date_dispensed"));
                Date expiration_date = getDate(jsonArray.getJSONObject(i).getString("expiration_date"));
                Date patient_birth_date = getDate(jsonArray.getJSONObject(i).getString("patient_birth_date"));
                Date created_at = getDate(jsonArray.getJSONObject(i).getString("created_at"));

                dataModels.add(CommonList.builder()
                        .id(jsonArray.getJSONObject(i).getInt("id"))
                        .title(jsonArray.getJSONObject(i).getString("generic_name"))
                        .subTitle(jsonArray.getJSONObject(i).getString("patient_name"))
                        .obj(Distribution.builder()
                                .id(jsonArray.getJSONObject(i).getInt("id"))
                                .quantity_dispensed(jsonArray.getJSONObject(i).getInt("quantity_dispensed"))
                                .generic_name(jsonArray.getJSONObject(i).getString("generic_name"))
                                .brand_name(jsonArray.getJSONObject(i).getString("brand_name"))
                                .unit_of_measure(jsonArray.getJSONObject(i).getString("unit_of_measure"))
                                .lot_number(jsonArray.getJSONObject(i).getString("lot_number"))
                                .patient_name(jsonArray.getJSONObject(i).getString("patient_name"))
                                .patient_address(jsonArray.getJSONObject(i).getString("patient_address"))
                                .patient_diagnosis(jsonArray.getJSONObject(i).getString("patient_diagnosis"))
                                .date_dispensed(date_dispensed)
                                .expiration_date(expiration_date)
                                .patient_birth_date(patient_birth_date)
                                .created_at(created_at).build()).build());
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

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create(); //Read Update
                alertDialog.setTitle("Distribution information");
                alertDialog.setMessage(
                        "Generic Name: " + ((Distribution) dataModel.getObj()).getGeneric_name() + "\n" +
                                "Brand Name: " + ((Distribution) dataModel.getObj()).getBrand_name() + "\n" +
                                "Unit of Measure: " + ((Distribution) dataModel.getObj()).getUnit_of_measure() + "\n" +
                                "Quantity Dispensed: " + ((Distribution) dataModel.getObj()).getQuantity_dispensed() + "\n" +
                                "Lot Number: " + ((Distribution) dataModel.getObj()).getLot_number() + "\n" +
                                "Expiration Date: " + ((Distribution) dataModel.getObj()).getExpiration_date() + "\n" +
                                "Patient Name: " + ((Distribution) dataModel.getObj()).getPatient_name() + "\n" +
                                "Patient Birth Date: " + ((Distribution) dataModel.getObj()).getPatient_birth_date() + "\n" +
                                "Patient Address: " + ((Distribution) dataModel.getObj()).getPatient_address() + "\n" +
                                "Patient Diagnosis: " + ((Distribution) dataModel.getObj()).getPatient_diagnosis() + "\n"
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
            if (date != null && !date.isEmpty() && !date.equalsIgnoreCase("null")) {
                zonedDateTime = ZonedDateTime.parse(date,
                        DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
                return Date.from(localDateTime.atZone(zonedDateTime.getZone()).toInstant());
            }
        }
        return null;
    }
}