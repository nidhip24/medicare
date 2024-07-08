package com.og.medicare.ui.order;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.og.medicare.R;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentInventoryBinding;
import com.og.medicare.databinding.FragmentOrderBinding;
import com.og.medicare.model.Inventory;

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

import okhttp3.Response;

public class OrderFragment extends Fragment {

    private FragmentOrderBinding binding;
    ArrayList<Inventory> dataModels;

    private EditText etTextOrderDate, editTextQuantity, editTextRequestedBy,
            editTextHealthStation;

    private MaterialAutoCompleteTextView editTextMedicineName;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        etTextOrderDate = binding.editTextOrderDate;
        editTextMedicineName = binding.dpMedicineName;
        editTextQuantity = binding.editTextQuantity;
        editTextRequestedBy = binding.editTextRequestedBy;
        editTextHealthStation = binding.editTextHealthStation;

        etTextOrderDate.setOnClickListener(v -> showDatePickerDialog(etTextOrderDate));

        Response inventoryResponse = APIUtil.getInventory();
        dataModels = new ArrayList<>();
        parseResponse(inventoryResponse);

        String[] medicineNames = new String[dataModels.size()];
        for (int i = 0; i < dataModels.size(); i++) {
            medicineNames[i] = dataModels.get(i).getMedicineName() + " - " + dataModels.get(i).getBrandName();
        }
        editTextMedicineName.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, medicineNames));

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orderDate = etTextOrderDate.getText().toString().trim();
                String quantity = editTextQuantity.getText().toString().trim();
                String requestedBy = editTextRequestedBy.getText().toString().trim();
                String healthStation = editTextHealthStation.getText().toString().trim();

                String medicineName = editTextMedicineName.getText().toString().trim();

                // Validate the input
                if (orderDate.isEmpty()) {
                    etTextOrderDate.setError("Order date is required");
                    etTextOrderDate.requestFocus();
                    return;
                }
                if (quantity.isEmpty()) {
                    editTextQuantity.setError("Quantity is required");
                    editTextQuantity.requestFocus();
                    return;
                }
                if (requestedBy.isEmpty()) {
                    editTextRequestedBy.setError("Requested by is required");
                    editTextRequestedBy.requestFocus();
                    return;
                }
                if (healthStation.isEmpty()) {
                    editTextHealthStation.setError("Health station is required");
                    editTextHealthStation.requestFocus();
                    return;
                }
                if (medicineName.isEmpty()) {
                    editTextMedicineName.setError("Medicine name is required");
                    editTextMedicineName.requestFocus();
                    return;
                }


                // Create json object and send to the server
                JSONObject order = new JSONObject();
                try {
                    order.put("date_requested", orderDate);
                    order.put("mtid", findItem(medicineName));
                    order.put("quantity_requested", quantity);
                    order.put("requested_by", requestedBy);
                    order.put("health_station_name", healthStation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Response response = APIUtil.addOrder(order);
                if (response != null) {
                    if (response.isSuccessful() && response.code() == 200) {
                        Toast.makeText(getContext(), "Order added successfully", Toast.LENGTH_SHORT).show();

                        // reset the form
                        etTextOrderDate.setText("");
                        editTextQuantity.setText("");
                        editTextRequestedBy.setText("");
                        editTextHealthStation.setText("");
                        editTextMedicineName.setText("");
                    } else {
                        Toast.makeText(getContext(), "Failed to add order", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to add order", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    private int findItem(String medicineName) {
        for (int i = 0; i < dataModels.size(); i++) {
            String[] mName = medicineName.split(" - ");
            if (dataModels.get(i).getMedicineName().equalsIgnoreCase(mName[0].trim())
                    && dataModels.get(i).getBrandName().equalsIgnoreCase(mName[1].trim())) {
                return dataModels.get(i).getId();
            }
        }
        return -1;
    }

    private void parseResponse(Response inventoryResponse) {
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                editText.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}