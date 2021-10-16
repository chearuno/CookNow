package com.msc.app.cook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.auth.User;

public class UserRegistration extends AppCompatActivity {
    private EditText userName;
    private EditText userEmail;
    private EditText userPassword;
    private Button btnCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        userName = findViewById(R.id.editPersonName);
        userEmail = findViewById(R.id.editEmailAddress);
        userPassword = findViewById(R.id.editPassword);
        btnCreateUser = findViewById(R.id.registerBtn);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (userName.equals("")){
                    Toast.makeText(UserRegistration.this, "Username is blank", Toast.LENGTH_SHORT).show();
                }
                if (userEmail.equals("")){
                    Toast.makeText(UserRegistration.this, "Email is blank", Toast.LENGTH_SHORT).show();
                }
                if (userPassword.equals("")){
                    Toast.makeText(UserRegistration.this, "Password is blank", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(UserRegistration.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}