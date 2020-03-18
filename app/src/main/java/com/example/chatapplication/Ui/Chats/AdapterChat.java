package com.example.chatapplication.Ui.Chats;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelChat;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {
    ArrayList<ModelChat> listChat;
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    String imageUrl;
    FirebaseUser firebaseUser;

    public AdapterChat(ArrayList<ModelChat> listChat, String imageUrl) {
        this.listChat = listChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_LEFT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_left, parent, false);
            return new MyHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_right, parent, false);
            return new MyHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // get data
        String message = listChat.get(position).getMessage();
        String timeStamp = listChat.get(position).getTimestamp();
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).into(holder.profileTv);
        }
        catch(Exception e){

        }
        //set status delivered message
        if(position==listChat.size()-1){
            if(listChat.get(position).isSeen()){
                holder.isSeenTv.setText("Seen");
            }
            else{
                holder.isSeenTv.setText("Delivered");
            }
        }
        else{
            holder.isSeenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        // get current user signed
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        // ketika user yang login sama dengan pengirim
        if(listChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }

    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView profileTv;
        TextView messageTv, timeTv, isSeenTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            profileTv= itemView.findViewById(R.id.profileIv);
            messageTv= itemView.findViewById(R.id.messageTv);
            timeTv= itemView.findViewById(R.id.timeTv);
            isSeenTv= itemView.findViewById(R.id.isSeenTv);

        }
    }
}
