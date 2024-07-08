package com.og.medicare.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.og.medicare.R;
import com.og.medicare.adapter.ListAdapter;
import com.og.medicare.model.Inventory;

import java.util.ArrayList;
import java.util.Date;


public class Home extends AppCompatActivity {

    ListView listView;
    private static ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = findViewById(R.id.item_list);

        ArrayList<Inventory> dataModels = new ArrayList<>();

//        dataModels.add(new Inventory(1, 1, 10, "Paracetamol", "1234", "Tablet", new Date()));
//        dataModels.add(new Inventory(2, 1, 10, "Paracetamol", "1234", "Tablet", new Date()));
//        dataModels.add(new Inventory(3, 1, 10, "Paracetamol", "1234", "Tablet", new Date()));
//        dataModels.add(new Inventory(4, 1, 10, "Paracetamol", "1234", "Tablet", new Date()));

        adapter = new ListAdapter(dataModels,getApplicationContext());

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
    }
}