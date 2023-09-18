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

public class FetchStudentDocsByCoordinatorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FetchStudentDocsByCoorAdapter adapter;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_student_docs_by_coordinator);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView=findViewById(R.id.recycleViewByCoorForDoc);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FetchStudentDocsByCoordinatorActivity.this, ManagePeopleByCoordinatorActivity.class));
                finish();
            }
        });

        title.setText("Student Docs");

        String studentId=getIntent().getStringExtra("studentId");

        FirebaseRecyclerOptions<FileModel> options =
                new FirebaseRecyclerOptions.Builder<FileModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("uploads").child(studentId).orderByChild("visibility").startAt(2), FileModel.class)
                        .build();

        adapter=new FetchStudentDocsByCoorAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}