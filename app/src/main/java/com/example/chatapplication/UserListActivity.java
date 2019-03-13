package com.example.chatapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Models.User;
import com.example.chatapplication.services.ServiceBuilder;
import com.example.chatapplication.services.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<User> userList;
    Gson gson = new Gson();
    String users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = (RecyclerView) findViewById(R.id.rclListId);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+".my_prefs", Context.MODE_PRIVATE);
        final String token  = sharedPreferences.getString("Token","N/A");

        final SharedPreferences userListPrefs = getSharedPreferences(getPackageName() + ".user_List", Context.MODE_PRIVATE);
        final SharedPreferences.Editor userListEditor = userListPrefs.edit();
        if(isNetworkConnected()) {
            UserService userService = ServiceBuilder.createService(UserService.class);
            Call<ArrayList<User>> createRequest = userService.GetUsers(token);
            createRequest.enqueue(new Callback<ArrayList<User>>() {
                @Override
                public void onResponse(retrofit2.Call<ArrayList<User>> call, Response<ArrayList<User>> response) {

                    Toast.makeText(UserListActivity.this, token, Toast.LENGTH_SHORT).show();
                    userList = response.body();
                    adapter = new UserAdapter(userList);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
                    recyclerView.setAdapter(adapter);

                    users = gson.toJson(userList);

                    userListEditor.putString("List", users);
                    userListEditor.commit();


                }

                @Override
                public void onFailure(retrofit2.Call<ArrayList<User>> call, Throwable t) {

                    Toast.makeText(UserListActivity.this, "Failed!!", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            String json = userListPrefs.getString("List", null);
            Type type = new TypeToken<ArrayList<User>>() {
            }.getType();
            ArrayList<User> fetchUserList = gson.fromJson(json, type);
            adapter = new UserAdapter(fetchUserList);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(adapter);
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
