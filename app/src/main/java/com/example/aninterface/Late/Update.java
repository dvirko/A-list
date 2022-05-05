package com.example.aninterface.Late;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aninterface.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class Update extends AppCompatActivity implements View.OnClickListener {
    private EditText scan_name, scan_phone, scan_late;
    private String name,phone,id,late,time;
    private Button delete, update;
    private Animation translate_anim_button;
    private View dialog_view;
    private CheckBox checkBox;
    private SharedPreferences remember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Init();
        SetAnimation();
        getIntentData();

        ActionBar ab=getSupportActionBar();
        if(ab !=null) {
            ab.setTitle(name);
        }

        InitListener();

    }

    private void SetAnimation() {
        update.setAnimation(translate_anim_button);
        delete.setAnimation(translate_anim_button);
    }

    private void InitListener() {
        update.setOnClickListener(this);
        delete.setOnClickListener(this);
        checkBox.setOnClickListener(this);
    }

    private void Init() {
        scan_name =findViewById(R.id.scanname_update);
        scan_phone =findViewById(R.id.scanphone_update);
        scan_late =findViewById(R.id.late_update);
        delete = findViewById(R.id.delete);
        update = findViewById(R.id.update);
        translate_anim_button = AnimationUtils.loadAnimation(this, R.anim.blink);
        dialog_view = View.inflate(this, R.layout.dialog_show_again, null);
        checkBox =  dialog_view.findViewById(R.id.checkbox);
        remember = getSharedPreferences("Remember" , Context.MODE_PRIVATE);
    }

    private void getIntentData(){
        if(getIntent().hasExtra("id_n") && getIntent().hasExtra("name_n")  && getIntent().hasExtra("phone_n")&&getIntent().hasExtra("late_n")&&getIntent().hasExtra("date_n") ){
            //Get
            id=getIntent().getStringExtra("id_n");
            name=getIntent().getStringExtra("name_n");
            phone=getIntent().getStringExtra("phone_n");
            late=getIntent().getStringExtra("late_n");
            time=getIntent().getStringExtra("date_n");

            //Set
            scan_name.setText(name);
            scan_phone.setText(phone);
            scan_late.setText(late);

        }else{
            Toast.makeText(this,"אין נתונים",Toast.LENGTH_SHORT).show();
        }
    }



    private void update_confirm_Dialog(){
        if(name.isEmpty()) {
            scan_name.setError("חייב להיות שם\nושם משפחה");
        }
       if(phone.length()<10) {
            scan_phone.setError("מספר הפלאפון איננו תקין");
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update) {
            name = scan_name.getText().toString();
            phone = scan_phone.getText().toString();
            late = scan_late.getText().toString();
            time = new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault()).format(new Date());

            if (late.isEmpty()) {
                late = "0";
            }
            if (!name.isEmpty() && phone.length() == 10) {
                String before = getIntent().getStringExtra("late_n");
                if (!before.equals(late)) {
                    new SMS(Update.this, phone, late);
                }
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Student");
                HashMap<String, Object> objectHashMap = new HashMap<>();
                objectHashMap.put("name", name);
                objectHashMap.put("phone", phone);
                objectHashMap.put("late", late);
                objectHashMap.put("date", time);
                myRef.child(id).updateChildren(objectHashMap, (error, ref) -> {
                    Intent next = new Intent(Update.this, Late_List.class);
                    startActivity(next);
                    finish();
                });
            } else {
                update_confirm_Dialog();
            }
        }
        else if(v.getId() == R.id.delete){
            if(!remember.getBoolean("Show",false)) {
                confirmDialog();
            } else{
                Remove();
            }

        }
        else if(v.getId() == R.id.checkbox){
            remember.edit().putBoolean("Show",checkBox.isChecked()).apply();
        }
    }
    private void Remove(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Student");
        myRef.child(id).removeValue().addOnSuccessListener(e->{
            Intent next = new Intent(Update.this, Late_List.class);
            startActivity(next);
            finish();
        });
    }
    private void confirmDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("מחיקה");
        builder.setMessage("האם אתה בטוח שאתה רוצה למחוק את "+name+" ?");
        builder.setView(dialog_view);
        builder.setPositiveButton("כן", (dialog, which) -> Remove());
        builder.setNegativeButton("לא", (dialog, which) -> {});
        builder.create().show();
    }

}