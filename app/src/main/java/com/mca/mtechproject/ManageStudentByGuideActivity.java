package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManageStudentByGuideActivity extends AppCompatActivity {

    private Button announcementButton,studentButton,timerByGuide;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_student_by_guide);

        announcementButton=findViewById(R.id.manageAnnouncementByGuideButton);
        studentButton=findViewById(R.id.manageStudentByGuideButton);
        timerByGuide=findViewById(R.id.manageTimerByGuideButton);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageStudentByGuideActivity.this, FacultyHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Evaluate Student as Guide");

        announcementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Announcement section
                Intent intent=new Intent(ManageStudentByGuideActivity.this,FetchAnnounceDocsByGuideActivity.class);
                startActivity(intent);
                finish();
            }
        });
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManageStudentByGuideActivity.this,StudentFetchDataByGuideActivity.class));
            }
        });


        timerByGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageStudentByGuideActivity.this,SetDueDateActivity.class));
            }
        });

    }
}