package com.example.aninterface.Late;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.aninterface.R;

public class Option extends AppCompatActivity implements View.OnClickListener {
    private Animation translate_anim;
    private SwitchCompat aSwitch,cSwitch;
    private LinearLayout linearLayout;
    private Button ok;
    private SharedPreferences option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        Init();
        SetAnimation();
        InitListener();
        aSwitch.setChecked(option.getBoolean("Show_Plus_Button",false));
        cSwitch.setChecked(option.getBoolean("Stop_Notification",false));
    }

    private void InitListener() {
        ok.setOnClickListener(this);
        aSwitch.setOnClickListener(this);
        cSwitch.setOnClickListener(this);
    }

    private void SetAnimation() {
        ok.setAnimation(translate_anim);
        linearLayout.setAnimation(translate_anim);
    }

    private void Init() {
        ok = findViewById(R.id.ok_option);
        translate_anim= AnimationUtils.loadAnimation(Option.this,R.anim.transliet_anim);
        linearLayout = findViewById(R.id.main_option);
        aSwitch=findViewById(R.id.switch1);
        cSwitch=findViewById(R.id.switch3);
        translate_anim= AnimationUtils.loadAnimation(Option.this,R.anim.blink);
        option = getSharedPreferences("Option" , Context.MODE_PRIVATE);

    }


    @Override
    public void onBackPressed(){
        final Intent intent = new Intent(Option.this, Late_List.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.ok_option){
            final Intent intent = new Intent(Option.this, Late_List.class);
            startActivity(intent);
            finish();
        }
        else if(v.getId()==R.id.switch1){
            option.edit().putBoolean("Show_Plus_Button",aSwitch.isChecked()).apply();
        }
        else if(v.getId()==R.id.switch3){
            option.edit().putBoolean("Stop_Notification",cSwitch.isChecked()).apply();
        }
    }

}

