package com.example.chatapplication.Ui.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.chatapplication.Ui.Chats.ChatsFragment;
import com.example.chatapplication.Ui.Group.GroupChatFragment;
import com.example.chatapplication.Ui.Home.HomeFragment;
import com.example.chatapplication.Ui.Login.LoginActivity;
import com.example.chatapplication.Ui.Main.MainActivity;
import com.example.chatapplication.Ui.Profile.ProfileFragment;
import com.example.chatapplication.R;
import com.example.chatapplication.Ui.Users.UsersFragment;
import com.example.chatapplication.notifications.Token;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class DashboarActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private Fragment fragment = new HomeFragment();
    private static final String KEY_FRAGMENT = "fragment";
    FirebaseAuth firebaseAuth;
    String mUID;

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
        checkUserStatus();
        // update token
        updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    // update token every open apps
    public  void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Token");
        Token mToken= new Token(token);
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(mToken);
    }

    private void checkUserStatus(){
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user== null){
            startActivity(new Intent(DashboarActivity.this, MainActivity.class));
            mUID = user.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            // set share preference
            editor.putString("Current_USERID", mUID);
            editor.apply();
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
                    case R.id.nav_more:
                        fragment = new GroupChatFragment();
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

//    private void showMoreOptions(){
//        PopupMenu popupMenu = new PopupMenu(this, mBottomNavigationView, Gravity.END);
//        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Notifications");
//        popupMenu.getMenu().add(Menu.NONE, 0, 0, "Group Chats");
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//                if(id==0){
//
//                }
//                else if(id==1){
//
//                }
//                return false;
//            }
//        });
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getSupportFragmentManager().putFragment(outState, KEY_FRAGMENT, fragment);
        super.onSaveInstanceState(outState);
    }
}
