package com.example.farmmart;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatInboxAdapter extends RecyclerView.Adapter<ChatInboxAdapter.ViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final OnChatInboxClickListener clickListener;

    public interface OnChatInboxClickListener {
        void onUserChatClick(User user);
    }

    public ChatInboxAdapter(Context context, List<User> userList, OnChatInboxClickListener clickListener) {
        this.context = context;
        this.userList = userList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_inbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(user.name);

        // Static mock times for your layout preview
        if (position == 0) holder.tvLastSeen.setText("last seen at 1:56 AM");
        else if (position == 1) holder.tvLastSeen.setText("last seen yesterday at 3:38 PM");
        else holder.tvLastSeen.setText("last seen within a week");

        // Initials Generation (e.g. "Maria Santos" -> "MS")
        String initials = "";
        if (user.name != null && !user.name.trim().isEmpty()) {
            String[] tokens = user.name.trim().split("\\s+");
            if (tokens.length >= 2) {
                initials = "" + tokens[0].toUpperCase().charAt(0) + tokens[1].toUpperCase().charAt(0);
            } else {
                initials = "" + tokens[0].toUpperCase().charAt(0);
            }
        } else {
            initials = "U";
        }
        holder.tvAvatarText.setText(initials);

        // Uniform background branding color tint matching
        holder.layoutAvatarBg.setBackgroundTintList(
                ColorStateList.valueOf(Color.parseColor("#435334"))
        );

        // Explicit click listener mapping logic
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUserChatClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout layoutAvatarBg;
        TextView tvAvatarText, tvUserName, tvLastSeen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutAvatarBg = itemView.findViewById(R.id.layout_inbox_avatar_bg);
            tvAvatarText = itemView.findViewById(R.id.tv_inbox_avatar_text);
            tvUserName = itemView.findViewById(R.id.tv_inbox_user_name);
            tvLastSeen = itemView.findViewById(R.id.tv_inbox_last_seen);
        }
    }
}