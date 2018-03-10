package com.destress.bdsman.de_stress;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView mScrollView;
    private SurfaceView mSnapView;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mScrollView = findViewById(R.id.scroll_view);
        mSnapView = findViewById(R.id.snap_view);

    }

    private void safeOpenCamera(View view){

    }
}
