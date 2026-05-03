package uws.ac.uk.noteapp;

public class ChatMessage {
    private String text;
    private boolean isUser; // true, если писали мы; false, если писал бот

    public ChatMessage(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }

    public String getText() {
        return text;
    }

    public boolean isUser() {
        return isUser;
    }
}