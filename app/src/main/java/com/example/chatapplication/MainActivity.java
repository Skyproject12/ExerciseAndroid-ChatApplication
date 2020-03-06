package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    Button buttonRegister;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonLogin= findViewById(R.id.button_login);
        buttonRegister= findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(click->{
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        });
        buttonLogin.setOnClickListener(click->{
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
        firebaseAuth= FirebaseAuth.getInstance();

        checkUserStatus();

    }

    private void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user!= null){
            startActivity(new Intent(MainActivity.this, DashboarActivity.class));
            finish();
        }
    }
}
