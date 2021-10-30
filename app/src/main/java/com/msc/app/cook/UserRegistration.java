package com.msc.app.cook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import es.dmoral.toasty.Toasty;

public class UserRegistration extends AppCompatActivity {
    private EditText InputUserName, InputUserEmail, InputUserPassword;
    private Button CreateUserBtn;
    private ProgressDialog loadingBar;
    private FirebaseFirestore db;

    public ImageView pick;
    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 101;
    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pick = findViewById(R.id.profpic);

        db = FirebaseFirestore.getInstance();

        pick.setOnClickListener(v -> {
            int picd = 0;
            if (picd == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromGallery();
                }
            } else if (picd == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });

        InputUserName = findViewById(R.id.editPersonName);
        InputUserEmail = findViewById(R.id.editEmailAddress);
        InputUserPassword = findViewById(R.id.editPassword);
        CreateUserBtn = findViewById(R.id.registerBtn);
        loadingBar = new ProgressDialog(this);

        CreateUserBtn.setOnClickListener(view -> CreateAccount());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void pickFromGallery() {
        CropImage.activity().start(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);

    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Glide.with(this)
                        .load(resultUri)
                        .into(pick);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                    boolean storage_accepted = grantResults[1] == (PackageManager.PERMISSION_GRANTED);
                    if (camera_accepted && storage_accepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable camera and storage permissions", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean storage_accepted = grantResults[0] == (PackageManager.PERMISSION_GRANTED);
                    if (storage_accepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
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


        db.collection("User").whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        if (task.getResult() == null || task.getResult().isEmpty()) {

                            int lowerLimit = 100000;
                            int upperLimit = 999999;
                            Random r = new Random();
                            int documentId = lowerLimit + (r.nextInt() * (upperLimit - lowerLimit));
                            ArrayList<String> favList = new ArrayList();
                            String docIdSting = String.valueOf(documentId);

                            HashMap<String, Object> userdataMap = new HashMap<>();
                            userdataMap.put("email", email);
                            userdataMap.put("password", password);
                            userdataMap.put("firstName", name);
                            userdataMap.put("lastName", "");
                            userdataMap.put("id", documentId);
                            userdataMap.put("favourites", favList);

                            db.collection("User").document(docIdSting)
                                    .set(userdataMap)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(UserRegistration.this, "Congratulations, you have successfully created your personal iChef account", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                            SharedPreferences.Editor editor = getSharedPreferences("MyPrefsFile", 0).edit();
                                            editor.putString("loggedUserFirstName", name);
                                            editor.putString("loggedUserLastName", "");
                                            editor.putString("loggedUserEmail", email);
                                            editor.putString("loggedUserId", docIdSting);
                                            editor.putString("fav_set",  "");

                                            Intent intent = new Intent(UserRegistration.this, MainActivity.class);
                                            startActivity(intent);

                                        } else {
                                            loadingBar.dismiss();
                                            Toast.makeText(UserRegistration.this, "Ummmm...Seems there is a network error, please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> toastError("Data writing Failed", UserRegistration.this));
                        } else {
                            Toast.makeText(UserRegistration.this, email + "is already used by some user, Please register with another email.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(UserRegistration.this, UserLogin.class);
                            startActivity(intent);
                        }

                    } else {
                        Log.d("Doc", "Error getting documents: ", task.getException());
                        toastError("Error getting documents", UserRegistration.this);
                    }
                });
    }


    void toastError(String title, Context context) {
        Toasty.error(context, title, Toast.LENGTH_LONG, true).show();
    }

    public void btn_userLoginForm(View view) {
        startActivity((new Intent(getApplicationContext(), UserLogin.class)));
    }
}
