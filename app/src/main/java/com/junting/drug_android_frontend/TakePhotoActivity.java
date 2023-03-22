package com.junting.drug_android_frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dynamsoft.core.CoreException;
import com.dynamsoft.core.ImageData;
import com.dynamsoft.core.LicenseManager;
import com.dynamsoft.core.LicenseVerificationListener;
import com.dynamsoft.dce.CameraEnhancer;
import com.dynamsoft.dce.CameraEnhancerException;
import com.dynamsoft.dce.DCECameraView;
import com.dynamsoft.ddn.DetectResultListener;
import com.dynamsoft.ddn.DetectedQuadResult;
import com.dynamsoft.ddn.DocumentNormalizer;
import com.dynamsoft.ddn.DocumentNormalizerException;

public class TakePhotoActivity extends AppCompatActivity {

    public static DocumentNormalizer mNormalizer;
    public static ImageData mImageData;
    public static DetectedQuadResult[] mQuadResults;

    private DCECameraView mCameraView;
    private CameraEnhancer mCamera;
    private boolean ifNeedToQuadEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);

        // Initialize license for Dynamsoft Document Normalizer SDK.
        // The license string here is a time-limited trial license. Note that network connection is required for this license to work.
        // You can also request an extension for your trial license in the customer portal: https://www.dynamsoft.com/customer/license/trialLicense?product=ddn&utm_source=installer&package=android
        LicenseManager.initLicense(BuildConfig.Dynamsoft_KEY, TakePhotoActivity.this, new LicenseVerificationListener() {
            @Override
            public void licenseVerificationCallback(boolean isSuccess, CoreException error) {
                if (!isSuccess) {
                    error.printStackTrace();
                    TakePhotoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast ts = Toast.makeText(getBaseContext(), "error:"+ error.getErrorCode()+ " "+error.getMessage(), Toast.LENGTH_LONG);
                            ts.show();
                        }
                    });
                }
            }
        });

        // Add camera view for previewing video.
        mCameraView = findViewById(R.id.camera_view);

        // Create an instance of Dynamsoft Camera Enhancer for video streaming.
        mCamera = new CameraEnhancer(this);
        mCamera.setCameraView(mCameraView);

        try {
            // Create an instance of Dynamsoft Document Normalizer.
            mNormalizer = new DocumentNormalizer();
        } catch (DocumentNormalizerException e) {
            e.printStackTrace();
        }

        // Bind the Camera Enhancer instance to the Document Normalizer instance.
        mNormalizer.setImageSource(mCamera);

        // Register the detect result listener to get the detected quads from images.
        mNormalizer.setDetectResultListener(new DetectResultListener() {
            @Override
            public void detectResultCallback(int id, ImageData imageData, DetectedQuadResult[] results) {
                if (results != null && results.length > 0 && ifNeedToQuadEdit) {
                    ifNeedToQuadEdit = false;
                    imageData.bytes = imageData.bytes.clone();
                    mImageData = imageData;
                    mQuadResults = results;

                    // Start QuadEditActivity to interactively adjust bounding quads.
                    Intent intent = new Intent(TakePhotoActivity.this, QuadEditActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start video quad detecting
        try {
            mCamera.open();
        } catch (CameraEnhancerException e) {
            e.printStackTrace();
        }
        mNormalizer.startDetecting();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop video quad detecting
        try {
            mCamera.close();
        } catch (CameraEnhancerException e) {
            e.printStackTrace();
        }
        mNormalizer.stopDetecting();
    }

    public void onCaptureBtnClick(View v) {
        // Set the flag to start quad edit activity.
        ifNeedToQuadEdit = true;
    }
}