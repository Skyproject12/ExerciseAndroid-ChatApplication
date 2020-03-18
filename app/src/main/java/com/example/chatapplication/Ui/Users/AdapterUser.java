package com.example.chatapplication.Ui.Users;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelUser;
import com.example.chatapplication.R;
import com.example.chatapplication.Ui.Chats.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// gunakan constructor kosong ketika suatu data tidak pasti terdapat

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder> {
    ArrayList<ModelUser> modelUsers = new ArrayList<>();

    public AdapterUser(ArrayList<ModelUser> modelUsers) {
        this.modelUsers = modelUsers;
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
            Intent intent= new Intent(myHolder.itemView.getContext(), ChatActivity.class);
            intent.putExtra("hisUid", hisUID);
            myHolder.itemView.getContext().startActivity(intent);
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

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mNaveTv = itemView.findViewById(R.id.text_nama);
            mEmailTv = itemView.findViewById(R.id.text_status);
            mAvatarIv = itemView.findViewById(R.id.image_chat);

        }
    }
}
