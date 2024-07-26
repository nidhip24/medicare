package com.og.medicare.ui.bsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.og.medicare.R;
import com.og.medicare.api.APIUtil;
import com.og.medicare.model.Role;
import com.og.medicare.util.DataStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import okhttp3.Response;

public class ReportBS extends BottomSheetDialogFragment {

    private EditText email;
    private Button btn_submit;
    private MaterialAutoCompleteTextView autoComplete;

    private static final String TAG = "ReportBS";

    public static ReportBS newInstance() {
        return new ReportBS();
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_bottom_sheet, container, false);

        email = view.findViewById(R.id.editEmail);
        autoComplete = view.findViewById(R.id.mReportType);
        btn_submit = view.findViewById(R.id.btn_submit);

        String[] medicineNames = {"expiry report", "low stock report", "distribution report", "inventory"};
        autoComplete.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, medicineNames));

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail = email.getText().toString();
                String mReportType = autoComplete.getText().toString();

                if (mReportType.isEmpty()) {
                    Toast.makeText(getContext(), "Report type is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create json object and send to the server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("requested_by",
                            Integer.parseInt(DataStorage.getInstance(getContext()).getS("id")));
                    jsonObject.put("report_type",
                            mReportType.replace(" ", "_"));
                    jsonObject.put("email", mEmail);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Response response = APIUtil.generateReport(jsonObject);
                File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                // Specify the file name
                String fileName = (mReportType.replace(" ", "_")) + "_" + System.currentTimeMillis() + ".csv";

                // Create the complete file path
                File filePath = new File(download, fileName);

                // Download the file from response
                APIUtil.downloadFile(response, filePath);

                Toast.makeText(getContext(), "Report generated successfully", Toast.LENGTH_SHORT).show();


                try {
                    JSONObject fileObj = new JSONObject();
                    fileObj.put("report_type", mReportType);
                    fileObj.put("file_path", filePath.getAbsolutePath());

                    Set<String> st = DataStorage.getInstance(getContext()).getString("report");
                    if (st == null) {
                        st = new HashSet<>();
                    }
                    st.add(fileObj.toString());
                    DataStorage.getInstance(getContext()).setString("report", st);

                    Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", filePath);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "text/csv");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                    // reset input
                    email.setText("");
                    autoComplete.setText("");

                    getDialog().hide();

                } catch (ActivityNotFoundException e) {
                    // no Activity to handle this kind of files
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                getDialog().hide();
            }
        };
    }
}
