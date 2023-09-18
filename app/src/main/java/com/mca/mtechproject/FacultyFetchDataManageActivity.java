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

public class FacultyFetchDataManageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FacultyAdapterManage myAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_fetch_data_manage);

        progressBar = findViewById(R.id.loadingRecords);
        recyclerView=findViewById(R.id.facRecord);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().setTitle("Search By Email");

        FirebaseRecyclerOptions<FacultyData> options =
                new FirebaseRecyclerOptions.Builder<FacultyData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("FacultyDetails"), FacultyData.class)
                        .build();


        myAdapter = new FacultyAdapterManage(options);
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
        FirebaseRecyclerOptions<FacultyData> options =
                new FirebaseRecyclerOptions.Builder<FacultyData>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("FacultyDetails").orderByChild("email").startAt(s).endAt(s+"~"), FacultyData.class)
                        .build();

        myAdapter = new FacultyAdapterManage(options);
        myAdapter.startListening();
        recyclerView.setAdapter(myAdapter);
    }
}