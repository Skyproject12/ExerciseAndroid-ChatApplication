package com.example.chatapplication.Ui.Users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelUser;
import com.example.chatapplication.R;
import com.example.chatapplication.Ui.Chats.ChatActivity;
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

// gunakan constructor kosong ketika suatu data tidak pasti terdapat

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {
    ArrayList<ModelUser> modelUsers = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    String myUid;

    public AdapterUser(ArrayList<ModelUser> modelUsers) {
        this.modelUsers = modelUsers;
        firebaseAuth = FirebaseAuth.getInstance();
        myUid= firebaseAuth.getUid();

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        String hisUID =  modelUsers.get(i).getUid();
        String userImage =""+modelUsers.get(i).getImage();
        String userName = modelUsers.get(i).getName();
        String userEmail = modelUsers.get(i).getEmail();
        myHolder.mNaveTv.setText(userName);
        myHolder.mEmailTv.setText(userEmail);
        Picasso.get().load(userImage).placeholder(R.drawable.ic_default_white).into(myHolder.mAvatarIv);
        myHolder.itemView.setOnClickListener(click -> {
            imBlockedORNot(hisUID, myHolder);
        });
        myHolder.blockTv.setImageResource(R.drawable.ic_unblocked);
        checkIsBlocked(hisUID, myHolder, i);

        // click block
        myHolder.blockTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelUsers.get(i).isBlocked()){
                    unBlockUser(hisUID);
                }
                else{
                    blockUser(hisUID);
                }
            }
        });
    }

    // check status bloc
    private void checkIsBlocked(String hisUID, MyHolder myHolder, int i) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUser").orderByChild("uid").equalTo(hisUID)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.exists()){
                        myHolder.blockTv.setImageResource(R.drawable.ic_blocked);
                        modelUsers.get(i).setBlocked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void imBlockedORNot(String hisUid, RecyclerView.ViewHolder viewHolder){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(hisUid).child("BlockedUser").orderByChild("uid").equalTo(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.exists()){
                        // when the block not process  beetween user
                        Toast.makeText(viewHolder.itemView.getContext(), "Sudah di bloc", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // when not block
                Intent intent= new Intent(viewHolder.itemView.getContext(), ChatActivity.class);
                intent.putExtra("hisUid", hisUid);
                viewHolder.itemView.getContext().startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void blockUser(String hisUID) {
        HashMap<String, String> hasMap = new HashMap<>();
        hasMap.put("uid", hisUID);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUser").child(hisUID).setValue(hasMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // block

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void unBlockUser(String hisUID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(myUid).child("BlockedUser").orderByChild("uid").equalTo(hisUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.exists()){
                        ds.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // unbloc
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelUsers.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView mAvatarIv;
        TextView mNaveTv;
        TextView mEmailTv;
        ImageView blockTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mNaveTv = itemView.findViewById(R.id.text_nama);
            mEmailTv = itemView.findViewById(R.id.text_status);
            mAvatarIv = itemView.findViewById(R.id.image_chat);
            blockTv= itemView.findViewById(R.id.unblock);

        }
    }
}
