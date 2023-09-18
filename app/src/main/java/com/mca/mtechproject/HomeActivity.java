package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    private CardView cardstudent, cardfaculty, cardadmin, cardabout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        cardstudent=findViewById(R.id.cardStudent);
        cardfaculty=findViewById(R.id.cardFaculty);
        cardadmin=findViewById(R.id.cardAdmin);
        cardabout=findViewById(R.id.cardAbout);

        cardstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,StudentLoginActivity.class));
            }
        });


        cardfaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,FacultyLoginActivity.class));
            }
        });


        cardadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,AdminLoginActivity.class));
            }
        });


        cardabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,AboutActivity.class));
            }
        });
    }
}