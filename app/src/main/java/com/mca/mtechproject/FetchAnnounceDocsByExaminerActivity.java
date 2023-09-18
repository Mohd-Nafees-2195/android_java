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

public class FetchAnnounceDocsByExaminerActivity extends AppCompatActivity {
    private RecyclerView docView;
    private ImageView uploadButton, backArrow;
    private TextView title;

    private AnnounceDocsAdapterByExaminer docAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_announce_docs_by_examiner);

        docView=findViewById(R.id.docviewByExaminer);
        uploadButton=findViewById(R.id.uploadFileByExaminer);
        docView.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FetchAnnounceDocsByExaminerActivity.this, ManageStudentByExaminerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Announcement By Examiner");

        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId=currentUser.getUid();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(checkUser.compareTo("Guide")==0){
                Intent intent=new Intent(FetchAnnounceDocsByExaminerActivity.this,UploadFileByFacultyActivity.class);
                intent.putExtra("UploadUser","Examiner");
                startActivity(intent);
            }
        });

        FirebaseRecyclerOptions<FileModel> options=new FirebaseRecyclerOptions.Builder<FileModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("UploadFilesByExaminer").child(userId),FileModel.class)
                .build();

        docAdapter=new AnnounceDocsAdapterByExaminer(options);
        docAdapter.startListening();
        docView.setAdapter(docAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        docAdapter.startListening();
        docAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        docAdapter.stopListening();
    }
}