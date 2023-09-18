package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StudentFetchDataByGuideActivity extends AppCompatActivity {

    private RecyclerView recyclerViewByGuide;
    private StudentAdapterByGuide guideAdapter;
    private ProgressBar progressBarByGuide;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fetch_data_by_guide);

        recyclerViewByGuide=findViewById(R.id.stuRecordByGuide);
        progressBarByGuide=findViewById(R.id.loadingRecordsByGuide);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentFetchDataByGuideActivity.this, ManageStudentByGuideActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Selected Students");

        recyclerViewByGuide.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String guideEmail=firebaseUser.getEmail();
        FirebaseRecyclerOptions<StudentData> options =
                new FirebaseRecyclerOptions.Builder<StudentData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentDetails").orderByChild("guideEmail").equalTo(guideEmail), StudentData.class)
                        .build();

        guideAdapter=new StudentAdapterByGuide(options);
        guideAdapter.startListening();
        recyclerViewByGuide.setAdapter(guideAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        guideAdapter.startListening();
        guideAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        guideAdapter.stopListening();
    }
}