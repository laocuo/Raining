package com.laocuo.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.laocuo.raining.Rain;

public class MainActivity extends AppCompatActivity {
    private Rain mRain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRain = (Rain) findViewById(R.id.rain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRain.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRain.stop();
    }
}
