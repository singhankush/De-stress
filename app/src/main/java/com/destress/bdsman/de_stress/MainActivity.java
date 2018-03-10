package com.destress.bdsman.de_stress;

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
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
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

    // Shake Animation for snapImage
    Animation snapImageShakeAnimation;

    //Camera to capture Snaps
    private Camera mCamera;

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

    private void safeOpenCamera(View view){
    }
}
