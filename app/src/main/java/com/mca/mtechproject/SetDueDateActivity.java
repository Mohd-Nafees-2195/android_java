package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetDueDateActivity extends AppCompatActivity {

    private Button selectDate,selectTime,submit;
    private TextView setDate,setTime, title;
    private String date="",time="";
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_due_date);

        selectDate=findViewById(R.id.selectDateButton);
        selectTime=findViewById(R.id.selectTimeButton);
        submit=findViewById(R.id.submitButton);
        setDate=findViewById(R.id.selectedDateTextView);
        setTime=findViewById(R.id.selectedTimeTextView);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetDueDateActivity.this, ManageStudentByGuideActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Set Submission Date & Time");

        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(SetDueDateActivity.this,CalendarPickerActivity.class));
               openDateDialog();
            }
        });

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimeDialog();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeAndDate=date+","+time;
                FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
                //String userId=currentUser.getUid();
                String userEmail=currentUser.getEmail();
                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("StudentDetails");
                databaseReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if(time.compareTo("")==0||date.compareTo("")==0){
                                    Toast.makeText(SetDueDateActivity.this, "Please Select Date and Time", Toast.LENGTH_SHORT).show();
                                }else {
                                    for(DataSnapshot dataSnap:dataSnapshot.getChildren()){
                                        StudentData data=dataSnap.getValue(StudentData.class);
                                        if(data.getGuideEmail().compareTo(userEmail)==0){
                                            data.setTimeDuration(timeAndDate);
                                            String userId=dataSnap.getKey();
                                            //Toast.makeText(SetDueDateActivity.this, userId, Toast.LENGTH_SHORT).show();
                                            databaseReference.child(userId).setValue(data);
                                            Toast.makeText(SetDueDateActivity.this, "Time Duration Added Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SetDueDateActivity.this,ManageStudentByGuideActivity.class));
                                            finish();
                                        }
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }

    private void openDateDialog(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        String currentTime=dateFormat.format(Calendar.getInstance().getTime());
        String arrayOfDate[]=currentTime.split("/");
        int year=Integer.parseInt(arrayOfDate[2]);
        int month=Integer.parseInt(arrayOfDate[1]);
        int day=Integer.parseInt(arrayOfDate[0]);
        DatePickerDialog datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date=String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year);
                setDate.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
            }
        }, year, month-1, day);
        datePickerDialog.show();
    }

    private void openTimeDialog(){
        TimePickerDialog timePickerDialog=new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time=String.valueOf(hourOfDay)+":"+String.valueOf(minute);
                setTime.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
            }
        }, 15, 00, true);
        timePickerDialog.show();
    }

}