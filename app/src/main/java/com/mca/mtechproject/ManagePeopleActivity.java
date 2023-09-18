package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagePeopleActivity extends AppCompatActivity {

    private Button students, faculty;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_people);

        students = findViewById(R.id.btn1);
        faculty = findViewById(R.id.btn2);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagePeopleActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Edit/Remove People");

        students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagePeopleActivity.this, StudentFetchDataManageActivity.class));
            }
        });

        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagePeopleActivity.this, FacultyFetchDataManageActivity.class));
            }
        });
    }
}