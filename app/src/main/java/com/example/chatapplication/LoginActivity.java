package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    TextView forgotPassword;

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
        forgotPassword= findViewById(R.id.forgot_password);

        buttonLogin.setOnClickListener(click->{
            loginUser();
        });
        notAccount.setOnClickListener(click->{
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        forgotPassword.setOnClickListener(click->{
            showRecoverPasswordDialog();
        });

    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=  new LinearLayout(this);
        EditText emailEt = new EditText(this);
        emailEt.setHint("enter your email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEt.setMinEms(16);
        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email =emailEt.getText().toString().trim(); 
                beginRecovery(email);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }); 
        builder.create().show();

    }

    private void beginRecovery(String email) {
        progressDialog.setMessage("Sending Email ...");
        progressDialog.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Send Email", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Failed ...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
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
                        startActivity(new Intent(LoginActivity.this, DashboarActivity.class));
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
