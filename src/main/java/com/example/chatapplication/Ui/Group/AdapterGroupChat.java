package com.example.chatapplication.Ui.Group;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelGroupChat;
import com.example.chatapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.ViewHolder> {

    private Context context;
    private ArrayList<ModelGroupChat> groupChatList;

    public AdapterGroupChat(Context context, ArrayList<ModelGroupChat> groupChatList) {
        this.context = context;
        this.groupChatList = groupChatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelGroupChat model = groupChatList.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        holder.nameTv.setText("");
        holder.timeTv.setText("");
        holder.messageTv.setText("");

        // tampilkan message terakhir dan waktu ketika user menutup group
        loadLastMessage(model, holder);
        holder.groupTitleTv.setText(groupTitle);
        try{
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_default_white).into(holder.groupIconIv);
        }
        catch(Exception e){
            holder.groupIconIv.setImageResource(R.drawable.ic_default_white);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // intent to detail adapter
                Intent intent= new Intent(context, GroupChatActivity.class);
                // send the groupId
                intent.putExtra("groupId", groupId);
                context.startActivity(intent);

            }
        });
    }

    private void loadLastMessage(ModelGroupChat model, ViewHolder holder) {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        // limit data from last data
        ref.child(model.getGroupId()).child("Messages").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String message = ""+ds.child("message").getValue();
                    String timestamp = ""+ds.child("timestamp").getValue();
                    String sender= ""+ds.child("sender").getValue();

                    Calendar cal = Calendar.getInstance(Locale.getDefault());
                    cal.setTimeInMillis(Long.parseLong(timestamp));
                    String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();

                    holder.messageTv.setText(message);
                    holder.timeTv.setText(dateTime);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    ref.orderByChild("uid").equalTo(sender).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                String name = "" + ds.child("name").getValue();
                                holder.nameTv.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView groupIconIv;
        private TextView groupTitleTv;
        private TextView nameTv;
        private TextView messageTv;
        private TextView timeTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupIconIv = itemView.findViewById(R.id.groupImage);
            groupTitleTv= itemView.findViewById(R.id.groupTitle);
            nameTv = itemView.findViewById(R.id.name_groupTv);
            messageTv= itemView.findViewById(R.id.messageTv);
            timeTv= itemView.findViewById(R.id.timeTv);

        }
    }

}
