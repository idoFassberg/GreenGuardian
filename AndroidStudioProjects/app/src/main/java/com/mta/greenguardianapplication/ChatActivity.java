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
    private DatabaseReference databaseReference;
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
        setListeners();
        loadReceiverDetails();
        init();
        listenMessages();
    }

    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, /*getBitmapFromEncodedString(receiverUser.image),*/ receiverUser.getId());
        binding.chatRecyclerView.setAdapter(chatAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
    }

    private void sendMessage() {
        DatabaseReference chatsRef = databaseReference;
        HashMap<String, Object> message = new HashMap<>();
        String messageId = chatsRef.push().getKey();
        message.put("sender_id", user.getUid());
        message.put("receiver_id", receiverUser.getId());
        String messageText = binding.inputMessage.getText().toString();
        message.put("message", messageText);
        message.put("timestamp", new Date().getTime());
        chatsRef.child(messageId).setValue(message);

        binding.inputMessage.setText(null);
    }

    private void listenMessages() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        String receiverUserId = receiverUser.id;

        // Listen for messages sent by the current user to the receiver
        databaseReference.orderByChild("sender_id").equalTo(currentUserId).addValueEventListener(eventListener);

        // Listen for messages sent by the receiver to the current user
        databaseReference.orderByChild("receiver_id").equalTo(receiverUserId).addValueEventListener(eventListener);
    }

    private final ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            chatMessages.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String message = snapshot.child("message").getValue(String.class);
                String receiverId = snapshot.child("receiver_id").getValue(String.class);
                String senderId = snapshot.child("sender_id").getValue(String.class);
                Long time = snapshot.child("timestamp").getValue(long.class);
                ChatMessage chatMessage = new ChatMessage(senderId, receiverId, message, time);
                if (chatMessage != null) {
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> Long.compare(obj2.dateTime, obj1.dateTime));
            chatAdapter.notifyDataSetChanged();

            if (!chatMessages.isEmpty()) {
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("MyApp", "onCancelled: " + databaseError.getMessage());
        }
    };

    /*private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if (value != null){
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(user.getUid());
                    chatMessage.receiverId = documentChange.getDocument().getString(receiverUser.getId());
                    chatMessage.message = documentChange.getDocument().getString(messageText);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(new Date().getTime()));
                    chatMessage.dateObject = documentChange.getDocument().getDate(new Date().getTime());
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };
*/

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails(){
        String userName = getIntent().getStringExtra("name");
        String userId = getIntent().getStringExtra("id");
        receiverUser.email = getIntent().getStringExtra("email");
        Log.d(userId, "userID:");
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