package com.destress.bdsman.de_stress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.HorizontalScrollView;

public class MainActivity extends AppCompatActivity {

    private HorizontalScrollView mListView;
    private View mSnapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = findViewById(R.id.list);

    }
}
