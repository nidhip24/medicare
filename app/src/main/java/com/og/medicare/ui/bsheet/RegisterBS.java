package com.og.medicare.ui.bsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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
import com.og.medicare.model.Inventory;
import com.og.medicare.model.Role;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Response;

public class RegisterBS extends BottomSheetDialogFragment {

    private EditText name, email, hstation;
    private Button register;
    private MaterialAutoCompleteTextView autoComplete;

    ArrayList<Role> dataModels;

    private FirebaseAuth mAuth;
    private Context mContext;
    private static final String TAG = "RegisterBS";

    public static RegisterBS newInstance(FirebaseAuth mAuth, Context mContext) {
        return new RegisterBS(mAuth, mContext);
    }

    RegisterBS(FirebaseAuth mAuth, Context mContext) {
        this.mAuth = mAuth;
        this.mContext = mContext;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_bottom_sheet, container, false);

        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        hstation = view.findViewById(R.id.hstation);
        register = view.findViewById(R.id.register);
        autoComplete = view.findViewById(R.id.roles);

        Response rolesResponse = APIUtil.getRoles();
        dataModels = new ArrayList<>();
        parseResponse(rolesResponse);

        String[] medicineNames = new String[dataModels.size()];
        for (int i = 0; i < dataModels.size(); i++) {
            medicineNames[i] = dataModels.get(i).getName();
        }
        autoComplete.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, medicineNames));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mName = name.getText().toString();
                String mEmail = email.getText().toString();
                String mHStation = hstation.getText().toString();
                String mRole = autoComplete.getText().toString();

                if (mName.isEmpty() || mEmail.isEmpty() || mHStation.isEmpty() || mRole.isEmpty()) {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                int roleId = findItem(mRole);
                if (roleId == -1) {
                    Toast.makeText(getContext(), "Role not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create json object and send to the server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("rid", roleId);
                    jsonObject.put("name", mName);
                    jsonObject.put("email", mEmail);
                    jsonObject.put("healthstation", mHStation);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try (Response response = APIUtil.addUser(jsonObject)) {
                    if (response != null) {
                        if (response.isSuccessful() && response.code() == 200) {

                            mAuth.createUserWithEmailAndPassword(mEmail, "123456")
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Log.d(TAG, "onComplete: " + user.getEmail());
                                                Toast.makeText(getContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getContext(), "User could not be created", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            // reset the form
                            name.setText("");
                            email.setText("");
                            hstation.setText("");
                            autoComplete.setText("");
                        } else {
                            Toast.makeText(getContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return view;
    }

    private int findItem(String name) {
        for (int i = 0; i < dataModels.size(); i++) {
            if (dataModels.get(i).getName().equalsIgnoreCase(name)) {
                return dataModels.get(i).getId();
            }
        }
        return -1;
    }

    private void parseResponse(Response response) {
        try {
            JSONArray inventory = new JSONArray(response.body().string());
            for (int i = 0; i < inventory.length(); i++) {
                dataModels.add(new Role(
                        inventory.getJSONObject(i).getInt("id"),
                        inventory.getJSONObject(i).getString("name")
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
