package uws.ac.uk.noteapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "GeminiChatbot";

    private static final String API_KEY = "YOUR_GEMINI_API_KEY";
    private static final String MODEL_NAME = "gemini-2.5-flash";

    private ImageButton buttonCloseChat;
    private EditText messageEditText;
    private Button sendButton;

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private GenerativeModelFutures generativeModel;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);


        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }


        buttonCloseChat = findViewById(R.id.buttonCloseChat);
        messageEditText = findViewById(R.id.editTextMessage);
        sendButton = findViewById(R.id.buttonSend);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);

        buttonCloseChat.setOnClickListener(v -> finish());


        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);


        try {
            GenerativeModel model = new GenerativeModel(MODEL_NAME, API_KEY);
            generativeModel = GenerativeModelFutures.from(model);
            executorService = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
        }

        addBotMessage("Hello! I am your AI Assistant. How can I help you today?");

        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) return;


        addUserMessage(messageText);
        messageEditText.setText("");
        sendButton.setEnabled(false);


        chatMessages.add(new ChatMessage("Thinking...", false));
        int thinkingIndex = chatMessages.size() - 1;
        chatAdapter.notifyItemInserted(thinkingIndex);
        chatRecyclerView.scrollToPosition(thinkingIndex);


        executorService.execute(() -> {
            try {

                Content content = new Content.Builder().addText(messageText).build();
                ListenableFuture<GenerateContentResponse> responseFuture = generativeModel.generateContent(content);

                Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse response) {
                        String responseText = response.getText();
                        runOnUiThread(() -> {
                            if (responseText != null) {
                                chatMessages.set(thinkingIndex, new ChatMessage(responseText, false));
                            } else {
                                chatMessages.set(thinkingIndex, new ChatMessage("I couldn't generate a response.", false));
                            }
                            chatAdapter.notifyItemChanged(thinkingIndex);
                            chatRecyclerView.scrollToPosition(thinkingIndex);
                            sendButton.setEnabled(true);
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "API Error", t);
                        runOnUiThread(() -> {
                            String errorMsg = t.getMessage();
                            if (errorMsg != null && errorMsg.contains("503")) {
                                errorMsg = "Server is overloaded. Please try again in a minute.";
                            }
                            chatMessages.set(thinkingIndex, new ChatMessage("❌ Error: " + errorMsg, false));
                            chatAdapter.notifyItemChanged(thinkingIndex);
                            sendButton.setEnabled(true);
                        });
                    }
                }, MoreExecutors.directExecutor());

            } catch (Exception e) {
                Log.e(TAG, "Execution error", e);
                runOnUiThread(() -> {
                    chatMessages.set(thinkingIndex, new ChatMessage("❌ System error occurred.", false));
                    chatAdapter.notifyItemChanged(thinkingIndex);
                    sendButton.setEnabled(true);
                });
            }
        });
    }

    private void addUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    private void addBotMessage(String message) {
        chatMessages.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}