package com.example.chatapplication.Ui.Users;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelUser;
import com.example.chatapplication.R;
import com.example.chatapplication.Ui.Dashboard.DashboarActivity;
import com.example.chatapplication.Ui.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView recyclerChat;
    View view;
    AdapterUser adapterUser;
    ArrayList<ModelUser> userList;
    FirebaseAuth firebaseAuth;


    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerChat = view.findViewById(R.id.recycler_chat);
        recyclerChat.setHasFixedSize(true);
        recyclerChat.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseAuth= FirebaseAuth.getInstance();
        userList = new ArrayList<>();
        getAllUser();
        return view;
    }

    private void getAllUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                if(dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ModelUser modelUser = ds.getValue(ModelUser.class);
                        // save value into model modelUser data
                        if (!modelUser.getUid().equals(user.getUid())) {
                            userList.add(modelUser);
                        }
                        adapterUser = new AdapterUser(userList);
                        recyclerChat.setAdapter(adapterUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id= item.getItemId();
        if(id==R.id.action_loggout){
            firebaseAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }


}
