package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView textProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Profile");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        firebaseAuth= FirebaseAuth.getInstance();
        textProfile=findViewById(R.id.text_profile);

    }
    private void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user== null){
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        showProfile();
        super.onStart();
    }
    private void showProfile(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        textProfile.setText(user.getEmail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.action_loggout){
            firebaseAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
