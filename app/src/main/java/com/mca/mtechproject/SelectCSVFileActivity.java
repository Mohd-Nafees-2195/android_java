package com.mca.mtechproject;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectCSVFileActivity extends AppCompatActivity {

    private ImageView selectCSV,registerAll,cancleCSV,fileLogo, backArrow;
    private TextView textView,textView1, title;
    private ProgressBar progressBar;
    private Uri fileUri;
    private String fullPath=null,failedRegistration="",successRegistration="";
    int count=0;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    private  static  final  String TAG="SelectCSVFileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_csvfile);

        textView=findViewById(R.id.editTextMultiLine);
        selectCSV=findViewById(R.id.selectCSV);
        registerAll=findViewById(R.id.registerAll);
        cancleCSV=findViewById(R.id.cancelCSV);
        fileLogo=findViewById(R.id.filelogo);
        progressBar=findViewById(R.id.CSVProgressBar);

        fileLogo.setVisibility(View.INVISIBLE);
        cancleCSV.setVisibility(View.INVISIBLE);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCSVFileActivity.this, AdminHomeActivity.class);
                startActivity(intent);
            }
        });

        title.setText("Upload CSV File to Register Student");

        cancleCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileLogo.setVisibility(View.INVISIBLE);
                cancleCSV.setVisibility(View.INVISIBLE);
                selectCSV.setVisibility(View.VISIBLE);
                textView.setText("NO File Selected");
                //fileUri=null;
                fullPath=null;
            }
        });

        registerAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //String path="sdcard/DCIM/Book1.csv";
                    if (fullPath == null) {
                        Toast.makeText(SelectCSVFileActivity.this, "Please Select A File", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SelectCSVFileActivity.this, AdminHomeActivity.class));
                        finish();
                    } else {
                        CSVReader csvReader = new CSVReader(new FileReader(fullPath));
                        ArrayList<StudentData> studentDataList = new ArrayList<>();
                        String nextLine[];
                        String header[] = null;
                        while ((nextLine = csvReader.readNext()) != null) {
                            int len = nextLine.length;
                            if (len != 7) {
                                Toast.makeText(SelectCSVFileActivity.this, "Data Missing Please Check CSV Template", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                if (header == null) {
                                    header = nextLine;
                                } else {
                                    String fullName = "", email = "", rollNumber = "", course = "", mobileNumber = "", password = "";
                                    for (int i = 1; i < len; i++) {
                                        String key = header[i].trim();
                                        String value = nextLine[i].trim();
                                        key = key.toLowerCase();
                                        if (key.compareTo("fullname") == 0 || key.compareTo("full name") == 0 || key.compareTo("name") == 0 || key.compareTo("full_name") == 0) {
                                            fullName = value;
                                        }
                                        else if (key.compareTo("email") == 0 || key.compareTo("emailid") == 0 || key.compareTo("email_id") == 0 || key.compareTo("email id") == 0) {
                                            email = value;
                                        }
                                        else if (key.compareTo("rollnumber") == 0 || key.compareTo("roll_number") == 0 || key.compareTo("roll number") == 0) {
                                            rollNumber = value;
                                        }
                                        else if (key.compareTo("course") == 0 || key.compareTo("stream") == 0) {
                                            course = value;
                                        }
                                        else if (key.compareTo("mobilenumber") == 0 || key.compareTo("mobile number") == 0 || key.compareTo("phonenumber") == 0 || key.compareTo("phone number") == 0 || key.compareTo("phone_number") == 0 || key.compareTo("mobile_number") == 0) {
                                            mobileNumber = value;
                                        }
                                        else if (key.compareTo("password") == 0) {
                                            password = value;
                                        }
                                    }
                                    if (fullName.compareTo("") == 0 || rollNumber.compareTo("") == 0 || email.compareTo("") == 0 || mobileNumber.compareTo("") == 0 || course.compareTo("") == 0 || password.compareTo("") == 0) {
                                        Toast.makeText(SelectCSVFileActivity.this, "Attributes are empty", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SelectCSVFileActivity.this,AdminHomeActivity.class));
                                        finish();
                                    }
                                    else {
                                        StudentData data = new StudentData(fullName, rollNumber, email, mobileNumber, course, password, "", "", "", "");
                                        if (!checkAllData(data)) {
                                            Toast.makeText(SelectCSVFileActivity.this, "Data Missing Please Check Your CSV File", Toast.LENGTH_SHORT).show();
                                            textView.setText("Data Missing Please Check Your CSV File");
                                            startActivity(new Intent(SelectCSVFileActivity.this,AdminHomeActivity.class));
                                            finish();
                                        } else {
                                            studentDataList.add(data);
                                        }
                                    }
                                }
                            }
                        }
                        if (!studentDataList.isEmpty()){
                            progressBar.setVisibility(View.VISIBLE);
                            int i=0;
                            insertAllData(i,studentDataList);
                        }
                        else {
                            Toast.makeText(SelectCSVFileActivity.this, "No Data Found In CSV", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        selectCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent=new Intent();
                                intent.setType("application/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);//to fetch files
                                startActivityForResult(Intent.createChooser(intent, "Select Pdf Files"),86);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                Toast.makeText(SelectCSVFileActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    private boolean checkAllData(StudentData data) {
        String  mobileRegex="[6-9][0-9]{9}";//First digit can be 6,7,8 and 9 and rest number will be from 0 to 9
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegex);
        mobileMatcher=mobilePattern.matcher(data.getMobileNumber());

        if(!Patterns.EMAIL_ADDRESS.matcher(data.getEmail()).matches()) {
            Toast.makeText(getApplicationContext(), "Valid email is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(data.getMobileNumber().length()!=10){
            Toast.makeText(getApplicationContext(), "Mobile Number Must Contain 10 Digits", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!mobileMatcher.find()){
            Toast.makeText(getApplicationContext(), "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(TextUtils.isEmpty(data.getCourse())) {
            Toast.makeText(getApplicationContext(), "Please Enter Your Course", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if (isValid(data.getPassword())) {
                return true;
            } else {
                Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters, having letter,digit and special symbol", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check user has selected a file or not
        if(requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            fileUri=data.getData();//Returns the uri of selected file

            fileLogo.setVisibility(View.VISIBLE);
            cancleCSV.setVisibility(View.VISIBLE);
            selectCSV.setVisibility(View.INVISIBLE);

            fullPath=fileUri.getPath();
            String str[]=fullPath.split("/");
            int len=str.length;
            fullPath="sdcard";
            for(int i=2;i<len;i++){
                fullPath=fullPath+"/"+str[i];
            }
            textView.setText(fullPath);
        }
        else
        {
            Toast.makeText(SelectCSVFileActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
        }
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

    private void insertAllData(int i,ArrayList<StudentData> dataList) {
        //Create Authentication
        if(i<dataList.size()){
            StudentData data=dataList.get(i);
            auth.createUserWithEmailAndPassword(data.getEmail(), data.getPassword())
                    .addOnCompleteListener(SelectCSVFileActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(data.getFullName()).build();
                                firebaseUser.updateProfile(profileChangeRequest);

                                //Now Registration is successful so we need to store the details of faculty into realtime database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("StudentDetails");
                                myRef.child(firebaseUser.getUid()).setValue(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Send Verification Email
                                                    firebaseUser.sendEmailVerification();
                                                    successRegistration += "Student - " + data.getEmail() + " - Success";
                                                    insertAllData(i+1,dataList);
                                                    count++;
                                                    auth.signOut();
                                                } else {
                                                    Toast.makeText(SelectCSVFileActivity.this, "Registration Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                                                    failedRegistration+="Student - "+data.getEmail()+"  Failed!! ";
                                                }
                                            }
                                        });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(SelectCSVFileActivity.this, "Your Password is too week! Kindly use a mix of alphabets, Numbers and Special Characters", Toast.LENGTH_SHORT).show();
                                    failedRegistration+="Student - "+data.getEmail()+"Failed due to Password was too week!! ";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(SelectCSVFileActivity.this, "Your Email is invalid or already in use. Kindly Re-Enter", Toast.LENGTH_SHORT).show();
                                    failedRegistration+="Student - "+data.getEmail()+"Failed due to invalid Email!! ";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(SelectCSVFileActivity.this, "User is already registered with this email!! Skipping...", Toast.LENGTH_SHORT).show();
                                    failedRegistration+="Student - "+data.getEmail()+"Failed due to already registered!! ";
                                } catch (Exception e) {
                                    Toast.makeText(SelectCSVFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    failedRegistration+="Student - "+data.getEmail()+"  Failed!! ";
                                }
                            }
                        }
                    });
        }
        if(failedRegistration==null||failedRegistration.compareTo("")==0){
            fileLogo.setVisibility(View.INVISIBLE);
            cancleCSV.setVisibility(View.INVISIBLE);
            selectCSV.setVisibility(View.VISIBLE);
            textView.setText(successRegistration);
            if(count==dataList.size()-1){
                progressBar.setVisibility(View.GONE);
                successRegistration+="!! Total Registered Students are => "+ (count+1)+"!! ";
                successRegistration+=" ** All Student Registered Successfully **";
                textView.setText(successRegistration);
                Toast.makeText(SelectCSVFileActivity.this, "All Student Registered Successfully", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(SelectCSVFileActivity.this, "Failed to register some students ", Toast.LENGTH_SHORT).show();
            textView.setText(failedRegistration);
        }
    }
}