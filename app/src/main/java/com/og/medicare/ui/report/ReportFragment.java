package com.og.medicare.ui.report;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.BuildConfig;
import com.og.medicare.R;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.FragmentOrderBinding;
import com.og.medicare.databinding.FragmentReportBinding;

import org.json.JSONObject;

import java.io.File;

import okhttp3.Response;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
        }

        Response response = APIUtil.generateReport(new JSONObject());
        File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        // Specify the file name
        String fileName = "expiry_report.csv";

        // Create the complete file path
        File filePath = new File(download, fileName);

        APIUtil.downloadFile(response, filePath);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}