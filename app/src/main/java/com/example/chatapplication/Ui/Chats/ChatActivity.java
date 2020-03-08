package com.example.chatapplication.Ui.Chats;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelChat;
import com.example.chatapplication.Data.ModelUser;
import com.example.chatapplication.R;
import com.example.chatapplication.Ui.Login.LoginActivity;
import com.example.chatapplication.Ui.Main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView userProfile;
    TextView nameTv;
    TextView userStatusTv;
    EditText messageEt;
    ImageButton sendImage;
    FirebaseAuth firebaseAuth;
    String hisUid;
    String myUid;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String hisImage;
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModelChat> chatList;
    AdapterChat adapterChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        userProfile = findViewById(R.id.image_users);
        recyclerView = findViewById(R.id.recycler_listchat);
        nameTv = findViewById(R.id.text_nama);
        userStatusTv = findViewById(R.id.text_status);
        messageEt = findViewById(R.id.message_input);
        sendImage = findViewById(R.id.image_send);
        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        hisUid = intent.getStringExtra("hisUid");
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        checkUserStatus();
        myUid = user.getUid();
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        // search user by user info
        Query userQuery = databaseReference.orderByChild("uid").equalTo(hisUid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = "" + ds.child("name").getValue();
                    hisImage = "" + ds.child("image").getValue();
                    nameTv.setText(name);
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_default_white).into(userProfile);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.ic_default_white).into(userProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendImage.setOnClickListener(click -> {
            String message = messageEt.getText().toString();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Cannot send the empty message", Toast.LENGTH_SHORT).show();
            } else {
                sendMessage(message);
            }
        });

        readMessage();
        seenMessage();

    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat modelChat = ds.getValue(ModelChat.class);
                    if(modelChat.getReceiver().equals(myUid) && modelChat.getSender().equals(hisUid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        ds.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                // get all chat
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelChat chat = ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid) ||
                            chat.getReceiver().equals(hisUid) && chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat = new AdapterChat((ArrayList<ModelChat>) chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message) {

        String timeStamp = String.valueOf(System.currentTimeMillis());
        //sendt Message
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hasMap = new HashMap<>();
        hasMap.put("sender", myUid);
        hasMap.put("receiver", hisUid);
        hasMap.put("message", message);
        hasMap.put("timestamp", timeStamp);
        hasMap.put("isSeen", false);
        databaseReference.child("Chats").push().setValue(hasMap);
        messageEt.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_loggout) {
            firebaseAuth.signOut();
            startActivity(new Intent(ChatActivity.this, LoginActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }
}
