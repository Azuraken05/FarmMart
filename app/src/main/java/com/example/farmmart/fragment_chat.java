package com.example.farmmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class fragment_chat extends Fragment {

    private RecyclerView rvChatInboxList;
    private AppDatabase db;
    private List<User> appUsersList;
    private ChatInboxAdapter inboxAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChatInboxList = view.findViewById(R.id.rv_chat_inbox_list);
        if (rvChatInboxList != null) {
            rvChatInboxList.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        db = AppDatabase.getInstance(getContext());
        appUsersList = new ArrayList<>();

        loadInboxUsers();

        return view;
    }

    private void loadInboxUsers() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        new Thread(() -> {
            List<User> rawUsers = db.userDao().getAllUsers();
            List<User> filteredInboxUsers = new ArrayList<>();

            if (rawUsers != null) {
                for (User u : rawUsers) {
                    if (u.id != currentUserId) {
                        filteredInboxUsers.add(u);
                    }
                }
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    appUsersList.clear();
                    appUsersList.addAll(filteredInboxUsers);

                    inboxAdapter = new ChatInboxAdapter(getContext(), appUsersList, new ChatInboxAdapter.OnChatInboxClickListener() {
                        @Override
                        public void onUserChatClick(User selectedUser) {
                            Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
                            intent.putExtra("SELECTED_USER_ID", selectedUser.id);
                            intent.putExtra("SELECTED_USER_NAME", selectedUser.name);
                            startActivity(intent);
                        }
                    });

                    if (rvChatInboxList != null) {
                        rvChatInboxList.setAdapter(inboxAdapter);
                    }
                });
            }
        }).start();
    }
}