package uws.ac.uk.noteapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (message.isUser()) {
            holder.textUserMessage.setText(message.getText());
            holder.textUserMessage.setVisibility(View.VISIBLE);
            holder.textBotMessage.setVisibility(View.GONE);
        } else {
            holder.textBotMessage.setText(message.getText());
            holder.textBotMessage.setVisibility(View.VISIBLE);
            holder.textUserMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textUserMessage, textBotMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textUserMessage = itemView.findViewById(R.id.textUserMessage);
            textBotMessage = itemView.findViewById(R.id.textBotMessage);
        }
    }
}