package com.example.groupproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    List<Friend> friendList;

    public FriendAdapter(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.name.setText("Name: " + friend.name);
        holder.phone.setText("Phone: " + friend.phone);
        holder.email.setText("Email: " + friend.email);
        holder.age.setText("Age: " + friend.age);
        holder.gender.setText("Gender: " + friend.gender);
        holder.dob.setText("Birthday: " + friend.dob);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, email, age, gender, dob;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            phone = itemView.findViewById(R.id.text_phone);
            email = itemView.findViewById(R.id.text_email);
            age = itemView.findViewById(R.id.text_age);
            gender = itemView.findViewById(R.id.text_gender);
            dob = itemView.findViewById(R.id.text_dob);
        }
    }
}