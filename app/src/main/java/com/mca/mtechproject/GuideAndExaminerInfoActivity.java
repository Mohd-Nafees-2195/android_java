package com.mca.mtechproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class GuideAndExaminerInfoActivity extends AppCompatActivity {

    TextView edGuideName,edExaminer1Name,edExaminer2Name,edGuideEmail,edExaminer1Email,edExaminer2Email, title;
    Button guideButton,examiner1Button,examiner2Button;
    String guideEmail,examiner1Email,examiner2Email;
    String guideUid,examiner1Uid,examiner2Uid;
    ImageView backArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_and_examiner_info);

        edGuideName=findViewById(R.id.guideName);
        edGuideEmail=findViewById(R.id.guideEmail);
        edExaminer1Name=findViewById(R.id.examiner1Name);
        edExaminer1Email=findViewById(R.id.examiner1Email);
        edExaminer2Name=findViewById(R.id.examiner2Name);
        edExaminer2Email=findViewById(R.id.examiner2Email);

        guideButton=findViewById(R.id.announcementByGuide);
        examiner1Button=findViewById(R.id.announcementByExaminer1);
        examiner2Button=findViewById(R.id.announcementByExaminer2);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideAndExaminerInfoActivity.this, StudentHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Guide & Examiner");

        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId= currentUser.getUid();
         FirebaseDatabase.getInstance().getReference("StudentDetails").child(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    StudentData data=dataSnapshot.getValue(StudentData.class);
                    guideEmail=data.getGuideEmail();
                    examiner1Email=data.getExaminer1Email();
                    examiner2Email=data.getExaminer2Email();

                    FirebaseDatabase.getInstance().getReference("FacultyDetails").get()
                            .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child:dataSnapshot.getChildren()) {

                                        FacultyData fData=child.getValue(FacultyData.class);
                                        String email=fData.getEmail();

                                        if(email.compareTo(guideEmail)==0){
                                            edGuideName.setText(fData.getFullName());
                                            edGuideEmail.setText(guideEmail);
                                            guideUid=child.getKey();
                                        }
                                        if(email.compareTo(examiner1Email)==0){
                                            edExaminer1Name.setText(fData.getFullName());
                                            edExaminer1Email.setText(examiner1Email);
                                            examiner1Uid=child.getKey();
                                        }
                                        if(email.compareTo(examiner2Email)==0){
                                            edExaminer2Name.setText(fData.getFullName());
                                            edExaminer2Email.setText(examiner2Email);
                                            examiner2Uid=child.getKey();
                                        }

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(GuideAndExaminerInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                }else{
                    edGuideName.setText("No Guide Selected");
                    edGuideEmail.setText("No Guide Selected");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

         guideButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //see all pdf uploaded by guide by Guide id so pass guideUid
                 Intent intent=new Intent(GuideAndExaminerInfoActivity.this,FetchAnnounceDocsByStudentActivity.class);
                 intent.putExtra("roleType","Guide");
                 intent.putExtra("userId",guideUid);
                 startActivity(intent);
             }
         });
         examiner1Button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //see all pdf uploaded by examiner1 by examiner1Uid so pass examiner1Uid
                 //that you have already taken above
                 //only you have to pass it
                 Intent intent=new Intent(GuideAndExaminerInfoActivity.this,FetchAnnounceDocsByStudentActivity.class);
                 intent.putExtra("roleType","examiner");
                 intent.putExtra("userId",examiner1Uid);
                 startActivity(intent);
             }
         });
         examiner2Button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //use above examiner2Uid to fetch all pdf of examiner2
                 Intent intent=new Intent(GuideAndExaminerInfoActivity.this,FetchAnnounceDocsByStudentActivity.class);
                 intent.putExtra("roleType","examiner");
                 intent.putExtra("userId",examiner2Uid);
                 startActivity(intent);
             }
         });

    }
}