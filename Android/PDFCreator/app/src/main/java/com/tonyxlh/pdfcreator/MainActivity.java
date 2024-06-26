package com.tonyxlh.pdfcreator;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dynamsoft.core.basic_structures.ImageData;
import com.dynamsoft.core.basic_structures.Quadrilateral;
import com.dynamsoft.cvr.CaptureVisionRouter;
import com.dynamsoft.cvr.CaptureVisionRouterException;
import com.dynamsoft.cvr.CapturedResult;
import com.dynamsoft.cvr.EnumPresetTemplate;
import com.dynamsoft.cvr.SimplifiedCaptureVisionSettings;
import com.dynamsoft.ddn.EnumImageColourMode;
import com.dynamsoft.ddn.NormalizedImageResultItem;
import com.dynamsoft.ddn.NormalizedImagesResult;
import com.dynamsoft.license.LicenseManager;
import com.dynamsoft.utility.ImageManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "DDN";
    private static final String LICENSE = "DLS2eyJoYW5kc2hha2VDb2RlIjoiMjAwMDAxLTE2NDk4Mjk3OTI2MzUiLCJvcmdhbml6YXRpb25JRCI6IjIwMDAwMSIsInNlc3Npb25QYXNzd29yZCI6IndTcGR6Vm05WDJrcEQ5YUoifQ==";
    private ActivityResultLauncher<String[]> galleryActivityLauncher;
    private CaptureVisionRouter mRouter;
    private TextView textView;
    private String templateName = EnumPresetTemplate.PT_NORMALIZE_DOCUMENT;
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
        RadioButton blackAndWhiteRadioButton = findViewById(R.id.blackAndWhiteRadioButton);
        RadioButton grayRadioButton = findViewById(R.id.grayRadioButton);
        RadioButton colorRadioButton = findViewById(R.id.colorRadioButton);
        CheckBox enableAutoCroppingCheckBox = findViewById(R.id.enableAutoCroppingCheckBox);
        enableAutoCroppingCheckBox.setOnCheckedChangeListener((view,checked)->{
            try {
                if (checked) {
                    templateName = EnumPresetTemplate.PT_DETECT_AND_NORMALIZE_DOCUMENT;
                }else{
                    templateName = EnumPresetTemplate.PT_NORMALIZE_DOCUMENT;
                    SimplifiedCaptureVisionSettings settings = mRouter.getSimplifiedSettings(EnumPresetTemplate.PT_NORMALIZE_DOCUMENT);
                    settings.roiMeasuredInPercentage = true;
                    settings.roi = new Quadrilateral(new Point(0,0),new Point(100,0),new Point(100,100),new Point(0,100));
                    mRouter.updateSettings(EnumPresetTemplate.PT_NORMALIZE_DOCUMENT,settings);
                }
                updateColorMode(EnumImageColourMode.ICM_BINARY);
                blackAndWhiteRadioButton.setChecked(true);
            } catch (CaptureVisionRouterException e) {
                throw new RuntimeException(e);
            }
        });
        blackAndWhiteRadioButton.setOnClickListener((view)->{
            try {
                updateColorMode(EnumImageColourMode.ICM_BINARY);
            } catch (CaptureVisionRouterException e) {
                throw new RuntimeException(e);
            }
        });
        grayRadioButton.setOnClickListener((view)->{
            try {
                updateColorMode(EnumImageColourMode.ICM_GRAYSCALE);
            } catch (CaptureVisionRouterException e) {
                throw new RuntimeException(e);
            }
        });
        colorRadioButton.setOnClickListener((view)->{
            try {
                updateColorMode(EnumImageColourMode.ICM_COLOUR);
            } catch (CaptureVisionRouterException e) {
                throw new RuntimeException(e);
            }
        });
        textView = findViewById(R.id.textView);
        selectImagesButton.setOnClickListener((view)->{
            galleryActivityLauncher.launch(new String[]{"image/*"});
        });
        galleryActivityLauncher = registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), new ActivityResultCallback<List<Uri>>() {
            @Override
            public void onActivityResult(List<Uri> results) {
                if (results != null) {
                    // perform desired operations using the result Uri
                    Log.d(TAG,"selected "+results.size()+" files");
                    textView.setText("Merging...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mergeImagesToPDF(results);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }).start();
                } else {
                    Log.d(TAG, "onActivityResult: the result is null for some reason");
                }
            }
        });
    }

    private void updateColorMode(int mode) throws CaptureVisionRouterException {
        SimplifiedCaptureVisionSettings settings = mRouter.getSimplifiedSettings(templateName);
        settings.documentSettings.colourMode = mode;
        Log.d(TAG,"color mode:"+settings.documentSettings.colourMode);
        mRouter.updateSettings(templateName,settings);
    }

    private void mergeImagesToPDF(List<Uri> results) throws Exception {
        ImageManager imageManager = new ImageManager();
        File externalFilesDir = this.getApplicationContext().getExternalFilesDir("");
        String filename = new Date().getTime()+".pdf";
        File outputFile = new File(externalFilesDir,filename);
        for (Uri result:results) {
            Log.d(TAG,result.getPath());
            InputStream inp = this.getApplicationContext().getContentResolver().openInputStream(result);
            if (inp != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inp.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                CapturedResult capturedResult = mRouter.capture(buffer.toByteArray(), templateName);
                NormalizedImagesResult normalizedImagesResult = capturedResult.getNormalizedImagesResult();
                if (normalizedImagesResult != null) {
                    NormalizedImageResultItem[] items = normalizedImagesResult.getItems();
                    if (items != null && items.length>0) {
                        ImageData imageData = items[0].getImageData();
                        imageManager.saveToFile(imageData,outputFile.getPath(),true);
                    }
                }
            }
        }
        Log.d(TAG,"run on ui thread");
        runOnUiThread(()->{
            if (outputFile.exists()) {
                textView.setText("PDF written to "+outputFile.getAbsolutePath());
            }else{
                textView.setText("Failed");
            }
        });
    }
}