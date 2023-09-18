package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagePeopleByCoordinatorActivity extends AppCompatActivity {

    private Button announceByCoordinator,StuCoorButton,FacuCoorButton;
    private TextView title;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_people_by_coordinator);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        //announceByCoordinator.setVisibility(View.GONE);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //announceByCoordinator=findViewById(R.id.manageAnnouncementByCoordinatorButton);
        StuCoorButton=findViewById(R.id.manageStudentByCoordinatorButton);
        FacuCoorButton=findViewById(R.id.manageFacultyByCoordinatorButton);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePeopleByCoordinatorActivity.this, FacultyHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Coordinator");

//        announceByCoordinator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ManagePeopleByCoordinatorActivity.this,FetchAnnounceDocsByCoordinatorActivity.class));
//            }
//        });
        StuCoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ManagePeopleByCoordinatorActivity.this,FetchAllStudentByCoordinatorActivity.class);
                intent.putExtra("RoleTypeToFetchStuData","faculty and coordinator");
                startActivity(intent);
            }
        });
        FacuCoorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagePeopleByCoordinatorActivity.this,FacultyFetchDataActivity.class));
            }
        });
    }
}