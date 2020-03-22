package com.example.chatapplication.Ui.Group;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.R;

public class GroupChatActivity extends AppCompatActivity {

    String groudId;
    private Toolbar toolbar;
    private ImageView imageGroup;
    private TextView namaGroup;
    private RecyclerView recyclerChat;
    private ImageButton buttonSend;
    private EditText inputMessage;
    private ImageView buttonAttach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Intent intent = getIntent();
        groudId = intent.getStringExtra("groupId");
        toolbar = findViewById(R.id.toolbar_chat);
        imageGroup = findViewById(R.id.image_users);
        namaGroup = findViewById(R.id.text_nama);
        recyclerChat = findViewById(R.id.recycler_listchat);
        buttonSend = findViewById(R.id.image_send);
        inputMessage = findViewById(R.id.message_input);
        buttonAttach = findViewById(R.id.image_attach);

    }
}
