package com.example.chatapplication.Ui.Group;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelGroup;
import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private String myGroupRole="";
    private Toolbar mToolbar;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 2000;
    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri image_uri = null;


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
        mToolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        loadGroupInfo();
        cameraPermission = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        storagePermission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        // click button send
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get edittext value
                String message = inputMessage.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "require message", Toast.LENGTH_SHORT).show();
                } else{
                    sendMessage(message);
                    inputMessage.setText("");
                }
            }
        });
        //Toast.makeText(this, "role aku adalah "+myGroupRole, Toast.LENGTH_SHORT).show();
        loadGroupMessage();
        loadMyGroupRole();

        buttonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageImportDialog();

            }
        });

    }

    // show choose image
    private void showImageImportDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickCamera();
                    }
                } else {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickGallery();
                    }
                }
            }
        }).show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "GroupImageTitle");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "GroupImageDescription");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                sendImageMessage();
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                sendImageMessage();
            }
        }
    }

    private void sendImageMessage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait ...");
        progressDialog.setMessage("Sending Image ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String filenamePath = "ChatImages/" + "" + System.currentTimeMillis();
        StorageReference reference = FirebaseStorage.getInstance().getReference(filenamePath);
        reference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!p_uriTask.isSuccessful()) ;
                Uri p_download = p_uriTask.getResult();
                if (p_uriTask.isSuccessful()) {
                    // get current time stamp
                    String timestamp = "" + System.currentTimeMillis();
                    //save into hasmap
                    HashMap<String, Object> send = new HashMap<>();
                    send.put("sender", "" + mAuth.getUid());
                    send.put("message", "" + p_download);
                    send.put("timestamp", "" + timestamp);
                    // send type of message
                    send.put("type", "" + "image");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                    // use timestimp to difference all message
                    ref.child(groudId).child("Messages").child(timestamp).setValue(send).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            inputMessage.setText("");
                            progressDialog.dismiss();

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccept && writeStorageAccept) {
                        pickCamera();
                    } else {
                        Toast.makeText(this, "required permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccept) {
                        pickGallery();
                    } else {
                        Toast.makeText(this, "required permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    // get role
    private void loadMyGroupRole() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groudId).child("Participants").orderByChild("uid").equalTo(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    myGroupRole = ""+ds.child("role").getValue();
                    // refresh menu
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group, menu);
        Toast.makeText(GroupChatActivity.this, "role"+myGroupRole, Toast.LENGTH_SHORT).show();
//        if(myGroupRole.equals("creator") || myGroupRole.equals("admin")){
//            menu.findItem(R.id.action_add_participant).setVisible(true);
//        }
//        else{
//            menu.findItem(R.id.action_add_participant).setVisible(false);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add_participant){
            Intent intent = new Intent(this,GroupParticaipantActivity.class );
            intent.putExtra("groupId", groudId);
            startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
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
