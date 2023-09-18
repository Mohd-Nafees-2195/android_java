package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class FetchStudentDocsByExaminerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewForDocsByExaminer;
    private StudentDocsAdapterByExaminer docsAdapterByExaminer;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_student_docs_by_examiner);

        recyclerViewForDocsByExaminer=findViewById(R.id.fetchStudentDocsByExaminer);
        recyclerViewForDocsByExaminer.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FetchStudentDocsByExaminerActivity.this, StudentFetchDataByExaminerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Student Report");

        String studentId=getIntent().getStringExtra("studentId");

        FirebaseRecyclerOptions<FileModel> options =
                new FirebaseRecyclerOptions.Builder<FileModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("uploads").child(studentId).orderByChild("visibility").startAt(1), FileModel.class)
                        .build();

        docsAdapterByExaminer = new StudentDocsAdapterByExaminer(options);
        docsAdapterByExaminer.startListening();
        recyclerViewForDocsByExaminer.setAdapter(docsAdapterByExaminer);

    }
    @Override
    protected void onStart() {
        super.onStart();
        docsAdapterByExaminer.startListening();
        docsAdapterByExaminer.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        docsAdapterByExaminer.startListening();
    }
}