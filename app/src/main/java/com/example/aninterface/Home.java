package com.example.aninterface;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.aninterface.Late.Late_List;

import java.util.Objects;

public class Home extends AppCompatActivity implements View.OnClickListener {
    private CardView toLateList,toStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Init();
        InitListener();

    }

    private void InitListener() {
        toLateList.setOnClickListener(this);
        toStudentList.setOnClickListener(this);
    }

    private void Init() {
        toLateList=findViewById(R.id.to_list_late);
        toStudentList=findViewById(R.id.to_list_student);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.to_list_late){
            final Intent intent = new Intent(this, Late_List.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}