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

public class FetchAllStudentByCoordinatorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FetchAllStudentByCoorAdapter adapter;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_all_student_by_coordinator);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FetchAllStudentByCoordinatorActivity.this, ManagePeopleByCoordinatorActivity.class));
                finish();
            }
        });

        title.setText("All Students");

        FirebaseRecyclerOptions<StudentData> options =
                new FirebaseRecyclerOptions.Builder<StudentData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentDetails").orderByChild("fullName"), StudentData.class)
                        .build();

        adapter = new FetchAllStudentByCoorAdapter(options);
        adapter.startListening();
        recyclerView.setAdapter(adapter);
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