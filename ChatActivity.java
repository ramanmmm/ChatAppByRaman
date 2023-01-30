package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.mychat.databinding.ActivityAuthenticationBinding;
import com.example.mychat.databinding.ActivityChatBinding;
import com.example.mychat.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    MessageAdapter messageAdapter;
    DatabaseReference databaseReferenceSender,databaseReferenceRecever;
    String reciverId,senderRoom,recieverRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        messageAdapter=new MessageAdapter(this);
        binding.recycler.setAdapter(messageAdapter);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        reciverId=getIntent().getStringExtra("id");
        senderRoom= FirebaseAuth.getInstance().getUid()+reciverId;
        recieverRoom=reciverId+FirebaseAuth.getInstance().getUid();
        databaseReferenceSender= FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        databaseReferenceRecever= FirebaseDatabase.getInstance().getReference("chats").child(recieverRoom);
        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageAdapter.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModel messageModel=dataSnapshot.getValue(MessageModel.class);
                    messageAdapter.add(messageModel);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=binding.message.getText().toString();
                if(message.trim().length()>0){
                    sendMessage(message);
                }
            }private void sendMessage(String message) {
                String messageID= UUID.randomUUID().toString();
                MessageModel messageModel=new MessageModel(messageID,FirebaseAuth.getInstance().getUid(),message);
                messageAdapter.add(messageModel);
                databaseReferenceSender.child(messageID).setValue(messageModel);
                databaseReferenceRecever.child(messageID).setValue(messageModel);
            }
        });

    }
}
