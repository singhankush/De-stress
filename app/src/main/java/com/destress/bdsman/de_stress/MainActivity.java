package com.destress.bdsman.de_stress;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public int currentCameraId;
    private HorizontalScrollView mScrollView;
    private SurfaceView mSnapView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private ImageButton mCaptureButton;
    private ImageButton mSwtichButton;

    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
        }catch (Exception e){

        }
        return c;
    }

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
//        mImageView = findViewById(R.id.image_view);

        if(checkCameraHardware()){
            mCamera = getCameraInstance();
            if(mCamera == null){
                Toast.makeText(this,"CAMERA UNAVAILABLE!",Toast.LENGTH_LONG).show();
            }else{
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCamera.setParameters(parameters);
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
                        if(state == STATE_READY) {
                            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                                @Override
                                public void onPictureTaken(byte[] bytes, Camera camera) {
                                Bitmap bitmap = getRotatedBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
//                                mImageView.setImageBitmap(bitmap);
//                                mImageView.setVisibility(View.VISIBLE);
//                                mImageView.invalidate();
                                }
                            });
                            state = STATE_FROZEN;
                        }else{
                            mCamera.startPreview();
                            state = STATE_READY;
                        }
                    }
                });

            }
        }else{
            Toast.makeText(this,"NO CAMERA DETECTED!",Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkCameraHardware(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
