package com.msc.app.cook;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserLogin extends AppCompatActivity {
    private EditText userEmail;
    private EditText userPassword;
    private Button btnLogin;
    //Pls ignore this line. Just for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        userEmail = findViewById(R.id.editEmailAddress);
        userPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.loginBtn);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (userEmail.toString().equals("test@gmail.com")
                        && userPassword.toString().equals("test")) {
                    Toast.makeText(UserLogin.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(UserLogin.this, "Login failed. Please check username and password again", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}