package com.destress.bdsman.de_stress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;


/**
 * Created by vipul on 10-03-2018.
 */

public class AnimationActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaration of Package Name
    //Used to retrieve Audio Uri
    public String PACKAGE_NAME;
    //Health
    public int health = 100;
    // Shake Animation for snapImage
    Animation snapImageShakeAnimation;
    // Horizontal Scroll Bar for Moves/Actions
    private HorizontalScrollView mScrollView;
    private ImageView mImageView;
    private ProgressBar mHealthBar;
    //bitmap image for splitting the image
    private Bitmap bitmap;
    private ImageView snapImage;
    // Variable for Audio Player
    private MediaPlayer audioPlayer;
    private Uri audioUri;
    private int defaultAudioId;
    //End of Variable for Audio Player

    //Moves Variables
    private FloatingActionButton firstMove,
            secondMove, thirdMove,
            fourthMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_animation);
        mScrollView = findViewById(R.id.scroll_view);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        //Progress Bar added
        mHealthBar  = findViewById(R.id.health_bar);
        mHealthBar.setProgress(health);

        //Renew button
        ImageButton renewButton = findViewById(R.id.renew_button);
        renewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                health = 100;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mHealthBar.setProgress(health,true);
                }else{
                    mHealthBar.setProgress(health);
                }
            }
        });

        //Photo added
        snapImage = findViewById(R.id.snap_image);
        String absPath = getIntent().getStringExtra("path");
        File file = new File(absPath);
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(absPath);
            snapImage.setImageBitmap(bitmap);
        }

        //Audio Player Work Started
        audioPlayer = new MediaPlayer();
        defaultAudioId = R.raw.owms; //default mp3 audio.
        audioUri = convertIdToUri(defaultAudioId);
        try {
            //default audio Player file set.
            audioPlayer.setDataSource(getApplicationContext(), audioUri);
            audioPlayer.prepare();
        } catch (IOException e) {
            Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
        }
        //Audio Player Ended

        //Start of Animation Default Declaration
        snapImageShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        firstMove = findViewById(R.id.button1);
        secondMove = findViewById(R.id.button2);
        thirdMove = findViewById(R.id.button3);
        fourthMove = findViewById(R.id.button4);

        firstMove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                findViewById(R.id.crack).setVisibility(View.INVISIBLE);
                final VideoView videoview = findViewById(R.id.move_video);
                final ImageView imageView = findViewById(R.id.common_video_background);
                final GifImageView gifView = findViewById(R.id.gif_view);
                videoview.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Uri uri = convertIdToUri(R.raw.scene_explosion);
                videoview.setVideoURI(uri);
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        videoview.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        snapImage.setVisibility(View.VISIBLE);
                        gifView.setVisibility(View.VISIBLE);
                        snapImage.startAnimation(snapImageShakeAnimation);
                        gifView.setImageResource(R.drawable.effect_explosion);
                        int audioId = R.raw.bgm_attack; //default mp3 audio.
                        audioUri = convertIdToUri(audioId);
                        try {
                            //default audio Player file set.
                            try {
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            } catch (IllegalStateException e) {
                                audioPlayer.reset();
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            }
                            audioPlayer.prepare();
                        } catch (IOException e) {
                            Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                        }
                        audioPlayer.start();
                        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                gifView.setVisibility(View.INVISIBLE);
                                crack();
                            }
                        });
                        // crack();
                        int damage = (int) (Math.round(20*Math.random())+40);
                        health = Math.max(0, health - damage);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mHealthBar.setProgress(health, true);
                        }else{
                            mHealthBar.setProgress(health);
                        }

                        if(health <= 0){
                            try {
                            //default audio Player file set.
                                audioPlayer.stop();
                                try {
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                } catch (IllegalStateException e) {
                                    audioPlayer.reset();
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                }
                                audioPlayer.prepare();
                            } catch (IOException e) {
                                Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                            }
                                audioPlayer.start();
                            }
                    }
                });
                ResizeAnimation resizeAnimation = new ResizeAnimation(
                        videoview, 800, 200, 1000, 500
                );
                ResizeAnimation resizeAnimationBlack = new ResizeAnimation(
                        imageView, 1500, 800, 2000, 2000
                );
                resizeAnimation.setDuration(10000);
                resizeAnimationBlack.setDuration(10000);
                videoview.startAnimation(resizeAnimation);
                imageView.startAnimation(resizeAnimationBlack);
                videoview.start();
                snapImage.setVisibility(View.INVISIBLE);
            }
        });

        secondMove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){findViewById(R.id.crack).setVisibility(View.INVISIBLE);
                final VideoView videoview = findViewById(R.id.move_video);
                final ImageView imageView = findViewById(R.id.common_video_background);
                final GifImageView gifView = findViewById(R.id.gif_view);
                videoview.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Uri uri = convertIdToUri(R.raw.scene_kamehameha);
                videoview.setVideoURI(uri);
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        videoview.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        snapImage.setVisibility(View.VISIBLE);
                        gifView.setVisibility(View.VISIBLE);
                        snapImage.startAnimation(snapImageShakeAnimation);
                        gifView.setImageResource(R.drawable.effect_blue_fire);
                        int audioId = R.raw.bgm_kamehameha; //default mp3 audio.
                        audioUri = convertIdToUri(audioId);
                        try {
                            //default audio Player file set.
                            audioPlayer.stop();
                            try {
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            } catch (IllegalStateException e) {
                                audioPlayer.reset();
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            }
                            audioPlayer.prepare();
                        } catch (IOException e) {
                            Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                        }
                        audioPlayer.start();
                        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                gifView.setVisibility(View.INVISIBLE);
                                crack();
                            }
                        });
                        // crack();
                        int damage = (int) (Math.round(20*Math.random())+40);
                        health = Math.max(0, health - damage);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mHealthBar.setProgress(health, true);
                        }else{
                            mHealthBar.setProgress(health);
                        }

                        if(health <= 0){
                            try {
                                //default audio Player file set.
                                audioPlayer.stop();
                                try {
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                } catch (IllegalStateException e) {
                                    audioPlayer.reset();
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                }
                                audioPlayer.prepare();
                            } catch (IOException e) {
                                Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                            }
                            audioPlayer.start();
                        }
                    }
                });
                ResizeAnimation resizeAnimation = new ResizeAnimation(
                        videoview, 800, 200, 1000, 500
                );
                ResizeAnimation resizeAnimationBlack = new ResizeAnimation(
                        imageView, 1500, 800, 2000, 2000
                );
                resizeAnimation.setDuration(10000);
                resizeAnimationBlack.setDuration(10000);
                videoview.startAnimation(resizeAnimation);
                imageView.startAnimation(resizeAnimationBlack);
                videoview.start();
                snapImage.setVisibility(View.INVISIBLE);
            }
        });

        thirdMove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){findViewById(R.id.crack).setVisibility(View.INVISIBLE);
                final VideoView videoview = findViewById(R.id.move_video);
                final ImageView imageView = findViewById(R.id.common_video_background);
                final GifImageView gifView = findViewById(R.id.gif_view);
                videoview.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Uri uri = convertIdToUri(R.raw.scene_cero);
                videoview.setVideoURI(uri);
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        videoview.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        snapImage.setVisibility(View.VISIBLE);
                        gifView.setVisibility(View.VISIBLE);
                        snapImage.startAnimation(snapImageShakeAnimation);
                        gifView.setImageResource(R.drawable.effect_fire);
                        int audioId = R.raw.bgm_cero; //default mp3 audio.
                        audioUri = convertIdToUri(audioId);
                        try {
                            //default audio Player file set.
                            audioPlayer.stop();
                            try {
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            } catch (IllegalStateException e) {
                                audioPlayer.reset();
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            }
                            audioPlayer.prepare();
                        } catch (IOException e) {
                            Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                        }
                        audioPlayer.start();
                        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                gifView.setVisibility(View.INVISIBLE);
                                crack();
                            }
                        });
                        // crack();
                        int damage = (int) (Math.round(20*Math.random())+40);
                        health = Math.max(0, health - damage);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mHealthBar.setProgress(health, true);
                        }else{
                            mHealthBar.setProgress(health);
                        }

                        if(health <= 0){
                            try {
                                //default audio Player file set.
                                try {
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                } catch (IllegalStateException e) {
                                    audioPlayer.reset();
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                }
                                audioPlayer.prepare();
                            } catch (IOException e) {
                                Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                            }
                            audioPlayer.start();
                        }
                    }
                });
                ResizeAnimation resizeAnimation = new ResizeAnimation(
                        videoview, 800, 200, 1000, 500
                );
                ResizeAnimation resizeAnimationBlack = new ResizeAnimation(
                        imageView, 1500, 800, 2000, 2000
                );
                resizeAnimation.setDuration(10000);
                resizeAnimationBlack.setDuration(10000);
                videoview.startAnimation(resizeAnimation);
                imageView.startAnimation(resizeAnimationBlack);
                videoview.start();
                snapImage.setVisibility(View.INVISIBLE);
            }
        });

        fourthMove.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){findViewById(R.id.crack).setVisibility(View.INVISIBLE);
                final VideoView videoview = findViewById(R.id.move_video);
                final ImageView imageView = findViewById(R.id.common_video_background);
                final GifImageView gifView = findViewById(R.id.gif_view);
                videoview.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                Uri uri = convertIdToUri(R.raw.scene_onepunch);
                videoview.setVideoURI(uri);
                videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {

                        videoview.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.INVISIBLE);
                        snapImage.setVisibility(View.VISIBLE);
                        gifView.setVisibility(View.VISIBLE);
                        snapImage.startAnimation(snapImageShakeAnimation);
                        gifView.setImageResource(R.drawable.effect_punch);
                        int audioId = R.raw.bgm_onepunch; //default mp3 audio.
                        audioUri = convertIdToUri(audioId);
                        try {
                            //default audio Player file set.
                            try {
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            } catch (IllegalStateException e) {
                                audioPlayer.reset();
                                audioPlayer.setDataSource(getApplicationContext(), audioUri);
                            }
                            audioPlayer.prepare();
                        } catch (IOException e) {
                            Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                        }
                        audioPlayer.start();
                        audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                gifView.setVisibility(View.INVISIBLE);
                                crack();
                            }
                        });
                        // crack();
                        int damage = (int) (Math.round(20*Math.random())+40);
                        health = Math.max(0, health - damage);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mHealthBar.setProgress(health, true);
                        }else{
                            mHealthBar.setProgress(health);
                        }

                        if(health <= 0){
                            try {
                                //default audio Player file set.
                                try {
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                } catch (IllegalStateException e) {
                                    audioPlayer.reset();
                                    audioPlayer.setDataSource(getApplicationContext(), audioUri);
                                }
                                audioPlayer.prepare();
                            } catch (IOException e) {
                                Log.d("ErrorMessage", "Audio Player Unable to get Audio Uri or Context");
                            }
                            audioPlayer.start();
                        }
                    }
                });
                ResizeAnimation resizeAnimation = new ResizeAnimation(
                        videoview, 800, 200, 1000, 500
                );
                ResizeAnimation resizeAnimationBlack = new ResizeAnimation(
                        imageView, 1500, 800, 2000, 2000
                );
                resizeAnimation.setDuration(10000);
                resizeAnimationBlack.setDuration(10000);
                videoview.startAnimation(resizeAnimation);
                imageView.startAnimation(resizeAnimationBlack);
                videoview.start();
                snapImage.setVisibility(View.INVISIBLE);
            }
        });
        //End of Animation Default Declaration
    }

    //Converts Raw data id to Uri for Audio Player
    public Uri convertIdToUri(int id){
        return Uri.parse("android.resource://" + PACKAGE_NAME + "/" + id);
    }

    //Split Image For Animation start
    //For future Use
    private void splitImage(ImageView image, int chunkNumber){
        //For the number of rows and columns of the grid to be displayed
        //Image divided in nxn matrix where rows = cols = n
        int n;

        int defHeight, defWidth;

        //For height and width of the small image chunks
        int chunkHeight, chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        ArrayList<ArrayList<Bitmap>> chunkedImages = new ArrayList<>();

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

    //Split Image for Animation Feature End

    //OnClick Listener starts
    @Override
    public void onClick(View v) {
        //If our applications' button is clicked
    }
    //OnClick Listener Ends

    // Crack Screen Animation function Start
    private void crack(){
        // Overlays Crack Background.
        visualFX();
        //Vibrates Mobile
        vibrateFX();
    }

    private void visualFX(){
        //Set background with broken glass image
        findViewById(R.id.crack).setVisibility(View.VISIBLE);
    }
    private void vibrateFX(){
        //Create vibrator object
        Vibrator mv = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        try{
            //Vibrate for 500 MS
            mv.vibrate(new long[]{ 0, 500, 0 }, -1);
        }catch (NullPointerException e){
            Log.d("Error", "Vibrate Error Occurred");
        }
    }
    //Crack Screen Animation function End

    protected void onDestroy() {
        super.onDestroy();
        // Release mediaplayer
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
    }
}
