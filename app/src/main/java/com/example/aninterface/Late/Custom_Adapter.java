package com.example.aninterface.Late;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aninterface.R;
import com.example.aninterface.Student;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class Custom_Adapter extends RecyclerView.Adapter<Custom_Adapter.MyViewHolder> {

    private final Context context;
    private final Activity activity;
    private ArrayList<Student> Student_model;
    private final SharedPreferences option;

    Custom_Adapter(Activity activity, Context context, ArrayList<Student> Student_model){
        this.activity=activity;
        this.context = context;
        this.Student_model =Student_model;
        option = context.getSharedPreferences("Option" , Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.student_late,parent,false);
         return new MyViewHolder(view);
    }


    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Student");
        holder.plus.setOnClickListener(v -> {
            Student_model.get(position).setLate(String.valueOf(Integer.parseInt(Student_model.get(position).getLate())+1));
            myRef.child(Student_model.get(position).getId_student()).updateChildren(getHashMap(position), (error, ref) -> {
                new SMS(activity, Student_model.get(position).getPhone(), Student_model.get(position).getLate());
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.sequential);
                holder.plus.setAnimation(animation);
                visually(position, holder);
            });

        });
        holder.cardView.setOnLongClickListener(v->{
            Prompt(position);


            return true;});


        if(checkTime(Student_model.get(position).getDate(),21)){
            Student_model.get(position).setLate(String.valueOf(Integer.parseInt(Student_model.get(position).getLate()) - 1));
            Student_model.get(position).setDate(new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault()).format(new Date()));
            if(Integer.parseInt(Student_model.get(position).getLate())<0){
                Student_model.get(position).setLate(String.valueOf(0));
            }

            //Make update a schema.
            myRef.child(Student_model.get(position).getId_student()).updateChildren(getHashMap(position), (error, ref)->{} );
        }

        /* Make a warning image and other visually stuff. */
        visually(position,holder);

        /* Recyclerview onClickListener */
        holder.cardView.setOnClickListener(view -> {

            Intent intent = new Intent(context, Update.class);
            intent.putExtra("id_n", Student_model.get(position).getId_student());
            intent.putExtra("name_n", Student_model.get(position).getName());
            intent.putExtra("phone_n", Student_model.get(position).getPhone());
            intent.putExtra("late_n", Student_model.get(position).getLate());
            intent.putExtra("date_n", Student_model.get(position).getDate());
            activity.startActivityForResult(intent, 1);
        });

    }

    @Override
    public int getItemCount() {
        return Student_model.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
       private ImageView warning,plus;
       private TextView name_txt,late_txt;
       private LinearLayout mainLayout;
       private CardView cardView;
       private Animation translate_anim;
       ConstraintLayout constraintLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Init();
            SetAnimation();
            Option_Plus_Button();
        }

        private void Init() {
            warning=itemView.findViewById(R.id.error);
            late_txt = itemView.findViewById(R.id.id_view);
            name_txt= itemView.findViewById(R.id.nameview);
            plus =itemView.findViewById(R.id.add_late_button);
            constraintLayout=itemView.findViewById(R.id.constraint_card);
            cardView=itemView.findViewById(R.id.cardView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            translate_anim= AnimationUtils.loadAnimation(context,R.anim.transliet_anim);
        }

        private void SetAnimation() {
            mainLayout.setAnimation(translate_anim);
        }

        private void Option_Plus_Button(){
            if(option.getBoolean("Show_Plus_Button",false)) {
                plus.setVisibility(View.GONE);
            }else{
                plus.setVisibility(View.VISIBLE);
            }
        }

    }

@RequiresApi(api = Build.VERSION_CODES.O)
private void visually(int position, final MyViewHolder holder){

    if(Integer.parseInt(Student_model.get(position).getLate())>0 && Integer.parseInt(Student_model.get(position).getLate())>=5){
        if(holder.warning.getVisibility()==View.GONE){
            addNotification(position,"יש לקחת את המכשיר למשך שבוע");
        }
        holder.warning.setVisibility(View.VISIBLE);
        if(checkTime(Student_model.get(position).getDate(),7)){
            holder.warning.setVisibility(View.GONE);
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Student");
            Student_model.get(position).setLate("0");
            Student_model.get(position).setDate(new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault()).format(new Date()));
            myRef.child(Student_model.get(position).getId_student()).updateChildren(getHashMap(position), (error, ref)-> addNotification(position,"יש להחזיר את המכשיר , עבר שבוע"));

        }
    }


    else {
        holder.warning.setVisibility(View.GONE);
    }

    holder.name_txt.setText(Student_model.get(position).getName());
    holder.late_txt.setText(Student_model.get(position).getLate());

    }

        void reset(){
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Student");
            for (int position = 0; position< Student_model.size(); position++) {
                Student_model.get(position).setLate("0");
                Student_model.get(position).setDate(new SimpleDateFormat("dd:MM:yyyy", Locale.getDefault()).format(new Date()));
                myRef.child(Student_model.get(position).getId_student()).updateChildren(getHashMap(position), (error, ref)->{} );
            }
        }

        private boolean checkTime(String time,int day){
            Calendar c = Calendar.getInstance();
            Date Today = new Date(),Pre=null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy",Locale.getDefault());
            try {
                c.setTime(Objects.requireNonNull(sdf.parse(time)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DAY_OF_MONTH,-day);
            try {
                Pre=c.getTime();
                Today=sdf.parse(Today.toString());
            }catch (ParseException e) {
                e.printStackTrace();
            }
            return (Objects.requireNonNull(Pre).after(Today));
        }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNotification(int position,String txt) {
        if(!option.getBoolean("Stop_Notification",false)) {
            Intent intent = new Intent(context.getApplicationContext(), Update.class);
            intent.putExtra("id_n", Student_model.get(position).getId_student());
            intent.putExtra("name_n", Student_model.get(position).getName());
            intent.putExtra("phone_n", Student_model.get(position).getPhone());
            intent.putExtra("late_n", Student_model.get(position).getLate());
            intent.putExtra("date_n", Student_model.get(position).getDate());
            String CHANNEL_ID = "MY_CHANNEL";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_LOW);
            PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
            Notification notification = new Notification.Builder(context.getApplicationContext(), CHANNEL_ID)
                    .setContentText(txt)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentTitle("עבור " + Student_model.get(position).getName())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setChannelId(CHANNEL_ID)
                    .setSmallIcon(R.drawable.icon)
                    .addAction(R.drawable.icon, "צפה", pendingIntent)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(position, notification);

            }
        }

        void FilterList(ArrayList<Student> Filter_Student){
        Student_model=Filter_Student;
        notifyDataSetChanged();
        }

        private HashMap<String,Object> getHashMap(int position){
            HashMap<String,Object> objectHashMap=new HashMap<>();
            objectHashMap.put("name",Student_model.get(position).getName());
            objectHashMap.put("phone",Student_model.get(position).getPhone());
            objectHashMap.put("late",Student_model.get(position).getLate());
            objectHashMap.put("date",Student_model.get(position).getDate());
            return objectHashMap;
        }
        @SuppressLint("SetTextI18n")
        private void Prompt(int position){
            Dialog builder=new Dialog(context);
            builder.setTitle(Student_model.get(position).getName());
            final View dialog_view = View.inflate(context, R.layout.dialog_cardview, null);
            TextView name_view = dialog_view.findViewById(R.id.n1_dialog_show),phone_view = dialog_view.findViewById(R.id.n2_dialog_show),late_view=dialog_view.findViewById(R.id.n3_dialog_show);
            builder.setContentView(dialog_view);
            name_view.setText("שם ומשפחה : "+Student_model.get(position).getName());
            phone_view.setText("פלאפון : "+Student_model.get(position).getPhone());
            late_view.setText("איחורים : "+Student_model.get(position).getLate());
            builder.setCanceledOnTouchOutside(true);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            builder.show();


        }


    }

