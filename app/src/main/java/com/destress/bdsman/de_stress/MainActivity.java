package com.destress.bdsman.de_stress;

import android.media.MediaPlayer;
import android.net.Uri;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView mScrollView;
    private SurfaceView mSnapView;
    private Camera mCamera;

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
            audioPlayer.setDataSource(getApplicationContext(), audioUri);
        }catch (IOException e){
            Log.d("ErrorMessage","Audio Player Unable to get Audio Uri or Context");
        }
        //Audio Player Ended
    }

    //Converts Raw data id to Uri for Audio Player
    public Uri getAudioUri(int id){
        return Uri.parse("android.resource://" + PACKAGE_NAME + "/" + id);
    }

    private void safeOpenCamera(View view){
    }
}
