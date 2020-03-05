package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin= findViewById(R.id.button_login);
        buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(click->{
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
    }
}
