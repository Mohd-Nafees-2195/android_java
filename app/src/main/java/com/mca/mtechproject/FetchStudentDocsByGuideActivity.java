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

public class FetchStudentDocsByGuideActivity extends AppCompatActivity {

    private RecyclerView recyclerViewForDocsByGuide;
    private StudentDocsAdapterByGuide docsAdapterByGuide;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_student_docs_by_guide);

        recyclerViewForDocsByGuide=findViewById(R.id.fetchStudentDocsByGuide);
        recyclerViewForDocsByGuide.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FetchStudentDocsByGuideActivity.this, StudentFetchDataByGuideActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Student Reports");

        String studentId=getIntent().getStringExtra("studentId");

            FirebaseRecyclerOptions<FileModel> options =
                    new FirebaseRecyclerOptions.Builder<FileModel>()
                            .setQuery(FirebaseDatabase.getInstance().getReference().child("uploads").child(studentId), FileModel.class)
                            .build();

            docsAdapterByGuide = new StudentDocsAdapterByGuide(options);
            docsAdapterByGuide.startListening();
            recyclerViewForDocsByGuide.setAdapter(docsAdapterByGuide);

    }

    @Override
    protected void onStart() {
        super.onStart();
        docsAdapterByGuide.startListening();
        docsAdapterByGuide.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        docsAdapterByGuide.startListening();
    }
}