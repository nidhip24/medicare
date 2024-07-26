package com.og.medicare.ui.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.og.medicare.adapter.ListAdapter;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentHomeBinding;
import com.og.medicare.databinding.FragmentOrderBinding;
import com.og.medicare.databinding.FragmentRegisterBinding;
import com.og.medicare.model.CommonList;
import com.og.medicare.model.Inventory;
import com.og.medicare.model.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Response;


public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    ListView listView;
    private static CommonListAdapter adapter;

    private ArrayList<CommonList> dataModels;
    private ArrayList<CommonList> filteredDataModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = binding.itemList;
        EditText search = binding.search;

        dataModels = new ArrayList<>();
        filteredDataModels = new ArrayList<>();

        adapter = new CommonListAdapter(filteredDataModels,getContext());

        Response userResponse = APIUtil.getRegisteredUser();

        try {
            JSONArray jsonArray = new JSONArray(userResponse.body().string());
            for (int i = 0; i < jsonArray.length(); i++) {
                ZonedDateTime zonedDateTime = null;
                Date created_at = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    zonedDateTime = ZonedDateTime.parse(jsonArray.getJSONObject(i).getString("created_at"),
                            DateTimeFormatter.ISO_DATE_TIME);
                    LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
                    created_at = Date.from(localDateTime.atZone(zonedDateTime.getZone()).toInstant());
                }

                User user = User.builder()
                        .id(jsonArray.getJSONObject(i).getInt("id"))
                        .rid(jsonArray.getJSONObject(i).getInt("rid"))
                        .name(jsonArray.getJSONObject(i).getString("name"))
                        .email(jsonArray.getJSONObject(i).getString("email"))
                        .healthstation(jsonArray.getJSONObject(i).getString("healthstation"))
                        .created_at(created_at).build();

                dataModels.add(CommonList.builder()
                        .id(jsonArray.getJSONObject(i).getInt("id"))
                        .title(jsonArray.getJSONObject(i).getString("name"))
                        .subTitle(jsonArray.getJSONObject(i).getString("email"))
                        .obj(user).build());
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
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create(); //Read Update
                alertDialog.setTitle("User information");
                alertDialog.setMessage(
                        "Name: " + ((User) dataModel.getObj()).getName() + "\n" +
                        "Email: " + ((User) dataModel.getObj()).getEmail() + "\n" +
                        "Health Station: " + ((User) dataModel.getObj()).getHealthstation() + "\n" +
                        "Created At: " + ((User) dataModel.getObj()).getCreated_at());

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}