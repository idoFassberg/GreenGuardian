package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.mta.greenguardianapplication.adapter.ChatAdapter;
import com.mta.greenguardianapplication.databinding.ActivityChatBinding;
import com.mta.greenguardianapplication.model.ChatMessage;
import com.mta.greenguardianapplication.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.annotations.NonNull;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private DatabaseReference database;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        receiverUser = new User();
        loadReceiverDetails();
        setListeners();
        init();
        listenMessages();
    }

    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, /*getBitmapFromEncodedString(receiverUser.image),*/ receiverUser.id);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseDatabase.getInstance().getReference("Chats");
    }

    private void sendMessage() {
        user = mAuth.getCurrentUser();
        DatabaseReference chatsRef = database;
        String messageText = binding.inputMessage.getText().toString();

        if (!messageText.isEmpty() && receiverUser != null) {
            String messageId = chatsRef.push().getKey(); // Generate a unique key for each message
            Log.d("MyApp", "Sending message: " + messageText);
            Log.d("MyApp", "Message ID: " + messageId);
            HashMap<String, Object> message = new HashMap<>();
            message.put("sender_id", user.getUid());
            message.put("receiver_id", receiverUser.id);
            message.put("message", messageText);
            message.put("timestamp", new Date().getTime()); // Store timestamp as a long value
            chatsRef.child(messageId).setValue(message); // Use the messageId as the key for each message
            Log.d("MyApp", "Message sent successfully!");
            binding.inputMessage.setText(null);
        } else {
            // Handle the case when receiverUser is null or message is empty
        }
    }


    private void listenMessages() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        String receiverUserId = receiverUser.id;

        // Listen for messages sent by the current user to the receiver
        database.orderByChild("sender_id").equalTo(currentUserId).addValueEventListener(eventListener);

        // Listen for messages sent by the receiver to the current user
        database.orderByChild("receiver_id").equalTo(receiverUserId).addValueEventListener(eventListener);
    }

    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            chatMessages.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                if (chatMessage != null) {
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.getTimestamp().compareTo(obj2.getTimestamp()));
            chatAdapter.notifyDataSetChanged();

            binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle onCancelled if needed
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails(){
        String userName = getIntent().getStringExtra("name");
        String userId = getIntent().getStringExtra("id");
        receiverUser.name = userName;
        receiverUser.id = userId;
        binding.textName.setText(receiverUser.name);
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
        binding.layoutSend.setOnClickListener(v->sendMessage());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}