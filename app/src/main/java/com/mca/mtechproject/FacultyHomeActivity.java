package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FacultyHomeActivity extends AppCompatActivity {

    private TextView edEmail,edFullName;
    Button allStudent,Coordinator,Examiner,Guide;
    private ProgressBar progressBar;
    private String fullName,email;
    private ImageView imageView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_home);

        edEmail=findViewById(R.id.email_field);
        edFullName=findViewById(R.id.fullname_field);
        progressBar=findViewById(R.id.homeFProgressBar);
        imageView=findViewById(R.id.ProfileFImage);

        //Announcement=findViewById(R.id.button1);
        Guide=findViewById(R.id.button2);
        Examiner=findViewById(R.id.button3);
        Coordinator=findViewById(R.id.button4);
        allStudent=findViewById(R.id.button5);

        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();
        String user=firebaseUser.getUid();
        FirebaseDatabase.getInstance().getReference("FacultyDetails").child(user).get()
                        .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                             FacultyData data=dataSnapshot.getValue(FacultyData.class);
                             if(data.getRole().compareTo("Faculty")==0){
                                 Coordinator.setVisibility(View.GONE);
                             }
                            }
                        });

        Guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Guide Section
                startActivity(new Intent(FacultyHomeActivity.this,ManageStudentByGuideActivity.class));
            }
        });
        Examiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Examiner Section
                startActivity(new Intent(FacultyHomeActivity.this,ManageStudentByExaminerActivity.class));
                //startActivity(new Intent(FacultyHomeActivity.this,StudentFetchDataByExaminerActivity.class));
            }
        });
        Coordinator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Coordinator Section
                String userId=firebaseUser.getUid();
                FirebaseDatabase.getInstance().getReference("FacultyDetails").child(userId)
                        .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                FacultyData data=dataSnapshot.getValue(FacultyData.class);
                                if(data.getRole().compareTo("Faculty & Coordinator")==0){
                                    startActivity(new Intent(FacultyHomeActivity.this,ManagePeopleByCoordinatorActivity.class));
                                }else{
                                    Toast.makeText(FacultyHomeActivity.this, "Invalid Access", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(FacultyHomeActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        allStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FacultyHomeActivity.this,StudentFetchDataActivity.class);
                intent.putExtra("RoleTypeToFetchStuData","faculty");
                startActivity(intent);
            }
        });


        if(firebaseUser==null){
            Toast.makeText(FacultyHomeActivity.this, "Something Went Wrong!,User's Details are not available", Toast.LENGTH_LONG).show();
          // startActivity(new Intent(FacultyHomeActivity.this,FacultyLoginActivity.class));
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }


    //Showing user profile
    private void showUserProfile(FirebaseUser firebaseUser){
        String userId=firebaseUser.getUid();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("FacultyDetails");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FacultyData facultyData=snapshot.getValue(FacultyData.class);
                if(facultyData!=null){
                    fullName=facultyData.getFullName();
                    getSupportActionBar().setTitle("Welcome");
                    email=facultyData.getEmail();
                    edFullName.setText(fullName);
                    edEmail.setText(email);
                    progressBar.setVisibility(View.GONE);
                    String imgURL=facultyData.getImageURL();
                    if(imgURL != null && imgURL.compareTo("") !=0){
                        Glide.with(getApplicationContext()).load(imgURL).into(imageView);
                    }
                    else {
                        Glide.with(getApplicationContext()).load(R.drawable.student).into(imageView);
                    }
                } else{
                    Toast.makeText(FacultyHomeActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    finish();
                    //startActivity(new Intent(FacultyHomeActivity.this,FacultyLoginActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FacultyHomeActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
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
            //start menuUpdateActivity
            Intent intent=new Intent(FacultyHomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra("UserTypeProfile","faculty");
            startActivity(intent);
        }
        else if(id==R.id.log_out)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(FacultyHomeActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}