package com.example.chatapplication.Ui.Group;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterParticipantAdd extends RecyclerView.Adapter<AdapterParticipantAdd.ViewHolder> {

    private Context context;
    private ArrayList<ModelUser> userList;
    private String groupId;
    // create role participan
    private String myGroupRole;

    public AdapterParticipantAdd(Context context, ArrayList<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_participant_add, parent, false);
        return new ViewHolder(view);
    }

    // select participant display in android adapter
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelUser user = userList.get(position);
        String name = user.getName();
        String email = user.getEmail();
        String image = user.getImage();
        // mengambil uid setiap pengguna yang terdapat di group
        String uid = user.getUid();
        holder.nameTv.setText(name);
        holder.emailTv.setText(email);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_default_white).into(holder.avatarIv);
        } catch (Exception e) {
            holder.avatarIv.setImageResource(R.drawable.ic_default_white);
        }

        // check role user
        checkIfAlreadyExists(user, holder);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check user already check
                // if added, show remove participant / make admin or anything
                // if not added, show participant
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                // check user in group exist or not
                ref.child(groupId).child("Participants").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // user exist in group
                            String hisPrevRole = "" + dataSnapshot.child("role").getValue();

                            // make options menu
                            String[] options;
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Choose Options");
                            // jika role saya creator
                            if (myGroupRole.equals("creator")) {
                                // jika role group teman kita adalah admin
                                if (hisPrevRole.equals("admin")) {
                                    // menampilkan dialog interface
                                    options = new String[]{"Remove Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                removeAdmin(user);
                                            } else {
                                                removeParticipant(user);
                                            }
                                        }
                                    }).show();
                                }
                                // ketika status participant
                                else if (hisPrevRole.equals("participant")) {
                                    options = new String[]{"Make Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                makeAdmin(user);
                                            } else {
                                                removeParticipant(user);
                                            }
                                        }
                                    }).show();
                                }
                            }
                            // ketika role admin
                            else if (myGroupRole.equals("admin")) {
                                // if this is creator
                                if (hisPrevRole.equals("creator")) {
                                    Toast.makeText(context, "creator of group ... ", Toast.LENGTH_SHORT).show();
                                }
                                // if this is admin
                                else if (hisPrevRole.equals("admin")) {
                                    options = new String[]{"Remove Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                removeAdmin(user);
                                            } else {
                                                removeParticipant(user);
                                            }
                                        }
                                    }).show();
                                } else if (hisPrevRole.equals("participant")) {
                                    // iam admin and his participant
                                    options = new String[]{"Make Admin", "Remove User"};
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == 0) {
                                                makeAdmin(user);
                                            } else {
                                                removeParticipant(user);
                                            }
                                        }
                                    }).show();
                                }
                            }
                        }
                        // not participant
                        else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Add Participant")
                                    .setMessage("Add this user in this group?")
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            addParicipant(user);
                                        }
                                    }).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    // add new participant
    private void addParicipant(ModelUser user) {
        HashMap<String, String> participant = new HashMap<>();
        participant.put("uid", user.getUid());
        participant.put("role","participant");
        participant.put("timestamp", user.getUid());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        // add pengguna berdasarkan uid
        ref.child(groupId).child("Participants").child(user.getUid()).setValue(participant).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Added successfull ... ", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // make admin
    private void makeAdmin(ModelUser user) {
        String timestame = ""+System.currentTimeMillis();
        HashMap<String, Object> admin = new HashMap<>();
        admin.put("role","admin");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(user.getUid()).updateChildren(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "The user is now admin ... ", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // remove participant
    private void removeParticipant(ModelUser user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    // make admin to participant
    private void removeAdmin(ModelUser user) {
        HashMap<String, Object> admin = new HashMap<>();
        admin.put("role","participant");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").child(user.getUid()).updateChildren(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "The user is now participant  ... ", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // check role user
    private void checkIfAlreadyExists(ModelUser user, ViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        // check in participant
        ref.child(groupId).child("Participants").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String hisRole = "" + dataSnapshot.child("role").getValue();
                    // set status sebagai role
                    holder.statusTv.setText(hisRole);
                } else {
                    holder.statusTv.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatarIv;
        private TextView nameTv;
        private TextView emailTv;
        private TextView statusTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.image_chat);
            nameTv = itemView.findViewById(R.id.text_nama);
            emailTv = itemView.findViewById(R.id.text_email);
            statusTv = itemView.findViewById(R.id.text_status);

        }
    }
}
