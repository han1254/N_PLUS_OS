package com.neuqer.n_plus_os.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.memory.SimulationMemory;
import com.neuqer.n_plus_os.memory.thread_factory.thread.InitUserFileTask;
import com.neuqer.n_plus_os.ui.recyclerview.FileOperate;
import com.neuqer.n_plus_os.util.FileLockUtil;

public class FileOperateActivity extends AppCompatActivity {

    private LinearLayout mBtnFileManage;
    private LinearLayout mBtnMemoryAnalysis;
    private LinearLayout mBtnDiskAnalysis;
    private LinearLayout mBtnDirectInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_operate);
        mBtnFileManage = (LinearLayout) findViewById(R.id.operate_btn_file_manage);
        mBtnMemoryAnalysis = findViewById(R.id.operate_btn_memory_analysis);
        mBtnDiskAnalysis = findViewById(R.id.operate_btn_disk_analysis);
        mBtnDirectInfo = findViewById(R.id.operate_btn_directory_info);

        mBtnFileManage.setOnClickListener(v -> {
            Intent intent = new Intent(FileOperateActivity.this, FileManagementActivity.class);
            startActivity(intent);
        });

        mBtnDiskAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(FileOperateActivity.this, DiskAnalysisActivity.class);
            startActivity(intent);
        });

        mBtnMemoryAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(FileOperateActivity.this, MemAnalysisActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileLockUtil.getInstance().getProtectedFiles().clear();
        SimulationMemory.getInstance().clearAllMemory();
    }
}