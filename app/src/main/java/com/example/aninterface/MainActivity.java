package com.example.aninterface;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private  TextView text;
    private Animation animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Init();
        setAnimation();
        long SPLASH_TIME_OUT = animation.getDuration();
        new Handler().postDelayed(this::StartIntent, SPLASH_TIME_OUT);

    }

    private void setAnimation() {
        text.setAnimation(animation);
    }

    private void Init() {
        text=findViewById(R.id.intro);
        animation= AnimationUtils.loadAnimation(this,R.anim.transliet_anim);
    }

    private void StartIntent() {
        final Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}