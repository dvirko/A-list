package com.example.aninterface.Late;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aninterface.R;
import com.example.aninterface.Student;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class Add extends AppCompatActivity {

    private EditText scan_phone, scan_name, scan_late;
    private String name,phone,late,time;
    private Button add;
    private Animation translate_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Init();
        SetAnimation();
        InitListener();


    }

    private void SetAnimation() {
        add.setAnimation(translate_anim);
    }

    private void InitListener() {
        add.setOnClickListener(v -> {

            name= scan_name.getText().toString();
            phone= scan_phone.getText().toString();
            late= scan_late.getText().toString();
            time = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault()).format(new Date());


            if(late.isEmpty()){
                late="0";
            }

            if(!name.isEmpty()&&phone.length()==10) {
                new SMS(Add.this,phone,late);
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Student");
                String id = myRef.push().getKey();
                myRef.child(Objects.requireNonNull(id)).setValue(new Student(id,name,phone,late,time)).addOnSuccessListener(command -> {
                    final Intent intent = new Intent(Add.this, Late_List.class);
                    startActivity(intent);
                    finish();
                });
            }else if(name.isEmpty()||phone.length()<10){
                confirmDialog();
            }



        });
    }

    private void Init() {
        add = findViewById(R.id.add);
        translate_anim = AnimationUtils.loadAnimation(this, R.anim.blink);
        scan_name = findViewById(R.id.name);
        scan_phone = findViewById(R.id.phone);
        scan_late =findViewById(R.id.late);

    }

    private  void confirmDialog(){
        if(name.isEmpty()&&phone.length()==10) {
            scan_name.setError("חייב להיות שם\nושם משפחה");
        }
        else if(phone.length()<10&&!name.isEmpty()) {
            scan_phone.setError("מספר הפלאפון איננו תקין");
        }
        else {
            scan_phone.setError("מספר הפלאפון איננו תקין");
            scan_name.setError("חייב להיות שם\nושם משפחה");
        }

    }

}