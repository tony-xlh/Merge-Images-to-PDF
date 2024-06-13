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

import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.license.LicenseManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "DDN";
    private static final String LICENSE = "DLS2eyJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSJ9";
    private ActivityResultLauncher<String[]> galleryActivityLauncher;
    private CaptureVisionRouter mRouter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LicenseManager.initLicense(LICENSE, this, (isSuccess, error) -> {
            if (!isSuccess) {
                Log.e(TAG, "InitLicense Error: " + error);
            }
        });
        mRouter = new CaptureVisionRouter(MainActivity.this);
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