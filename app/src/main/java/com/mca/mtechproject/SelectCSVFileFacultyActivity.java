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

public class SelectCSVFileFacultyActivity extends AppCompatActivity {

    private ImageView selectCSV,registerAll,cancleCSV,fileLogo, backArrow;
    private TextView textView, title;
    private ProgressBar progressBar;
    private Uri fileUri;
    ArrayList<FacultyData> facultyDataList;
    int count=0;
    private String fullPath=null,failedRegistation="",successRegistration="";
    FirebaseAuth auth=FirebaseAuth.getInstance();


    private  static  final  String TAG="SelectCSVFileFacultyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_csvfile_faculty);

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
                Intent intent = new Intent(SelectCSVFileFacultyActivity.this, AdminHomeActivity.class);
                startActivity(intent);
            }
        });

        title.setText("Upload CSV File to Register Faculty");

        cancleCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileLogo.setVisibility(View.INVISIBLE);
                cancleCSV.setVisibility(View.INVISIBLE);
                selectCSV.setVisibility(View.VISIBLE);
                textView.setText("NO File Selected");
                fullPath=null;
            }
        });

        registerAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //String path="sdcard/DCIM/Book1.csv";
                    if (fullPath == null) {
                        Toast.makeText(SelectCSVFileFacultyActivity.this, "Please Select A File", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SelectCSVFileFacultyActivity.this, AdminHomeActivity.class));
                        finish();
                    } else {
                        CSVReader csvReader = new CSVReader(new FileReader(fullPath));
                        facultyDataList = new ArrayList<>();
                        String nextLine[];
                        String header[] = null;
                        while ((nextLine = csvReader.readNext()) != null) {
                            int len = nextLine.length;
                            if (len != 6) {
                                Toast.makeText(SelectCSVFileFacultyActivity.this, "Data Missing Please Check CSV Template", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SelectCSVFileFacultyActivity.this, AdminHomeActivity.class));
                                finish();
                            } else {
                                if (header == null) {
                                    header = nextLine;
                                } else {
                                    String fullName = "", email = "", department = "", mobileNumber = "", password = "";// emailExaminer1="", emailExaminer2="";
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
                                        else if (key.compareTo("department") == 0 ) {
                                            department = value;
                                        }
                                        else if (key.compareTo("mobilenumber") == 0 || key.compareTo("mobile number") == 0 || key.compareTo("phonenumber") == 0 || key.compareTo("phone number") == 0 || key.compareTo("phone_number") == 0 || key.compareTo("mobile_number") == 0) {
                                            mobileNumber = value;
                                        }
                                        else if (key.compareTo("password") == 0) {
                                            password = value;
                                        }
//                                        else if (key.compareTo("emailexaminer1") == 0 ) {
//                                            emailExaminer1 = value;
//                                        }
//                                        else if (key.compareTo("emailexaminer2") == 0) {
//                                            emailExaminer2 = value;
//                                        }
                                    }
                                    if (fullName.compareTo("") == 0 || department.compareTo("") == 0 || email.compareTo("") == 0 || mobileNumber.compareTo("") == 0 || password.compareTo("") == 0) {
                                        Toast.makeText(SelectCSVFileFacultyActivity.this, "Some data are missing", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SelectCSVFileFacultyActivity.this, AdminHomeActivity.class));
                                        finish();
                                    }
                                    else {
                                        FacultyData data = new FacultyData(fullName, email, mobileNumber, department, "", "", password, "Faculty");
                                        if (!checkAllData(data)) {
                                            Toast.makeText(SelectCSVFileFacultyActivity.this, "Data Missing Please Check Your CSV File", Toast.LENGTH_SHORT).show();
                                            textView.setText("Data Missing Please Check Your CSV File");
                                            startActivity(new Intent(SelectCSVFileFacultyActivity.this, AdminHomeActivity.class));
                                            finish();
                                        } else {
                                            facultyDataList.add(data);
                                        }
                                    }
                                }
                            }
                        }

                        if (!facultyDataList.isEmpty()){
                            progressBar.setVisibility(View.VISIBLE);
                            int i=0;
                            insertAllData(i);
                        }
                        else {
                            Toast.makeText(SelectCSVFileFacultyActivity.this, "No Data Found In CSV", Toast.LENGTH_SHORT).show();
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
                                startActivityForResult(Intent.createChooser(intent, "Select CSV Files"),86);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                Toast.makeText(SelectCSVFileFacultyActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });
    }

    private boolean checkAllData(FacultyData data) {
        String  mobileRegex="[6-9][0-9]{9}";//First digit can be 6,7,8 and 9 and rest number will be from 0 to 9
        Matcher mobileMatcher;
        Pattern mobilePattern=Pattern.compile(mobileRegex);
        mobileMatcher=mobilePattern.matcher(data.getMobileNumber());

        if(!Patterns.EMAIL_ADDRESS.matcher(data.getEmail()).matches()) {
            Toast.makeText(getApplicationContext(), "Valid email is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if(data.getMobileNumber().length()!=10){
            Toast.makeText(getApplicationContext(), "Mobile Number Must Contain 10 Digits", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!mobileMatcher.find()){
            Toast.makeText(getApplicationContext(), "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(data.getDepartment())){
            Toast.makeText(getApplicationContext(), "Fill Department Name", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(data.getExaminer1())){
            Toast.makeText(getApplicationContext(), "Fill Examiner1", Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(data.getExaminer2())) {
            Toast.makeText(getApplicationContext(), "Fill Examiner2", Toast.LENGTH_SHORT).show();
            return false;
        } else {
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
            Toast.makeText(SelectCSVFileFacultyActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
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
    private void insertAllData(int i){
        if(i<facultyDataList.size()){
            FacultyData data=facultyDataList.get(i);
            auth.createUserWithEmailAndPassword(data.getEmail(), data.getPassword())
                    .addOnCompleteListener(SelectCSVFileFacultyActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();

                                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(data.getFullName()).build();
                                firebaseUser.updateProfile(profileChangeRequest);

                                //Now Registration is successful so we need to store the details of faculty into realtime database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("FacultyDetails");

                                myRef.child(firebaseUser.getUid()).setValue(data)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    //Send Verification Email
                                                    firebaseUser.sendEmailVerification();
                                                    successRegistration += "Faculty - " + data.getEmail() + "- Success!! ";
                                                    insertAllData(i+1);
                                                    count++;
                                                    auth.signOut();
                                                } else {
                                                    Toast.makeText(SelectCSVFileFacultyActivity.this, "Registration Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                                                    failedRegistation+="Faculty - " + data.getEmail() +"Failed!! ";
                                                }
                                            }
                                        });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(SelectCSVFileFacultyActivity.this, "Your Password is too week! Kindly use a mix of alphabets, Numbers and Special Characters", Toast.LENGTH_SHORT).show();
                                    failedRegistation += "Faculty - " + data.getEmail() + "Failed due to Password was too week!!  ";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(SelectCSVFileFacultyActivity.this, "Your Email is invalid or already in use. Kindly Re-Enter", Toast.LENGTH_SHORT).show();
                                    failedRegistation += "Faculty - " + data.getEmail() + "Failed due to invalid Email!!  ";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(SelectCSVFileFacultyActivity.this, "User is already registered with this email!! Skipping...", Toast.LENGTH_SHORT).show();
                                    failedRegistation += "Faculty - " + data.getEmail() + "Failed due to already registered!! ";
                                } catch (Exception e) {
                                    Toast.makeText(SelectCSVFileFacultyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    failedRegistation+="Faculty - " + data.getEmail() +"Failed!! ";
                                }
                            }
                        }
                    });
        }
        if(failedRegistation==null||failedRegistation.compareTo("")==0){
            fileLogo.setVisibility(View.INVISIBLE);
            cancleCSV.setVisibility(View.INVISIBLE);
            selectCSV.setVisibility(View.VISIBLE);
            textView.setText(successRegistration);
            if(count==facultyDataList.size()-1){
                progressBar.setVisibility(View.GONE);
                successRegistration+="!! Total Registered Faculties are => "+ (count+1)+"!! ";
                successRegistration+=" ** All Faculty Registered Successfully **";
                textView.setText(successRegistration);
                Toast.makeText(SelectCSVFileFacultyActivity.this, "All Faculty Registered Successfully", Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(SelectCSVFileFacultyActivity.this, "Failed to register some Faculty ", Toast.LENGTH_SHORT).show();
            textView.setText(failedRegistation);
        }

    }
}