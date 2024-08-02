package com.og.medicare.ui.inventory;

import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.og.medicare.R;
import com.og.medicare.api.APIUtil;
import com.og.medicare.cache.AppCache;
import com.og.medicare.databinding.FragmentHomeBinding;
import com.og.medicare.databinding.FragmentInventoryBinding;

import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.Response;

public class Inventory extends Fragment {

    private FragmentInventoryBinding binding;
    private MaterialAutoCompleteTextView etUnitMeasure, etClassificationOfMedicine;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        // Set click listener for the expiry date field
        binding.etExpiryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        etUnitMeasure = binding.etUnitMeasure;
        etClassificationOfMedicine = binding.etClassificationOfMedicine;

        // Set the dropdown values for the unit of measure
        etUnitMeasure.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, AppCache.getInstance().getCache("unit_of_measure")));

        etClassificationOfMedicine.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, AppCache.getInstance().getCache("classification_of_medicine")));

        // Set click listener for the submit button
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get values from EditTexts
                String genericName = binding.etGenericName.getText().toString().trim();
                String brandName = binding.etBrandName.getText().toString().trim();
                String expiryDate = binding.etExpiryDate.getText().toString().trim();
                String quantity = binding.etQuantity.getText().toString().trim();
                String lotNumber = binding.etLotNumber.getText().toString().trim();
                String unitMeasure = etUnitMeasure.getText().toString().trim();
                String classificationOfMedicine = etClassificationOfMedicine.getText().toString().trim();

                // Validate inputs
                if (genericName.isEmpty()) {
                    binding.etGenericName.setError("Generic Name is required");
                    binding.etGenericName.requestFocus();
                    return;
                }

                if (brandName.isEmpty()) {
                    binding.etBrandName.setError("Brand Name is required");
                    binding.etBrandName.requestFocus();
                    return;
                }

                if (expiryDate.isEmpty()) {
                    binding.etExpiryDate.setError("Expiry Date is required");
                    binding.etExpiryDate.requestFocus();
                    return;
                }

                if (quantity.isEmpty()) {
                    binding.etQuantity.setError("Quantity is required");
                    binding.etQuantity.requestFocus();
                    return;
                }

                if (lotNumber.isEmpty()) {
                    binding.etLotNumber.setError("Lot Number is required");
                    binding.etLotNumber.requestFocus();
                    return;
                }

                if (unitMeasure.isEmpty()) {
                    binding.etUnitMeasure.setError("Unit of Measure is required");
                    binding.etUnitMeasure.requestFocus();
                    return;
                }

                if (classificationOfMedicine.isEmpty()) {
                    binding.etClassificationOfMedicine.setError("Classification of Medicine is required");
                    binding.etClassificationOfMedicine.requestFocus();
                    return;
                }

                JSONObject inventoryJson = new JSONObject();
                try {
                    inventoryJson.put("generic_name", genericName);
                    inventoryJson.put("brand_name", brandName);
                    inventoryJson.put("expiry_date", expiryDate);
                    inventoryJson.put("quantity", quantity);
                    inventoryJson.put("lot_number", lotNumber);
                    inventoryJson.put("unit_of_measure", unitMeasure);
                    inventoryJson.put("classification_of_medicine", classificationOfMedicine);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // call an api to save the data
                try (Response response = APIUtil.addInventory(inventoryJson)) {
                    if (response.isSuccessful() && response.code() == 200) {
                        Toast.makeText(getContext(), "Record added successfully", Toast.LENGTH_SHORT).show();

                        // reset the form
                        binding.etGenericName.setText("");
                        binding.etBrandName.setText("");
                        binding.etExpiryDate.setText("");
                        binding.etQuantity.setText("");
                        binding.etLotNumber.setText("");
                        etUnitMeasure.setText("");
                        etClassificationOfMedicine.setText("");
                    } else {
                        Toast.makeText(getContext(), "Failed to add record", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Failed to add record", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                String date = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                binding.etExpiryDate.setText(date);
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