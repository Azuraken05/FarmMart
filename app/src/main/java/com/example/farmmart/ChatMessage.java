package com.example.farmmart;

public class ChatMessage {
    private String messageText;
    private String timeStamp;
    private boolean isSentByMe; // true = Right side (Green bubble), false = Left side (Beige bubble)

    public ChatMessage(String messageText, String timeStamp, boolean isSentByMe) {
        this.messageText = messageText;
        this.timeStamp = timeStamp;
        this.isSentByMe = isSentByMe;
    }

    public String getMessageText() { return messageText; }
    public String getTimeStamp() { return timeStamp; }
    public boolean isSentByMe() { return isSentByMe; }
}