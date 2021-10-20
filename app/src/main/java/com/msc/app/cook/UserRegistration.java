package com.msc.app.cook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserRegistration extends AppCompatActivity {
    private EditText InputUserName, InputUserEmail, InputUserPassword;
    private Button CreateUserBtn;
    private ProgressDialog loadingBar;

    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference root = db.getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        InputUserName = findViewById(R.id.editPersonName);
        InputUserEmail = findViewById(R.id.editEmailAddress);
        InputUserPassword = findViewById(R.id.editPassword);
        CreateUserBtn = findViewById(R.id.registerBtn);
        loadingBar = new ProgressDialog(this);

        CreateUserBtn.setOnClickListener(view -> CreateAccount());
    }

    private void CreateAccount() {
        String name = InputUserName.getText().toString();
        String email = InputUserEmail.getText().toString();
        String password = InputUserPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidaDetails(name, email, password);
        }
    }

    private void ValidaDetails(final String name, final String email, final String password) {

            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!(dataSnapshot.child("Users").child(email).exists())) {

                        HashMap<String, Object> userdataMap = new HashMap<>();
                        userdataMap.put("email", email);
                        userdataMap.put("password", password);
                        userdataMap.put("name", name);

                        RootRef.child("Users").child(email).updateChildren(userdataMap)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserRegistration.this, "Congratulations, you have successfully created your personal iChef account", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                        Intent intent = new Intent(UserRegistration.this, UserLogin.class);
                                        startActivity(intent);

                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(UserRegistration.this, "Ummmm...Seems there is a network error, please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(UserRegistration.this, email + "is already used by some user, Please register with another email.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                        Intent intent = new Intent(UserRegistration.this, UserLogin.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled( DatabaseError error) {

                }
            });
        }

    public void btn_userLoginForm(View view) {
        startActivity((new Intent(getApplicationContext(), UserLogin.class)));
    }
}
