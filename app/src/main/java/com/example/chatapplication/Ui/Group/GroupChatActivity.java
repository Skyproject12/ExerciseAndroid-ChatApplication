package com.example.chatapplication.Ui.Group;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelGroup;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    String groudId;
    private ImageView imageGroup;
    private TextView namaGroup;
    private RecyclerView recyclerChat;
    private ImageButton buttonSend;
    private EditText inputMessage;
    private ImageView buttonAttach;
    private FirebaseAuth mAuth;
    private ArrayList<ModelGroup> groupArray;
    private AdapterGroup adapterGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Intent intent = getIntent();
        // get data group id
        groudId = intent.getStringExtra("groupId");
        imageGroup = findViewById(R.id.image_users);
        namaGroup = findViewById(R.id.text_nama);
        recyclerChat = findViewById(R.id.chatRv);
        buttonSend = findViewById(R.id.image_send);
        inputMessage = findViewById(R.id.message_input);
        buttonAttach = findViewById(R.id.image_attach);
        mAuth = FirebaseAuth.getInstance();
        loadGroupInfo();
        // click button send
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get edittext value
                String message = inputMessage.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "require message", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(message);
                    inputMessage.setText("");
                }
            }
        });

        loadGroupMessage();

    }

    // load the message from firebase
    private void loadGroupMessage() {
        groupArray= new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        // mengambil data dari message di group untuk dikirim ke adapter
        ref.child(groudId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupArray.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelGroup model = ds.getValue(ModelGroup.class);
                    groupArray.add(model);
                }
                adapterGroup = new AdapterGroup( GroupChatActivity.this, groupArray);
                recyclerChat.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
                recyclerChat.setAdapter(adapterGroup);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // send message
    private void sendMessage(String message) {
        // get current time stamp
        String timestamp = ""+System.currentTimeMillis();
        //save into hasmap
        HashMap<String, Object> send = new HashMap<>();
        send.put("sender",""+mAuth.getUid());
        send.put("message",""+message);
        send.put("timestamp",""+timestamp);
        // send type of message
        send.put("type",""+"text");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        // use timestimp to difference all message
        ref.child(groudId).child("Messages").child(timestamp).setValue(send).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatActivity.this, "failed send", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // load info group
    private void loadGroupInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        // get from group
        reference.orderByChild("groupId").equalTo(groudId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String groupTitle = ""+ds.child("groupTitle").getValue();
                    String groupDescription = ""+ds.child("groupDescription").getValue();
                    String groupIcon = ""+ds.child("groupIcon").getValue();
                    String timestamp = ""+ds.child("timestamp").getValue();
                    String createBy = ""+ds.child("createdBy").getValue();

                    namaGroup.setText(groupTitle);
                    // set image group
                    try{
                        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_default_white).into(imageGroup);
                    }
                    catch (Exception e){
                        imageGroup.setImageResource(R.drawable.ic_default_white);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
