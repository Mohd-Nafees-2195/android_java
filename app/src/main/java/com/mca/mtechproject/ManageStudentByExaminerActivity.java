package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageStudentByExaminerActivity extends AppCompatActivity {

    private Button Announcement,examiner1,examiner2;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_student_by_examiner);

        Announcement=findViewById(R.id.manageAnnouncementByExaminerButton);
        examiner1=findViewById(R.id.manageStudentByExaminer1Button);
        examiner2=findViewById(R.id.manageStudentByExaminer2Button);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageStudentByExaminerActivity.this, FacultyHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Evaluate Student as Examiner");

        Announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Announcement section
                Intent intent=new Intent(ManageStudentByExaminerActivity.this,FetchAnnounceDocsByExaminerActivity.class);
                //intent.putExtra("AnnouncementUser","Examiner");
                startActivity(intent);
                finish();
            }
        });

        examiner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("Examiner","Examiner1");
                editor.apply();
                Intent intent=new Intent(ManageStudentByExaminerActivity.this,StudentFetchDataByExaminerActivity.class);
                startActivity(intent);
            }
        });

        examiner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                editor.putString("Examiner","Examiner2");
                editor.apply();
                Intent intent=new Intent(ManageStudentByExaminerActivity.this,StudentFetchDataByExaminerActivity.class);
                startActivity(intent);
            }
        });
    }
}