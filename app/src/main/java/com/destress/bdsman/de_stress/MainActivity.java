package com.destress.bdsman.de_stress;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Horizontal Scroll Bar for Moves/Actions
    private HorizontalScrollView mScrollView;

    //View Defined for storing image
    private SurfaceView mSnapView;
    private SurfaceHolder mHolder;

    // Shake Animation for snapImage
    Animation snapImageShakeAnimation;

    //Camera to capture Snaps
    private Camera mCamera;
    private ImageButton mCaptureButton;
    private ImageView mImageView;

    //bitmap image for splitting the image
    private Bitmap bitmap;
    private ImageView snapImage;
    //Declaration of Package Name
    //Used to retrieve Audio Uri
    public String PACKAGE_NAME;

    // Variable for Audio Player
    private MediaPlayer audioPlayer;
    private Uri audioUri;
    private int defaultAudioId;
    //End of Variable for Audio Player
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        mScrollView = findViewById(R.id.scroll_view);
        mSnapView = findViewById(R.id.snap_view);
        mHolder = mSnapView.getHolder();
        mCaptureButton = findViewById(R.id.capture_button);
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
        //Audio Player Work Started
        audioPlayer = new MediaPlayer();
        defaultAudioId = R.raw.owms; //default mp3 audio.
        audioUri = getAudioUri(defaultAudioId);
        try{
            //default audio Player file set.
            audioPlayer.setDataSource(getApplicationContext(), audioUri);
        }catch (IOException e){
            Log.d("ErrorMessage","Audio Player Unable to get Audio Uri or Context");
        }
        //Audio Player Ended

        //Start of Animation
        snapImage = (ImageView) findViewById(R.id.snap_image);
        snapImageShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);
        snapImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                snapImage.startAnimation(snapImageShakeAnimation);
            }
        });
        //End of Animation
    }

    //Converts Raw data id to Uri for Audio Player
    public Uri getAudioUri(int id){
        return Uri.parse("android.resource://" + PACKAGE_NAME + "/" + id);
    }

    private void splitImage(ImageView image, int chunkNumber){
        //For the number of rows and columns of the grid to be displayed
        //Image divided in nxn matrix where rows = cols = n
        int n;

        int defHeight, defWidth;

        //For height and width of the small image chunks
        int chunkHeight, chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        ArrayList< ArrayList<Bitmap> > chunkedImages = new ArrayList<>();

        //Getting the scaled bitmap of the source image
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

        n = (int) Math.sqrt(chunkNumber);
        defHeight = bitmap.getHeight();
        defWidth = bitmap.getWidth();
        chunkHeight = defHeight/n;
        chunkWidth = defWidth/n;

        Random r = new Random();

        //x and y are the pixel positions of the image chunks
        int y = 0;
        int rows = 0;
        while(y < defHeight){
            int randomChunkHeight = r.nextInt(chunkHeight-1) + 1;
            int x = 0;
            if((defHeight-y) < randomChunkHeight){
                randomChunkHeight = (defHeight - y);
            }
            ArrayList<Bitmap> arr_bitmaps = new ArrayList<>();

            while(x < defWidth){
                int randomChunkWidth = r.nextInt(chunkWidth-1)+1;
                if((defWidth - x) < randomChunkWidth) {
                    randomChunkWidth = defWidth - x;
                }
                arr_bitmaps.add(Bitmap.createBitmap(scaledBitmap, x, y, randomChunkWidth, randomChunkHeight));
                x += randomChunkWidth;
            }
            chunkedImages.add(arr_bitmaps);
            y += randomChunkHeight;
            rows++;
        }

       // showImage(chunkedImages, defHeight, defWidth);

    }

    /*
    private void showImage(ArrayList< ArrayList<Bitmap> > chunkedImages, int height, int width){
        GridView grid = (GridView) findViewById(R.id.gridview);
        for(int i=0; i<chunkedImages.size(); i++){
            ArrayList<Bitmap> row_images = chunkedImages.get(i);
            grid.setAdapter(new ImageAdapter(this, row_images, height, width));
        }
    }*/

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
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) return true;
        return false;
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try{
            c = Camera.open();
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
