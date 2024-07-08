package com.og.medicare.ui.distribution;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.og.medicare.R;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentDistributionBinding;
import com.og.medicare.databinding.FragmentHomeBinding;
import com.og.medicare.databinding.FragmentSlideshowBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import okhttp3.Response;

public class DistributionFragment extends Fragment {

    private FragmentDistributionBinding binding;

    private EditText etDateDispensed, etGenericName, etBrandName, etUnitMeasure, etQuantityDispensed, etLotNumber, etExpirationDate, etPatientName, etPatientBirthDate, etPatientAddress, etPatientDiagnosis;
    private Button btnSubmit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDistributionBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);


        etDateDispensed = root.findViewById(R.id.et_date_dispensed);
        etGenericName = root.findViewById(R.id.et_generic_name);
        etBrandName = root.findViewById(R.id.et_brand_name);
        etUnitMeasure = root.findViewById(R.id.et_unit_measure);
        etQuantityDispensed = root.findViewById(R.id.et_quantity_dispensed);
        etLotNumber = root.findViewById(R.id.et_lot_number);
        etExpirationDate = root.findViewById(R.id.et_expiration_date);
        etPatientName = root.findViewById(R.id.et_patient_name);
        etPatientBirthDate = root.findViewById(R.id.et_patient_birth_date);
        etPatientAddress = root.findViewById(R.id.et_patient_address);
        etPatientDiagnosis = root.findViewById(R.id.et_patient_diagnosis);
        btnSubmit = root.findViewById(R.id.btn_submit);

        etDateDispensed.setOnClickListener(v -> showDatePickerDialog(etDateDispensed));
        etExpirationDate.setOnClickListener(v -> showDatePickerDialog(etExpirationDate));
        etPatientBirthDate.setOnClickListener(v -> showDatePickerDialog(etPatientBirthDate));

        btnSubmit.setOnClickListener(v -> {
            // Get values from EditTexts
            String dateDispensed = etDateDispensed.getText().toString().trim();
            String genericName = etGenericName.getText().toString().trim();
            String brandName = etBrandName.getText().toString().trim();
            String unitMeasure = etUnitMeasure.getText().toString().trim();
            String quantityDispensed = etQuantityDispensed.getText().toString().trim();
            String lotNumber = etLotNumber.getText().toString().trim();
            String expirationDate = etExpirationDate.getText().toString().trim();
            String patientName = etPatientName.getText().toString().trim();
            String patientBirthDate = etPatientBirthDate.getText().toString().trim();
            String patientAddress = etPatientAddress.getText().toString().trim();
            String patientDiagnosis = etPatientDiagnosis.getText().toString().trim();

            // Validate inputs
            if (dateDispensed.isEmpty()) {
                etDateDispensed.setError("Date Dispensed is required");
                etDateDispensed.requestFocus();
                return;
            }
            if (genericName.isEmpty()) {
                etGenericName.setError("Generic Name is required");
                etGenericName.requestFocus();
                return;
            }
            if (brandName.isEmpty()) {
                etBrandName.setError("Brand Name is required");
                etBrandName.requestFocus();
                return;
            }
            if (unitMeasure.isEmpty()) {
                etUnitMeasure.setError("Unit of Measure is required");
                etUnitMeasure.requestFocus();
                return;
            }
            if (quantityDispensed.isEmpty()) {
                etQuantityDispensed.setError("Quantity Dispensed is required");
                etQuantityDispensed.requestFocus();
                return;
            }
            if (lotNumber.isEmpty()) {
                etLotNumber.setError("Lot Number is required");
                etLotNumber.requestFocus();
                return;
            }
            if (expirationDate.isEmpty()) {
                etExpirationDate.setError("Expiration Date is required");
                etExpirationDate.requestFocus();
                return;
            }
            if (patientName.isEmpty()) {
                etPatientName.setError("Patient Name is required");
                etPatientName.requestFocus();
                return;
            }
            if (patientBirthDate.isEmpty()) {
                etPatientBirthDate.setError("Patient Birth Date is required");
                etPatientBirthDate.requestFocus();
                return;
            }
            if (patientAddress.isEmpty()) {
                etPatientAddress.setError("Patient Address is required");
                etPatientAddress.requestFocus();
                return;
            }

            // Create a JSON object
            JSONObject distributionJson = new JSONObject();
            try {
                distributionJson.put("date_dispensed", dateDispensed);
                distributionJson.put("generic_name", genericName);
                distributionJson.put("brand_name", brandName);
                distributionJson.put("unit_of_measure", unitMeasure);
                distributionJson.put("quantity_dispensed", quantityDispensed);
                distributionJson.put("lot_number", lotNumber);
                distributionJson.put("expiration_date", expirationDate);
                distributionJson.put("patient_name", patientName);
                distributionJson.put("patient_birth_date", patientBirthDate);
                distributionJson.put("patient_address", patientAddress);
                distributionJson.put("patient_diagnosis", patientDiagnosis);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // call an api to save the data
            try (Response response = APIUtil.addDistribution(distributionJson)) {
                if (response.isSuccessful() && response.code() == 200) {
                    Toast.makeText(getContext(), "Record added successfully", Toast.LENGTH_SHORT).show();

                    // reset the form
                    etDateDispensed.setText("");
                    etGenericName.setText("");
                    etBrandName.setText("");
                    etUnitMeasure.setText("");
                    etQuantityDispensed.setText("");
                    etLotNumber.setText("");
                    etExpirationDate.setText("");
                    etPatientName.setText("");
                    etPatientBirthDate.setText("");
                    etPatientAddress.setText("");
                    etPatientDiagnosis.setText("");
                } else {
                    Toast.makeText(getContext(), "Failed to add record", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to add record", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
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