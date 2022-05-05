package com.example.aninterface.Late;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aninterface.Home;
import com.example.aninterface.R;
import com.example.aninterface.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Late_List extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private RecyclerView recyclerView;
    private Custom_Adapter customAdapter;
    private ArrayList<Student> Student_model;
    private ProgressBar Load;
    private ImageView empty;
    private  TextView no_data;
    private Animation animation;
    private FloatingActionButton add_button;
    private final FirebaseDatabase db=FirebaseDatabase.getInstance();
    private final DatabaseReference myRef =db.getReference("Student");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_late_list);

        getPermission();//Get Permission for send SMS.

        Init();
        SetAnimation();
        InitListener();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Late_List.this));
        Student_model = new ArrayList<>();
        customAdapter = new Custom_Adapter(Late_List.this, this, Student_model);
        recyclerView.setAdapter(customAdapter);
        InsertDataIntoArrayList();

    }
    private void SetAnimation() {
        add_button.setAnimation(animation);
        Load.setVisibility(View.VISIBLE);
    }

    private void InitListener() {
        add_button.setOnClickListener(view -> {
            Intent intent = new Intent(Late_List.this, Add.class);
            startActivity(intent);
        });
    }


    private void InsertDataIntoArrayList() {
        myRef.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Student_model.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Student obj = dataSnapshot.getValue(Student.class);
                    Student_model.add(obj);
                    Load.setVisibility(View.GONE);
                }
                customAdapter.notifyDataSetChanged();
                if (Student_model.isEmpty()) {
                    empty.setVisibility(View.VISIBLE);
                    no_data.setVisibility(View.VISIBLE);
                    Load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Init() {
        Load=findViewById(R.id.load);
        add_button = findViewById(R.id.button);
        animation = AnimationUtils.loadAnimation(this, R.anim.blink);
        recyclerView = findViewById(R.id.list);
        empty = findViewById(R.id.empty_image);
        no_data = findViewById(R.id.no_data);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    private void filter(String text) {

        ArrayList<Student> Student_model_filter=new ArrayList<>();

        for (Student s : Student_model){
            if(s.getName().toLowerCase().contains(text)){
                Student_model_filter.add(s);
            }
        }
        customAdapter.FilterList(Student_model_filter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem refresh = menu.findItem(R.id.reset);
        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItem option = menu.findItem(R.id.option);


        final SearchView editTextSearch = (SearchView) MenuItemCompat.getActionView(searchItem);


        refresh.setOnMenuItemClickListener(item -> {
            confirmDialog();
            return false;
        });

        editTextSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                editTextSearch.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }

        });

        option.setOnMenuItemClickListener(e -> {
            Intent intent = new Intent(Late_List.this, Option.class);
            startActivity(intent);
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("איתחול");
        builder.setMessage("האם אתה בטוח שאתה רוצה לעשות איתחול?");
        builder.setPositiveButton("כן", (dialog, which) -> {
            customAdapter.reset();
            recyclerView.setAdapter(customAdapter);
        });

        builder.setNegativeButton("לא", (dialog, which) -> {});
        builder.create().show();

    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(this, Home.class);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getPermission() {
        if (
                ContextCompat.checkSelfPermission(Late_List.this,
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Late_List.this,
                    Manifest.permission.SEND_SMS)) {
                System.out.println("no have permission");
            } else {
                ActivityCompat.requestPermissions(Late_List.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
}


