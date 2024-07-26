package com.og.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.og.medicare.api.APIUtil;
import com.og.medicare.databinding.ActivityHome2Binding;
import com.og.medicare.ui.bsheet.OrderBS;
import com.og.medicare.ui.bsheet.RegisterBS;
import com.og.medicare.ui.bsheet.ReportBS;
import com.og.medicare.util.DataStorage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Home2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHome2Binding binding;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHome2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        setSupportActionBar(binding.appBarHome2.toolbar);
        binding.appBarHome2.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navController.getCurrentDestination().getId() == R.id.nav_home) {

                } else if (navController.getCurrentDestination().getId() == R.id.nav_inventory) {

                } else if (navController.getCurrentDestination().getId() == R.id.nav_distribution) {

                } else if (navController.getCurrentDestination().getId() == R.id.nav_order) {
                    OrderBS orderBS = OrderBS.newInstance();
                    orderBS.show(getSupportFragmentManager(), "orderBS");
                } else if (navController.getCurrentDestination().getId() == R.id.nav_report) {
                    ReportBS reportBS = ReportBS.newInstance();
                    reportBS.show(getSupportFragmentManager(), "reportBS");
                } else if (navController.getCurrentDestination().getId() == R.id.nav_register) {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    RegisterBS registerBS = RegisterBS.newInstance(mAuth, getApplicationContext());
                    registerBS.show(getSupportFragmentManager(), "registerBS");

                    Snackbar.make(view, "Replace with your own action register", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .setAnchorView(R.id.fab).show();
                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_inventory, R.id.nav_distribution, R.id.nav_order,
                R.id.nav_report, R.id.nav_register, R.id.nav_distribution_list)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        TextView username = binding.navView.getHeaderView(0).findViewById(R.id.username);
        TextView role = binding.navView.getHeaderView(0).findViewById(R.id.role);

        try {
            username.setText(DataStorage.getInstance(getApplicationContext()).getS("email"));
            role.setText(DataStorage.getInstance(getApplicationContext()).getS("type"));

            if (DataStorage.getInstance(getApplicationContext()).getS("type").equalsIgnoreCase("admin")) {
                navigationView.getMenu().findItem(R.id.nav_register).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_inventory).setVisible(true);
            } else {
                navigationView.getMenu().findItem(R.id.nav_register).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_inventory).setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home2, menu);

        // add onclick listener
        menu.findItem(R.id.action_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    // start activity and clear stack
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}