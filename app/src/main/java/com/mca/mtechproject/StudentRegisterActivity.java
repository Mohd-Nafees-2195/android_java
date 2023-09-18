package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentRegisterActivity extends AppCompatActivity {

    private EditText edFUllName,edEmail,edPassword,edCourse,edRollNumber,edMobileNumber;
    private Button btnStudentRegister;
    private ProgressBar progressBar;
    private TextView tv, title;
    private ImageView backArrow, passwordVisibility;

    private  static  final  String TAG="StudentRegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        edFUllName=findViewById(R.id.editTextSRegFullName);
        edRollNumber=findViewById(R.id.editTextSRegRollNumber);
        edEmail=findViewById(R.id.editTextSRegEmail);
        edMobileNumber=findViewById(R.id.editTextSRegMobileNumber);
        edCourse=findViewById(R.id.editTextSRegCourse);
        edPassword=findViewById(R.id.editTextSRegPassword);
        btnStudentRegister = findViewById(R.id.buttonSRegister);
        tv = findViewById(R.id.textViewCSV);
        progressBar=findViewById(R.id.studentRegProgressBar);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentRegisterActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Student Registration");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StudentRegisterActivity.this,SelectCSVFileActivity.class));
            }
        });

        btnStudentRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName=String.valueOf(edFUllName.getText());
                String rollNumber=String.valueOf(edRollNumber.getText());
                String email=String.valueOf(edEmail.getText());
                String mobileNumber=String.valueOf(edMobileNumber.getText());
                String course=String.valueOf(edCourse.getText());
                String password=String.valueOf(edPassword.getText());

                //Validation of Mobile Number Using Matcher And Pattern (Regular Expression)
                String  mobileRegex="[6-9][0-9]{9}";//First digit can be 6,7,8 and 9 and rest number will be from 0 to 9
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(mobileNumber);

                if(TextUtils.isEmpty(fullName)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Full Name", Toast.LENGTH_SHORT).show();
                    edFUllName.setError("Full Name is required");
                    edFUllName.requestFocus();
                }
                else if(TextUtils.isEmpty(rollNumber)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Roll Number", Toast.LENGTH_SHORT).show();
                    edRollNumber.setError("Roll Number is required");
                    edRollNumber.requestFocus();
                }
                else if(TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                    edEmail.setError("Email is required");
                    edEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(getApplicationContext(), "Please Re-Enter Your Email", Toast.LENGTH_SHORT).show();
                    edEmail.setError("Valid email is required");
                    edEmail.requestFocus();
                }
                else if(TextUtils.isEmpty(mobileNumber)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                    edMobileNumber.setError("Mobile Number is required");
                    edMobileNumber.requestFocus();
                }
                else if(mobileNumber.length()!=10){
                    Toast.makeText(getApplicationContext(), "Please Re-Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                    edMobileNumber.setError("Mobile Number Must Contain 10 Digits");
                    edMobileNumber.requestFocus();
                }
                else if(!mobileMatcher.find()){
                    Toast.makeText(getApplicationContext(), "Please Enter Your Mobile Number", Toast.LENGTH_SHORT).show();
                    edMobileNumber.setError("Invalid Mobile Number");
                    edMobileNumber.requestFocus();
                }
                else if(TextUtils.isEmpty(course)) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Course", Toast.LENGTH_SHORT).show();
                    edCourse.setError("Course is required");
                    edCourse.requestFocus();
                }
                else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    edPassword.setError("Password is required");
                    edPassword.requestFocus();
                }
                else{
                    if (isValid(password)) {
                        progressBar.setVisibility(View.VISIBLE);
                        StudentData data=new StudentData(fullName,rollNumber,email,mobileNumber,course,password,"","","","");
                        insertData(email, data);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters, having letter,digit and special symbol", Toast.LENGTH_SHORT).show();
                        edPassword.clearComposingText();
                    }
                }
            }
        });

    }

    public static boolean isValid(String passwordHere) {
        int f1 = 0, f2 = 0, f3 = 0;
        if (passwordHere.length() < 8)
            return false;
        else {
            for (int p = 0; p < passwordHere.length(); p++) {
                if (Character.isLetter(passwordHere.charAt(p)))
                    f1 = 1;
                if (Character.isDigit(passwordHere.charAt(p)))
                    f2 = 1;
                char c = passwordHere.charAt(p);
                if (c >= 32 && c <= 46 || c == 64)
                    f3 = 1;
            }
        }
        if (f1 == 1 && f2 == 1 && f3 == 1)
            return true;
        return false;
    }

    private void insertData(String email, StudentData data) {

        //Create Authentication
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,data.getPassword())
                .addOnCompleteListener(StudentRegisterActivity.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            FirebaseUser firebaseUser=auth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder().setDisplayName(data.getFullName()).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            //Now Registration is successful so we need to store the details of faculty into realtime database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("StudentDetails");
                            myRef.child(firebaseUser.getUid()).setValue(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                //Send Verification Email
                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(StudentRegisterActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                edFUllName.setText("");
                                                edRollNumber.setText("");
                                                edEmail.setText("");
                                                edMobileNumber.setText("");
                                                edCourse.setText("");
                                                edPassword.setText("");
                                                //Open Faculty Profile after successful registration
                                                //To prevent user from returning back to register Activity on pressing back button after  registration
                                                auth.signOut();
                                                finish();
                                            } else{
                                                Toast.makeText(StudentRegisterActivity.this, "Registration Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else{
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e){
                                edPassword.setError("Your Password is too week! Kindly use a mix of alphabets, Numbers and Special Characters");
                                edPassword.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                edEmail.setError("Your Email is invalid or already in use. Kindly Re-Enter");
                                edEmail.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e){
                                edEmail.setError("User is already registered with this email. Use Another Email");
                                edEmail.requestFocus();
                            } catch (Exception e){
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(StudentRegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

}