package com.tonyxlh.pdfcreator;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1;

    public static final String TAG = "DDN";
    private ActivityResultLauncher<String[]> galleryActivityLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button selectImagesButton = findViewById(R.id.selectImagesButton);
        selectImagesButton.setOnClickListener((view)->{
            galleryActivityLauncher.launch(new String[]{"image/*"});
        });
        galleryActivityLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> results) {
                if (results != null) {
                    // perform desired operations using the result Uri
                    Log.d(TAG,"selected "+results.size()+" files");
                } else {
                    Log.d(TAG, "onActivityResult: the result is null for some reason");
                }
            }
        });
    }
}