package com.neuqer.n_plus_os.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.ui.recyclerview.DiskAdapter;

public class DiskAnalysisActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disk_analysis);
        recyclerView = findViewById(R.id.disk_analysis_recyclerview);

        DiskAdapter adapter = new DiskAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false));
    }
}