package com.og.medicare.ui.report;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.BuildConfig;
import com.og.medicare.R;
import com.og.medicare.adapter.CommonListAdapter;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentOrderBinding;
import com.og.medicare.databinding.FragmentReportBinding;
import com.og.medicare.model.CommonList;
import com.og.medicare.model.Inventory;
import com.og.medicare.model.Order;
import com.og.medicare.util.DataStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import okhttp3.Response;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;

    private ArrayList<CommonList> dataModels;
    private static CommonListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView listView = binding.itemList;

        Set<String> st = DataStorage.getInstance(getContext()).getString("report");
        dataModels = new ArrayList<>();
        if (st != null) {
            for (String s : st) {
                try {
                    JSONObject obj = new JSONObject(s);
                    dataModels.add(CommonList.builder()
                            .obj(obj).title(obj.getString("report_type"))
                            .subTitle(obj.getString("file_path").split("/")[obj.getString("file_path").split("/").length - 1])
                            .build());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        adapter = new CommonListAdapter(dataModels, getContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CommonList dataModel= dataModels.get(position);

                File file = new File(((JSONObject)dataModel.getObj()).optString("file_path"));
                Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "text/csv");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}