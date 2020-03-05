package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    TextView notAccount;
    EditText inputEmail;
    EditText inputPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        notAccount= findViewById(R.id.not_have_acccount);
        inputEmail= findViewById(R.id.input_email);
        inputPassword= findViewById(R.id.input_password);
        buttonLogin= findViewById(R.id.button_login);
        mAuth=FirebaseAuth.getInstance();
        progressDialog= new ProgressDialog(this);

        buttonLogin.setOnClickListener(click->{
            loginUser();
        });
        notAccount.setOnClickListener(click->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    public void loginUser(){
        progressDialog.setMessage("Logging In ...");
        progressDialog.show();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inputEmail.setError("enter valid email");
            inputEmail.setFocusable(true);
            progressDialog.dismiss();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                        progressDialog.dismiss();
                    }
                    else{
                        progressDialog.dismiss();
                    }
                }
            });
            progressDialog.dismiss();
        }

    }
}
