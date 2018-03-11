package com.destress.bdsman.de_stress;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int STATE_READY=1;
    private static final int STATE_FROZEN=0;
    public Bitmap capturedImage;
    public int state=STATE_READY;
    public SurfaceHolder.Callback mHolderCallback;
    public Camera.PictureCallback mCameraCallback;
    public View bottomPane1, bottomPane2;
    public int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private SurfaceView mSnapView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ImageButton mCaptureButton;
    private ImageButton mSwitchButton;
    private ImageButton mAcceptButton;
    private ImageButton mDeclineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mSnapView = findViewById(R.id.snap_view);
        mHolder = mSnapView.getHolder();
        mCaptureButton = findViewById(R.id.capture_button);
        mSwitchButton = findViewById(R.id.switch_camera_button);
        mAcceptButton = findViewById(R.id.accept_button);
        mDeclineButton = findViewById(R.id.decline_button);
        bottomPane1 = findViewById(R.id.bottom_pane1);
        bottomPane2 = findViewById(R.id.bottom_pane2);
        mCameraCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                capturedImage = getRotatedBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        };
        mHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                setupCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if(mHolder.getSurface() == null) return;
                setupCamera();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(mCamera!=null){
                    mCamera.stopPreview();
                }
            }
        };
        mHolder.setFixedSize(960,720);
        mHolder.addCallback(mHolderCallback);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCamera != null) {
                    if (state == STATE_READY) {
                        mCamera.takePicture(null, null, mCameraCallback);
                        state = STATE_FROZEN;
                        bottomPane1.setVisibility(View.GONE);
                        bottomPane2.setVisibility(View.VISIBLE);
                    } else {
                        mCamera.startPreview();
                        state = STATE_READY;
                        bottomPane2.setVisibility(View.GONE);
                        bottomPane1.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        if(checkCameraHardware()){
            setupCamera();
            mSwitchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCamera != null){
                        mCamera.stopPreview();
                        mCamera.release();
                    }
                    if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    }else{
                        currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    }
                    setupCamera();
                }
            });
        }else{
            Toast.makeText(this,"NO CAMERA DETECTED!",Toast.LENGTH_LONG).show();
        }
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(capturedImage != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    capturedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    File file = getOutputMediaFile();
                    if (file == null) return;
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(baos.toByteArray());
                        fos.close();

                        //Send to AnimationActivity
                        Intent intent = new Intent(MainActivity.this, AnimationActivity.class);
                        intent.putExtra("path",file.getAbsolutePath());
                        startActivity(intent);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
                mCamera.startPreview();
                state = STATE_READY;
                bottomPane2.setVisibility(View.GONE);
                bottomPane1.setVisibility(View.VISIBLE);
            }
            private File getOutputMediaFile(){
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "De_stress");
                if(!dir.exists()){
                    if(!dir.mkdirs()) {
                        return null;
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                return new File(dir + File.separator + "IMG_" + timeStamp + ".jpeg");
            }
        });
        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state == STATE_READY) {
                    state = STATE_FROZEN;
                    bottomPane1.setVisibility(View.GONE);
                    bottomPane2.setVisibility(View.VISIBLE);
                } else {
                    mCamera.startPreview();
                    state = STATE_READY;
                    bottomPane2.setVisibility(View.GONE);
                    bottomPane1.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void setupCamera(){
        mCamera = getCameraInstance();
        if(mCamera == null){
            Toast.makeText(this,"CAMERA UNAVAILABLE!",Toast.LENGTH_LONG).show();
        }else{
            mCamera.stopPreview();
            try {
                mCamera.setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCamera.setDisplayOrientation(90);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setExposureCompensation(0);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }
    }
    private boolean checkCameraHardware(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open(currentCameraId);
        }catch (Exception e){

        }
        return c;
    }
    private Bitmap getRotatedBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
