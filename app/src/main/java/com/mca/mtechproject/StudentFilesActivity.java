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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StudentFilesActivity extends AppCompatActivity
{
    private RecyclerView recview;
    private FileAdapter adapter;
    private ImageView backArrow;
    private TextView title;


    FirebaseAuth auth;
    String rollNumber="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_files);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recview = (RecyclerView) findViewById(R.id.pfdrecview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StudentFilesActivity.this, StudentHomeActivity.class));
                finish();
            }
        });

        title.setText("All Files");

        auth=FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        String userId=user.getUid();

        FirebaseRecyclerOptions<FileModel> options =
                new FirebaseRecyclerOptions.Builder<FileModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("uploads").child(userId), FileModel.class)
                        .build();

        adapter = new FileAdapter(options);
        adapter.startListening();
        recview.setAdapter(adapter);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
}