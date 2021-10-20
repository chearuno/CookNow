package com.msc.app.cook;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        back = findViewById(R.id.back);


        PhotoView photoView = findViewById(R.id.photo_view);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.ic_baseline_error_24);


        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(getIntent().getStringExtra("image"))
                .into(photoView);

        back.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }
}