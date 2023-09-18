package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminHomeActivity extends AppCompatActivity {

    private CardView cardRegstudent, cardRegfaculty, cardRegCoordinator, cardManagePeople;
    private TextView edEmail,edFullName;
    private ProgressBar progressBar;
    private String fullName,email;
    private ImageView imageView;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        cardRegstudent = findViewById(R.id.cardRegisterStudent);
        cardRegfaculty = findViewById(R.id.cardRegisterFaculty);
        cardRegCoordinator = findViewById(R.id.cardRegisterCoordinator);
        cardManagePeople = findViewById(R.id.cardManagePeople);

        edEmail=findViewById(R.id.email_field);
        edFullName=findViewById(R.id.fullname_field);
        progressBar=findViewById(R.id.homeFProgressBar);
        imageView=findViewById(R.id.ProfileFImage);

        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        cardRegstudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, StudentRegisterActivity.class));
            }
        });

        cardRegfaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, FacultyRegisterActivity.class));
            }
        });

        cardRegCoordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, CSV_Format_Activity.class));
            }
        });

        cardManagePeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminHomeActivity.this, ManagePeopleActivity.class));
            }
        });

        if(firebaseUser==null){
            Toast.makeText(AdminHomeActivity.this, "Something Went Wrong!,User's Details are not available", Toast.LENGTH_LONG).show();
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }


    //Showing user profile
    private void showUserProfile(FirebaseUser firebaseUser){
        String userId=firebaseUser.getUid();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Admin");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdminData adminData=snapshot.getValue(AdminData.class);
                if(adminData!=null){
                    fullName=adminData.getFullName();
                    getSupportActionBar().setTitle("Welcome Admin");
                    email=adminData.getEmail();
                    edFullName.setText(fullName);
                    edEmail.setText(email);
                    String imgURL=adminData.getImageURL();
                    if(imgURL == null) {
                        imageView.setImageResource(R.drawable.admin);
                    }
                    else {
                        Glide.with(getApplicationContext()).load(imgURL).into(imageView);
                    }
                    progressBar.setVisibility(View.GONE);
                } else{
                    Toast.makeText(AdminHomeActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminHomeActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //Creating ActionBar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu items
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.manage_profile)
        {
            Intent intent=new Intent(AdminHomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra("UserTypeProfile","admin");
            startActivity(intent);
        }
        else if(id==R.id.log_out)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminHomeActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}