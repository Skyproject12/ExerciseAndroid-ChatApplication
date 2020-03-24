package com.example.chatapplication.Ui.Group;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Data.ModelGroup;
import com.example.chatapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.ViewHolder> {
    private static final int MSG_TYPE_LEFT =0;
    private static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<ModelGroup> modelGroup;
    private FirebaseAuth firebaseAuth;


    public AdapterGroup(Context context, ArrayList<ModelGroup> modelGroup) {
        this.context = context;
        this.modelGroup = modelGroup;
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_goupchat_right, parent, false);
            return new ViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_list, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelGroup model = modelGroup.get(position);
        String message = model.getMessage();
        String timestimp = model.getTimestamp();
        String senderUid = model.getSender();
        // change time stamp to format dateTime
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(Long.parseLong(timestimp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
        // settext message
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);

        setUserName(model, holder);
    }

    private void setUserName(ModelGroup model, ViewHolder holder) {
        // get sender info from uid
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(model.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    String name = ""+ds.child("name").getValue();
                    holder.nameTv.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        // ketika pengirim sama dengan id sekarang
        if(modelGroup.get(position).getSender().equals(firebaseAuth.getUid())){
            // maka message di set di right
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return modelGroup.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTv;
        private TextView messageTv;
        private TextView timeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv= itemView.findViewById(R.id.timeTv);

        }
    }

}
