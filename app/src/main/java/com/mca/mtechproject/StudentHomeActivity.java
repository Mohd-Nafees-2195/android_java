package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentHomeActivity extends AppCompatActivity {

    private TextView edEmail,edRollNumber,timer0,timer1,timer2,hour,minuts,second,day;
    private Button notice,uploadButton, faculties, allfiles;
    private String fullName,email,rollNumber;
    private CircleImageView imageView;
    ProgressBar progressBar;
    private long startTimeInMillis;
    private FirebaseAuth auth;
    private  String time="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        edEmail=findViewById(R.id.studentEmailField);
        edRollNumber=findViewById(R.id.studentRollNumberField);
        notice=findViewById(R.id.noticeButton);
        uploadButton=findViewById(R.id.uploadButton);
        faculties = findViewById(R.id.facultylist);
        allfiles = findViewById(R.id.allfiles);
        timer1=findViewById(R.id.timer1);
        timer2=findViewById(R.id.timer2);
        hour=findViewById(R.id.hour);
        minuts=findViewById(R.id.minuts);
        second=findViewById(R.id.second);
        day=findViewById(R.id.day);
        timer0=findViewById(R.id.timer0);
        progressBar=findViewById(R.id.studentHomeFProgressBar);
        imageView=findViewById(R.id.studentProfileFImage);

         FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();
         String userId=currentUser.getUid();

         FirebaseDatabase.getInstance().getReference("StudentDetails").child(userId).get()
                 .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                     @Override
                     public void onSuccess(DataSnapshot dataSnapshot) {
                         if(dataSnapshot.exists()){
                             StudentData data=dataSnapshot.getValue(StudentData.class);
                             String timeDuration=data.getTimeDuration();


                             //Get System Date and Time
                             String currentDate=new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());
                             String currentTime=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date());
                             //Date currentTime1= Calendar.getInstance().getTime();


                             if(timeDuration.compareTo("")!=0){
                                 String dateAndTime[]=timeDuration.split(",");
                                 boolean checkTime=compareTime(currentTime,dateAndTime[1]);
                                 boolean checkDate=compareDate(currentDate,dateAndTime[0]);
                                 if(checkDate) {
                                     showTimer(timeDuration);
                                 } else if(compareDateOnEqual(currentDate,dateAndTime[0])){
                                     if(checkTime){
                                         showTimer(timeDuration);
                                     }else {
                                         setVisibility();
                                         time="";
                                     }
                                 }
                                 else{
                                     setVisibility();
                                     time="";
                                 }
                             }else{
                                 setVisibility();
                                 time="";
                             }
                         }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         setVisibility();
                         Toast.makeText(StudentHomeActivity.this, "Time Duration Has Gone", Toast.LENGTH_SHORT).show();
                     }
                 });

        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  //This is announcement section
                startActivity(new Intent(StudentHomeActivity.this,GuideAndExaminerInfoActivity.class));
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StudentHomeActivity.this,UploadFileActivity.class);
                //intent.putExtra("rollNumber",rollNumber);
                startActivity(intent);
            }
        });

        faculties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentHomeActivity.this,FacultyFetchDataActivity.class));
            }
        });

        allfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StudentHomeActivity.this,StudentFilesActivity.class);
                intent.putExtra("rollNumber",rollNumber);
                startActivity(intent);
            }
        });


        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(StudentHomeActivity.this, "Something Went Wrong!,User's Details are not available", Toast.LENGTH_LONG).show();
            //startActivity(new Intent(StudentHomeActivity.this,StudentLoginActivity.class));
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showStudentProfile(firebaseUser);
        }
    }

    private void setVisibility() {
        uploadButton.setVisibility(View.GONE);
        timer0.setVisibility(View.GONE);
        timer1.setVisibility(View.GONE);
        timer2.setVisibility(View.GONE);
        day.setVisibility(View.GONE);
        hour.setVisibility(View.GONE);
        minuts.setVisibility(View.GONE);
        second.setVisibility(View.GONE);
    }

    private boolean compareDateOnEqual(String currentDate, String s) {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date currDate=dateFormat.parse(currentDate);
            Date setDateOnDB=dateFormat.parse(s);
            if(currDate.compareTo(setDateOnDB)==0){
                return true;
            }else {
                return false;
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private boolean compareDate(String currentDate, String s) {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date currDate=dateFormat.parse(currentDate);
            Date setDateOnDB=dateFormat.parse(s);
            if(currDate.before(setDateOnDB)){
                return true;
            }else {
                return false;
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean compareTime(String currentTime, String s) {
        SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm");
        try{
            Date currTime=dateFormat.parse(currentTime);
            Date setTimeOnDB=dateFormat.parse(s);
            if(currTime.before(setTimeOnDB)||currTime.compareTo(setTimeOnDB)==0){
                return true;
            }else {
                return false;
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void showTimer(String timeDuration){
        String dateAndTime[]=timeDuration.split(",");
        String currentDate=new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());
        String currentTime=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date());

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        try {
            Date currDate = dateFormat.parse(currentDate);
            Date getDateOnDB=dateFormat.parse(dateAndTime[0]);

            Date getTimeOnDB = timeFormat.parse(dateAndTime[1]);
            Date currTime=timeFormat.parse(currentTime);

            //Converting date on millies
            long currMillisofDate=currDate.getTime();
            long getDateOnDBMillies=getDateOnDB.getTime();

            //Converting time on millies
            long currMilliesOfTime=currTime.getTime();
            long getTimeOnDBMIllies=getTimeOnDB.getTime();

            if(currDate.compareTo(getDateOnDB)==0){
                //Only time is remaining
                long restTime=getTimeOnDBMIllies-currMilliesOfTime;
                time=String.valueOf(restTime);
            }else{
                long restDate=getDateOnDBMillies-currMillisofDate;
                time=String.valueOf(restDate);
            }

            /**Timer Start from here *******************************************************/

            if(time.length()!=0){
                long millisTime=Long.parseLong(time);
                if(millisTime!=0){
                    CountDownTimer countDownTimer=new CountDownTimer(millisTime,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                            String time1=String.format("%02d:%02d:%02d:%02d",TimeUnit.MILLISECONDS.toDays(millisUntilFinished),
                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)-
                                                    TimeUnit.MILLISECONDS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)),
                                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)), Locale.getDefault());

                                    final String[] hourMinSec=time1.split(":");
                                    uploadButton.setVisibility(View.VISIBLE);
                                    day.setText(hourMinSec[0]);
                                    hour.setText(hourMinSec[1]);
                                    minuts.setText(hourMinSec[2]);
                                    second.setText(hourMinSec[3]);
                                }
                            });
                        }
                        @Override
                        public void onFinish() {
                            setVisibility();
                        }
                    };
                    countDownTimer.start();
                }else {
                    setVisibility();
                }
            }else {
                setVisibility();
            }
            /**Timer End Here **************************************************************/

        }catch (Exception e){
            setVisibility();
            Toast.makeText(StudentHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Showing student profile
    private void showStudentProfile(FirebaseUser firebaseUser){
        String userId=firebaseUser.getUid();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("StudentDetails");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentData studentData=snapshot.getValue(StudentData.class);
                if(studentData!=null){
                    fullName=studentData.getFullName();
                    getSupportActionBar().setTitle("Welcome  "+fullName);
                    email=studentData.getEmail();
                    rollNumber=studentData.getRollNumber();
                    edRollNumber.setText(rollNumber);
                    edEmail.setText(email);
                    String imgURL=studentData.getImageURL();
                    if(imgURL != null && imgURL.compareTo("") !=0){
                        Glide.with(getApplicationContext()).load(imgURL).into(imageView);
                    }
                    else {
                        Glide.with(getApplicationContext()).load(R.drawable.student).into(imageView);
                    }
                    progressBar.setVisibility(View.GONE);
                } else{
                    Toast.makeText(StudentHomeActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    finish();

                    //startActivity(new Intent(StudentHomeActivity.this,StudentLoginActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentHomeActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
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
            Intent intent=new Intent(StudentHomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra("UserTypeProfile","student");
            startActivity(intent);
        }
        else if(id==R.id.log_out)
        {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(StudentHomeActivity.this, HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}