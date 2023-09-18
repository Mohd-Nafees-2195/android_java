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

public class FetchAnnounceDocsByStudentActivity extends AppCompatActivity {

    private RecyclerView studentRecyclerView;
    private AnnounceDocsAdapterByStudent studentDocsAdapter;
    private ImageView backArrow;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_announce_docs_by_student);

        studentRecyclerView=findViewById(R.id.studentViewDocs);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FetchAnnounceDocsByStudentActivity.this, GuideAndExaminerInfoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Announcement");

        String userId=getIntent().getStringExtra("userId");
        String roleType=getIntent().getStringExtra("roleType");

        if(roleType.compareTo("Guide")==0){
            FirebaseRecyclerOptions<FileModel> options=new FirebaseRecyclerOptions.Builder<FileModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("UploadFilesByGuide").child(userId), FileModel.class)
                    .build();

            studentDocsAdapter=new AnnounceDocsAdapterByStudent(options);
            studentRecyclerView.setAdapter(studentDocsAdapter);
            studentDocsAdapter.startListening();
        }else{
            FirebaseRecyclerOptions<FileModel> options=new FirebaseRecyclerOptions.Builder<FileModel>()
                    .setQuery(FirebaseDatabase.getInstance().getReference().child("UploadFilesByExaminer").child(userId), FileModel.class)
                    .build();

            studentDocsAdapter=new AnnounceDocsAdapterByStudent(options);
            studentRecyclerView.setAdapter(studentDocsAdapter);
            studentDocsAdapter.startListening();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        studentDocsAdapter.startListening();
        studentDocsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        studentDocsAdapter.startListening();
    }

}