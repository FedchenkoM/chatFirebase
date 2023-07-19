package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_MESSAGE_SIZE = 180;
    private static final String EMPTY_MESSAGE_ERROR = "Message is empty";
    private static final String LONG_MESSAGE_ERROR = "Message longer than 200 characters";

    private final FirebaseDatabase db = FirebaseDatabase.getInstance();
    private final DatabaseReference dbref = db.getReference("messages");

    EditText messageInput;
    Button sendMessageButton;
    RecyclerView messagesRecycler;
    ArrayList<String> messages = new ArrayList<>();

    public void sendMessageOnClick(View view) {
        String message = messageInput.getText().toString().trim();

        if (message.equals("")) {
            Toast.makeText(getApplicationContext(), EMPTY_MESSAGE_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > MAX_MESSAGE_SIZE) {
            Toast.makeText(getApplicationContext(), LONG_MESSAGE_ERROR, Toast.LENGTH_SHORT).show();
            return;
        }

        dbref.push().setValue(message);
        messageInput.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendMessageButton = findViewById(R.id.send_message_button);
        messageInput = findViewById(R.id.message_input);
        messagesRecycler = findViewById(R.id.messages_recycler);

        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        DataAdapter dataAdapter = new DataAdapter(this, messages);

        messagesRecycler.setAdapter(dataAdapter);

        dbref.addChildEventListener(new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String message = snapshot.getValue(String.class);
                messages.add(message);
                dataAdapter.notifyDataSetChanged();
                messagesRecycler.smoothScrollToPosition(messages.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}