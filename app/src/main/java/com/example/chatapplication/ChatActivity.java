package com.example.chatapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapplication.Models.User;
import com.example.chatapplication.Models.messages;
import com.example.chatapplication.services.ServiceBuilder;
import com.example.chatapplication.services.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    TextView userName;
    ListView messageView;
    EditText chatbox;
    Button button;
    ArrayAdapter<String> adapter;
    String chat;
    ArrayList<messages> messageList = new ArrayList<>();
    Gson gson = new Gson();
    String msgList;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userName = (TextView) findViewById(R.id.userName);
        messageView = (ListView) findViewById(R.id.message);
        chatbox = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.btnsend);

        final User user = gson.fromJson(getIntent().getStringExtra("userDetails"),User.class);

        final int id = user.getId();
        userName.setText(user.getName());

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName()+".my_prefs", Context.MODE_PRIVATE);
        final String token  = sharedPreferences.getString("Token","N/A");
        final messages message=new messages();

        Messages(token,id);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat = chatbox.getText().toString();
                message.setMessage(chat);
                message.setToUserId(id);
                if(isNetworkConnected()) {

                    UserService userService = ServiceBuilder.createService(UserService.class);
                    Call<Void> sendMessage = userService.SendMessage(token, message);
                    sendMessage.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            Messages(token, id);
                            Toast.makeText(ChatActivity.this, "sent to " + user.getName(), Toast.LENGTH_SHORT).show();
                            chatbox.setText(" ");
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                            Toast.makeText(ChatActivity.this, "Failed to send", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{

                    Messages(token, id);
                    Toast.makeText(ChatActivity.this, "OffLine Mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

       // doTheAutoRefresh(token,id);

    }
    void Messages(String token , final int id)
    {
        final SharedPreferences userMsgPrefs = getSharedPreferences(getPackageName()+".userMessage",Context.MODE_PRIVATE);

        if(isNetworkConnected()) {
            final UserService userService = ServiceBuilder.createService(UserService.class);
            Call<ArrayList<messages>> createRequest = userService.GetMessages(token, id);
            createRequest.enqueue(new Callback<ArrayList<messages>>() {
                @Override
                public void onResponse(Call<ArrayList<messages>> call, Response<ArrayList<messages>> response) {


                    messageList = response.body();
                    String[] msg = new String[messageList.size()];
                    msgList = gson.toJson(messageList);
                    for (int i = 0; i < messageList.size(); i++) {
                        msg[i] = messageList.get(i).getMessage();
                    }

                    userMsgPrefs.edit().putString(String.valueOf(id), msgList).apply();

                    adapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_1, msg);
                    messageView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(Call<ArrayList<messages>> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            String jsonMsg = userMsgPrefs.getString(String.valueOf(id), null);
            if (jsonMsg == null) {
                Toast.makeText(ChatActivity.this, "No message.", Toast.LENGTH_SHORT).show();
            } else {
                Type type = new TypeToken<ArrayList<messages>>() {
                }.getType();
                ArrayList<messages> fetchMsg = gson.fromJson(jsonMsg, type);
                String[] msg = new String[fetchMsg.size()];
                for (int i = 0; i < fetchMsg.size(); i++) {
                    msg[i] = fetchMsg.get(i).getMessage();
                }
                adapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_1, msg);
                messageView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Toast.makeText(ChatActivity.this, "Go online to send message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void doTheAutoRefresh(final String token,final int id) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Messages(token,id);
                doTheAutoRefresh(token,id);
            }
        }, 100);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
