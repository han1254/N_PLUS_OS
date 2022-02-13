package com.neuqer.n_plus_os.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.neuqer.n_plus_os.R;
import com.neuqer.n_plus_os.memory.thread_factory.thread.InitTask;

public class LoginActivity extends AppCompatActivity implements LoginListener {

    private EditText mLoginEditText;
    private Button mLoginEnterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mLoginEditText = findViewById(R.id.login_edit_txt);
        mLoginEnterBtn = findViewById(R.id.login_enter_btn);

        mLoginEnterBtn.setOnClickListener(v -> {
            new InitTask(this).execute(mLoginEditText.getText().toString().trim());
        });
    }

    @Override
    public void onOperateComplete(int result) {
        Toast.makeText(this, "进入主界面", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, FileOperateActivity.class);
        startActivity(intent);
    }
}