package com.jack.applockscreen.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jack.applockscreen.R;
import com.jack.applockscreen.service.LockScreenService;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "LOCK";
    private LockScreenService service;
    private Button btn_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_open = (Button) findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, LockScreenService.class));
            }
        });
    }
}
