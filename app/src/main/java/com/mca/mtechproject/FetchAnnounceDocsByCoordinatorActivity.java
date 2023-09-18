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

public class FetchAnnounceDocsByCoordinatorActivity extends AppCompatActivity {

    private  RecyclerView docView;
    private ImageView uploadButton, backArrow;
    private AnnounceDocsAdapterByCoordinator docAdapter;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_announce_docs_by_coordinator);

        docView=findViewById(R.id.recycleViewByCoor);
        uploadButton=findViewById(R.id.uploadFileByCoordinator);
        docView.setLayoutManager(new LinearLayoutManager(this));

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FetchAnnounceDocsByCoordinatorActivity.this, ManagePeopleByCoordinatorActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Announcement");

        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId=currentUser.getUid();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(FetchAnnounceDocsByCoordinatorActivity.this,UploadFileByFacultyActivity.class);
                intent.putExtra("UploadUser","Coordinator");
                startActivity(intent);
                finish();
            }
        });

        FirebaseRecyclerOptions<FileModel> options=new FirebaseRecyclerOptions.Builder<FileModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("uploads").child(userId),FileModel.class)
                .build();

        docAdapter=new AnnounceDocsAdapterByCoordinator(options);
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