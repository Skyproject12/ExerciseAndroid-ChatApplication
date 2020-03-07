package com.example.chatapplication.Ui.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.chatapplication.Ui.Chats.ChatsFragment;
import com.example.chatapplication.Ui.Home.HomeFragment;
import com.example.chatapplication.Ui.Login.LoginActivity;
import com.example.chatapplication.Ui.Main.MainActivity;
import com.example.chatapplication.Ui.Profile.ProfileFragment;
import com.example.chatapplication.R;
import com.example.chatapplication.Ui.Users.UsersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboarActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private Fragment fragment = new HomeFragment();
    private static final String KEY_FRAGMENT = "fragment";
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboar);
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("DashBoard");
        firebaseAuth= FirebaseAuth.getInstance();

        tabFragment();
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        else {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_FRAGMENT);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

        }
    }

    private void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user== null){
            startActivity(new Intent(DashboarActivity.this, MainActivity.class));
            finish();
        }
    }

    // when the backkey is press in android
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


    private void tabFragment() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.nav_users:
                        fragment = new UsersFragment();
                        break;
                    case R.id.nav_chat:
                        fragment = new ChatsFragment();
                        break;

                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .commit();
                return true;
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, KEY_FRAGMENT, fragment);
        super.onSaveInstanceState(outState);
    }
}
