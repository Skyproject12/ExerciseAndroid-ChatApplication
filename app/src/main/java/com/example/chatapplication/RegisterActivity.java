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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText inputEmail;
    EditText inputPassword;
    Button buttonRegister;

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Create Account");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        inputEmail= findViewById(R.id.input_email);
        inputPassword= findViewById(R.id.input_password);
        buttonRegister= findViewById(R.id.button_register);
        progressDialog= new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();

        progressDialog.setMessage("Registering User...");

        buttonRegister.setOnClickListener(click->{
            String email = inputEmail.getText().toString().trim();
            String password= inputPassword.getText().toString().trim();
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                inputEmail.setError("Invalid Email");
                inputEmail.setFocusable(true);
            }
            else if(password.length()<6){
                inputPassword.setError("Password length at least 6 character");
                inputPassword.setFocusable(true);
            }
            else{
                registerUser(email, password);
            }
        });


    }

    private void registerUser(String email, String password) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    FirebaseUser user= mAuth.getCurrentUser();
                    startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                    finish();
                }
                else{
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
