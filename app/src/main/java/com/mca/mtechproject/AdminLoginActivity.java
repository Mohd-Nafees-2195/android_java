package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText edEmail,edPassword;
    private Button btnLogin;
    private ImageView passwordVisibility, backArrow;
    private TextView forgetpassword, title;

    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private  static  final  String TAG="AdminLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edPassword=findViewById(R.id.editTextLogSPassword);
        edEmail=findViewById(R.id.editTextLogSEmail);
        btnLogin=findViewById(R.id.buttonSLogin);
        progressBar=findViewById(R.id.loginProgressBar);
        forgetpassword=findViewById(R.id.forgetpass);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Admin Login");

        passwordVisibility = findViewById(R.id.showpwd);
        // show default icon in password field
        passwordVisibility.setImageResource(R.drawable.ic_baseline_visibility_24);

        // show and hide password in password field
        passwordVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edPassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    // If password is visible then Hide it
                    edPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change Icon
                    passwordVisibility.setImageResource(R.drawable.ic_baseline_visibility_24);
                }
                else{
                    // If password is Hidden then show it
                    edPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    // change Icon
                    passwordVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }
            }
        });

        auth=FirebaseAuth.getInstance();

        // check if admin is already login then redirect it
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null)
        {
            progressBar.setVisibility(View.VISIBLE);
            checkUser(user);
        }

        //Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=edEmail.getText().toString();
                String password=edPassword.getText().toString();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(AdminLoginActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                    edEmail.setError("Email is required");
                    edEmail.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(AdminLoginActivity.this, "Please Re-Enter Your Email", Toast.LENGTH_SHORT).show();
                    edEmail.setError("Invalid Email");
                    edEmail.requestFocus();
                } else if(TextUtils.isEmpty(password)){
                    Toast.makeText(AdminLoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
                    edEmail.setError("Password is required");
                    edEmail.requestFocus();
                } else{
                    progressBar.setVisibility(View.VISIBLE);
                    adminLogin(email,password,user);
                }
            }
        });

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

    }

    ProgressDialog loadingBar;

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);

        // write the email using which you registered
        emailet.setText("Email");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailet.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if (task.isSuccessful()) {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(AdminLoginActivity.this, "Done sent", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AdminLoginActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(AdminLoginActivity.this, "Error Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void adminLogin(String email,String password,FirebaseUser user)
    {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //Get Instance of current user
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            //Check user has verified email or not
                            if(firebaseUser.isEmailVerified()){
                                //checkUser(user);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(AdminLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                firebaseUser.sendEmailVerification();
                                auth.signOut();//sign out user
                                progressBar.setVisibility(View.GONE);
                                showAlertDialog();
                            }

                        } else{
                            try{
                                throw task.getException();
                            } catch(FirebaseAuthInvalidUserException e){
                                edEmail.setError("User not exists or is no longer valid, please register again");
                                edEmail.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e){
                                edEmail.setError("Invalid credentials,Please Try Again");
                                edEmail.requestFocus();
                            } catch(Exception e){
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(AdminLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminLoginActivity.this, "Failed To Login! Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAlertDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(AdminLoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now, You can not login without email verification");

        //Open Email app if user clicks/taps continue button
        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //Create the alert dialog
        AlertDialog alertDialog=builder.create();
        //show the alert dialog
        alertDialog.show();
    }

    private void checkUser(FirebaseUser user){
        String userId=user.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Admin");
        reference.child(userId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                AdminData adminData=dataSnapshot.getValue(AdminData.class);
                if(adminData!=null){
                    Toast.makeText(AdminLoginActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(AdminLoginActivity.this, "Can not Login In This Role", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AdminLoginActivity.this, HomeActivity.class);
                    //auth.signOut();
                    startActivity(intent);
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminLoginActivity.this, "Can not Login In This Role", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}