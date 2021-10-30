package com.msc.app.cook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserLogin extends AppCompatActivity {
    private EditText userEmail, userPassword;
    private Button btnLogin;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        userEmail = findViewById(R.id.editEmailAddress);
        userPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.loginBtn);
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener((view) ->
                db.collection("User").whereEqualTo("email", userEmail.getText().toString()).whereEqualTo("password", userPassword.getText().toString())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                if (task.getResult() == null || task.getResult().isEmpty()) {
                                    Toast.makeText(UserLogin.this, "User not found", Toast.LENGTH_LONG).show();
                                } else {
                                    QuerySnapshot querySnapshot = task.getResult();
                                    Map<String, Object> document = querySnapshot.getDocuments().get(0).getData();

                                    SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", 0).edit();

                                    editor.putString("loggedUserFirstName", document.get("firstName").toString());
                                    editor.putString("loggedUserLastName", document.get("lastName").toString());
                                    editor.putString("loggedUserEmail", document.get("email").toString());
                                    editor.putString("loggedUserId", document.get("id").toString());
                                    editor.putString("fav_set",  document.get("favList").toString());


                                    Intent intent = new Intent(UserLogin.this, MainActivity.class);
                                    finish();
                                    startActivity(intent);
                                    editor.apply();

                                }
                            }
                        }));
    }

    public void btn_userRegForm(View view) {
        startActivity(new Intent(getApplicationContext(), UserRegistration.class));
    }
}
