package com.og.medicare.ui.home;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.og.medicare.R;
import com.og.medicare.adapter.ListAdapter;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentHomeBinding;
import com.og.medicare.model.Inventory;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Response;

public class HomeFragment extends Fragment {

    ListView listView;
    private static ListAdapter adapter;

    private FragmentHomeBinding binding;

    private ArrayList<Inventory> dataModels;
    private ArrayList<Inventory> filteredDataModels;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        listView = binding.main.findViewById(R.id.item_list);

        EditText search = binding.main.findViewById(R.id.search);

        dataModels = new ArrayList<>();
        filteredDataModels = new ArrayList<>();

        adapter = new ListAdapter(filteredDataModels,getContext());

        Response inventoryResponse = APIUtil.getInventory();
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

                dataModels.add(new Inventory(
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

//                DataModel dataModel= dataModels.get(position);
//
//                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
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
            for (Inventory item : dataModels) {
                if (item.getMedicineName().toLowerCase().contains(text) ||
                        item.getBrandName().toLowerCase().contains(text) ||
                        item.getLotNumber().toLowerCase().contains(text)) {
                    filteredDataModels.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}