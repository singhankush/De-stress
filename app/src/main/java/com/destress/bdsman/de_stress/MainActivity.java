package com.destress.bdsman.de_stress;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView mScrollView;
    private SurfaceView mSnapView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ImageButton mCaptureButton;
    private ImageButton mSwtichButton;
    public View bottomPane1, bottomPane2;
    public int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mScrollView = findViewById(R.id.scroll_view);
        mSnapView = findViewById(R.id.snap_view);
        mHolder = mSnapView.getHolder();
        mCaptureButton = findViewById(R.id.capture_button);
        mSwtichButton = findViewById(R.id.switch_camera_button);
        bottomPane1 = findViewById(R.id.)
        final Camera.PictureCallback callback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Bitmap bitmap = getRotatedBitmap(BitmapFactory.decodeByteArray(bytes, 0 ,bytes.length));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,0,baos);
                File file = getOutputMediaFile();
                if(file == null) return;
                try{
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(baos.toByteArray());
                    fos.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
            private File getOutputMediaFile(){
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "De_stress");
                if(!dir.exists()){
                    if(!dir.mkdirs()) {
                        return null;
                    }
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                return new File(dir + File.separator + "IMG_" + timeStamp + ".png");
            }
        };
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    mCamera.setPreviewDisplay(surfaceHolder);
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if(mHolder.getSurface() == null) return;
                try {
                    mCamera.stopPreview();
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.setDisplayOrientation(90);
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                if(mCamera!=null){
                    mCamera.stopPreview();
                }
            }
        });
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            private static final int STATE_READY=1;
            private static final int STATE_FROZEN=0;
            public int state = STATE_READY;

            @Override
            public void onClick(View view) {
                if(mCamera != null) {
                    if (state == STATE_READY) {
                        mCamera.takePicture(null, null, callback);
                        state = STATE_FROZEN;

                    } else {
                        mCamera.startPreview();
                        state = STATE_READY;
                    }
                }
            }
        });

        if(checkCameraHardware()){
            mCamera = getCameraInstance();
            setupCamera();
            mSwtichButton.setOnClickListener(new View.OnClickListener() {
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
                    mCamera = getCameraInstance();
                    setupCamera();
                }
            });
        }else{
            Toast.makeText(this,"NO CAMERA DETECTED!",Toast.LENGTH_LONG).show();
        }
    }
    private void setupCamera(){
        if(mCamera == null){
            Toast.makeText(this,"CAMERA UNAVAILABLE!",Toast.LENGTH_LONG).show();
        }else{
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(parameters);
        }
    }
    private boolean checkCameraHardware(){
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) return true;
        return false;
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
