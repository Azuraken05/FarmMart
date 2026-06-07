package com.example.farmmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private List<ChatMessage> messageList;

    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_bubble, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (message.isSentByMe()) {
            holder.layoutSent.setVisibility(View.VISIBLE);
            holder.layoutReceived.setVisibility(View.GONE);
            holder.tvSentText.setText(message.getMessageText());
            holder.tvSentTime.setText(message.getTimeStamp());
        } else {
            holder.layoutReceived.setVisibility(View.VISIBLE);
            holder.layoutSent.setVisibility(View.GONE);
            holder.tvReceivedText.setText(message.getMessageText());
            holder.tvReceivedTime.setText(message.getTimeStamp());
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutReceived, layoutSent;
        TextView tvReceivedText, tvReceivedTime, tvSentText, tvSentTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutReceived = itemView.findViewById(R.id.layout_chat_received);
            layoutSent = itemView.findViewById(R.id.layout_chat_sent);
            tvReceivedText = itemView.findViewById(R.id.tv_chat_received_text);
            tvReceivedTime = itemView.findViewById(R.id.tv_chat_received_time);
            tvSentText = itemView.findViewById(R.id.tv_chat_sent_text);
            tvSentTime = itemView.findViewById(R.id.tv_chat_sent_time);
        }
    }
}