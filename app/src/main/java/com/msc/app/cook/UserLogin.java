package com.msc.app.cook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLogin extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private Button btnLogin;
    private DatabaseReference ref;
    public static final String MY_PREFS_NAME = "UserLogin";
    boolean userFound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String name = prefs.getString("userEmail", "");
        if (!name.equals("")) {

            String isVendorLogged = prefs.getString("userIsVender", "");
            if (isVendorLogged.equals("")) {
                startActivity(new Intent(UserLogin.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(UserLogin.this, BaseActivity.class));
                finish();
            }
        }

        userEmail = findViewById(R.id.editEmailAddress);
        userPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.loginBtn);
        ref = FirebaseDatabase.getInstance().getReference();

        btnLogin.setOnClickListener((view) -> {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot datas : dataSnapshot.getChildren()) {

                        final Users userNew = datas.getValue(Users.class);
                        String emailText = userNew.getEmail();

                        if (emailText.equals(userEmail.getText().toString().trim())) {

                            userFound = true;
                            String password = userNew.getPassword();
                            String vender = userNew.getAdmin();

                            if (password.equals(userPassword.getText().toString().trim())) {

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("loggedUserName", userNew.getName());
                                editor.putString("loggedUserEmail", userNew.getEmail());

                                Log.e("TAG", dataSnapshot.getKey());

                                if (vender == null) {

                                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                    editor.apply();

                                } else {


                                    editor.putString("userIsVender", "YES");

                                    Intent intent = new Intent(UserLogin.this, BaseActivity.class);
                                    finish();
                                    startActivity(intent);
                                    editor.apply();

                                }

                            } else {
                                Toast.makeText(UserLogin.this, "Password is wrong", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    if (!userFound) {
                        Toast.makeText(UserLogin.this, "User not found", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(UserLogin.this, "No internet found", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    public void btn_userRegForm(View view) {
        startActivity(new Intent(getApplicationContext(), UserRegistration.class));
    }
}
