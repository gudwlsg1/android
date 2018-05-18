package com.example.wjsur0329.seeker;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.wjsur0329.seeker.Helper.InventoryHelper;
import com.example.wjsur0329.seeker.list.InventoryAdapter;

public class InventoryActivity extends AppCompatActivity {

    private InventoryAdapter adapter;
    private RecyclerView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        adapter = new InventoryAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        list = (RecyclerView)findViewById(R.id.list);
        list.setLayoutManager(manager);
        list.setAdapter(adapter);

        InventoryHelper helper = new InventoryHelper(this, "inventory", null, 1);
        adapter.setData(helper.getAll());
    }
}
