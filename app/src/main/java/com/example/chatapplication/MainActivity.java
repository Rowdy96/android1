package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chatapplication.Models.User;
import com.example.chatapplication.services.ServiceBuilder;
import com.example.chatapplication.services.UserService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText etName;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etName.getText().toString().contains(" ")) {
                    User user = new User();
                    user.setName(etName.getText().toString());
                    if(isNetworkConnected()) {
                        UserService userService = ServiceBuilder.createService(UserService.class);
                        Call<User> createRequest = userService.AddUser(user);

                        createRequest.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                SharedPreferences sharedPreferences = getSharedPreferences(getPackageName() + ".my_prefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                String token = response.body().getToken();
                                editor.putString("Token", token);
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                                startActivity(intent);

                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Failed to Log in", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
                        startActivity(intent);
                        Toast.makeText(MainActivity.this, "this is offline mode", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Invalid Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
