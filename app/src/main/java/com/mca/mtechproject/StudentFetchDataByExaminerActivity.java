package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StudentFetchDataByExaminerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewByExaminer;
    private ProgressBar progressBarByExaminer;
    private StudentAdapterByExaminer examinerAdapter;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fetch_data_by_examiner);

        recyclerViewByExaminer=findViewById(R.id.stuRecordByExaminer);
        progressBarByExaminer=findViewById(R.id.loadingRecordsByExaminer);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentFetchDataByExaminerActivity.this, ManageStudentByExaminerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Examiner of Student");

        recyclerViewByExaminer.setLayoutManager(new LinearLayoutManager(this));

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();
        String userEmail=firebaseUser.getEmail();

        SharedPreferences sharedPreferences=getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String Examiner=sharedPreferences.getString("Examiner",null);


        if(Examiner.compareTo("Examiner1")==0) {
            FirebaseRecyclerOptions<StudentData> options =
                    new FirebaseRecyclerOptions.Builder<StudentData>()  //orderByChild("examiner1").equalTo(userEmail).orderByChild("examiner2").equalTo(userEmail)
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentDetails").orderByChild("examiner1Email").equalTo(userEmail), StudentData.class)
                            .build();

            examinerAdapter=new StudentAdapterByExaminer(options);
            examinerAdapter.startListening();
            recyclerViewByExaminer.setAdapter(examinerAdapter);
        } else{
            FirebaseRecyclerOptions<StudentData> options =
                    new FirebaseRecyclerOptions.Builder<StudentData>()  //orderByChild("examiner1").equalTo(userEmail).orderByChild("examiner2").equalTo(userEmail)
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentDetails").orderByChild("examiner2Email").equalTo(userEmail), StudentData.class)
                            .build();

            examinerAdapter=new StudentAdapterByExaminer(options);
            examinerAdapter.startListening();
            recyclerViewByExaminer.setAdapter(examinerAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        examinerAdapter.startListening();
        examinerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        examinerAdapter.stopListening();
    }
}