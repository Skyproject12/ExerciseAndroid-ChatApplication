package com.example.chatapplication.Ui.Group;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelGroupChat;
import com.example.chatapplication.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

        holder.groupTitleTv.setText(groupTitle);
        holder.nameTv.setText(model.getGroupDescription());
        try{
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_default_white).into(holder.groupIconIv);
        }
        catch(Exception e){
            holder.groupIconIv.setImageResource(R.drawable.ic_default_white);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv= itemView.findViewById(R.id.messageTv);
            timeTv= itemView.findViewById(R.id.timeTv);

        }
    }

}
