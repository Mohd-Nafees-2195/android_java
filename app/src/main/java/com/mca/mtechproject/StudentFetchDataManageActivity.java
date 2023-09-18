package com.mca.mtechproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class StudentFetchDataManageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StudentAdapterManage myAdapter;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fetch_data_manage);

        progressBar = findViewById(R.id.loadingRecords);
        recyclerView=findViewById(R.id.stuRecord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("Search By Roll Number");

        FirebaseRecyclerOptions<StudentData> options =
                new FirebaseRecyclerOptions.Builder<StudentData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentDetails"), StudentData.class)
                        .build();


        myAdapter = new StudentAdapterManage(options);
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search);
        android.widget.SearchView searchView=(android.widget.SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processsearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processsearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void processsearch(String s)
    {
        FirebaseRecyclerOptions<StudentData> options =
                new FirebaseRecyclerOptions.Builder<StudentData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("StudentDetails").orderByChild("rollNumber").startAt(s).endAt(s+"~"), StudentData.class)
                        .build();

        myAdapter = new StudentAdapterManage(options);
        myAdapter.startListening();
        recyclerView.setAdapter(myAdapter);
    }
}