package com.mta.greenguardianapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Half;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mta.greenguardianapplication.adapter.ChatAdapter;
import com.mta.greenguardianapplication.databinding.ActivityChatBinding;
import com.mta.greenguardianapplication.databinding.ActivityMainChatBinding;
import com.mta.greenguardianapplication.model.ChatMessage;
import com.mta.greenguardianapplication.model.User;
import com.mta.greenguardianapplication.utilities.Constants;
import com.mta.greenguardianapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        receiverUser = (User) getIntent().getSerializableExtra("User");
        setListeners();
        loadReceiverDetails();
        init();
    }

    private void init(){
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, /*getBitmapFromEncodedString(receiverUser.image),*/ receiverUser.id);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessage() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        String messageText = binding.inputMessage.getText().toString();
        if (!messageText.isEmpty() && receiverUser != null) {
            HashMap<String, Object> message = new HashMap<>();
            message.put("sender_id", getIntent().getStringExtra("current_user_id"));
            message.put("receiver_id", receiverUser.id);
            message.put("message", messageText);
            message.put("timestamp", new Date());
            /*database.collection("chats").add(message);*/
            binding.inputMessage.setText("");
        } else {
            // Handle the case when receiverUser is null or message is empty
        }
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails(){
        String userName = getIntent().getStringExtra("name");
        binding.textName.setText(userName);
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
        binding.layoutSend.setOnClickListener(v->sendMessage());
    }
}