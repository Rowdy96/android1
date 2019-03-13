package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Models.User;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<User> mUsers;


    public UserAdapter(ArrayList<User> users) {
        mUsers= users;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Context context;
        public TextView nameTextView;
        public ViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            itemView.setOnClickListener(this);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                User user = mUsers.get(position);
                Gson gson = new Gson();
                String userDetails = gson.toJson(user);
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("userDetails",userDetails);
                context.startActivity(intent);

            }

        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.user_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(contactView,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = mUsers.get(i);
        TextView textView = viewHolder.nameTextView;
        textView.setText(user.getName());

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


}
