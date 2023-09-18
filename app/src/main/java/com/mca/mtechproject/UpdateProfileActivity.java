package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editName, editMobile;
    private FirebaseAuth auth;
    private Button updateButton;
    private ImageView userImage, backArrow, choosePhoto;
    private TextView title;

    private Uri selectedImageUri=null;

    private final int SELECT_PICTURE=100;

    String sname, smobile, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        editName = findViewById(R.id.name);
        editMobile = findViewById(R.id.phone);
        updateButton = (Button) findViewById(R.id.btnUpdate);
        choosePhoto = (ImageView)findViewById(R.id.camera);
        userImage = (ImageView) findViewById(R.id.profileImage);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, StudentHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Update Profile");

        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userType=getIntent().getStringExtra("UserTypeProfile");
        if(userType.compareTo("admin")==0){
            showAdminData(firebaseUser);
        }else if(userType.compareTo("faculty")==0){
            showFacultyData(firebaseUser);
        }else {
            showStudentData(firebaseUser);
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(userType.compareTo("admin")==0){
                    startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
                }else if(userType.compareTo("faculty")==0){
                    startActivity(new Intent(UpdateProfileActivity.this, FacultyHomeActivity.class));
                }else {
                    startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
                }

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.compareTo("admin")==0){
                    updateAdminProfile(firebaseUser);
                }else if(userType.compareTo("faculty")==0){
                    updateFacultyProfile(firebaseUser);
                }else {
                    updateStudentProfile(firebaseUser);
                }
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
                choosePhoto.setVisibility(View.INVISIBLE);
            }
        });
    }

    //Update Faculty Profile
    public void updateFacultyProfile(FirebaseUser firebaseUser){
        userId=firebaseUser.getUid();
        Map<String, Object> map = new HashMap<>();

        map.put("fullName", editName.getText().toString());
        map.put("mobileNumber", editMobile.getText().toString());

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference uploader = storage.getReference().child("ProfileImage/" + userId);
        if(selectedImageUri != null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Image Uploading....");
            dialog.show();

            uploader.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dialog.dismiss();
                                    map.put("imageURL", uri.toString());

                                    FirebaseDatabase.getInstance().getReference().child("FacultyDetails")
                                            .child(userId).updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UpdateProfileActivity.this, FacultyHomeActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UpdateProfileActivity.this, FacultyHomeActivity.class));
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded : " + (int) percent + " %");
                        }
                    });
        }
        else {
            FirebaseDatabase.getInstance().getReference().child("FacultyDetails")
                    .child(userId).updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UpdateProfileActivity.this, FacultyHomeActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, "Failed To Update", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UpdateProfileActivity.this, FacultyHomeActivity.class));
                            finish();
                        }
                    });
        }
    }

    //Update Admin Profile
    public void updateAdminProfile(FirebaseUser firebaseUser){
        userId=firebaseUser.getUid();
        Map<String, Object> map = new HashMap<>();

        map.put("fullName", editName.getText().toString());
        map.put("mobileNumber", editMobile.getText().toString());

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference uploader = storage.getReference().child("ProfileImage/" + userId);
        if(selectedImageUri != null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Image Uploading....");
            dialog.show();

            uploader.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dialog.dismiss();
                                    map.put("imageURL", uri.toString());

                                    FirebaseDatabase.getInstance().getReference().child("Admin")
                                            .child(userId).updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded : " + (int) percent + " %");
                        }
                    });
        }
        else {
            FirebaseDatabase.getInstance().getReference().child("Admin")
                    .child(userId).updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, "Failed To Update", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
                            finish();
                        }
                    });
        }
    }

    //Update student profile
    public void updateStudentProfile(FirebaseUser firebaseUser)
    {
        userId=firebaseUser.getUid();
        Map<String, Object> map = new HashMap<>();

        map.put("fullName", editName.getText().toString());
        map.put("mobileNumber", editMobile.getText().toString());

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference uploader = storage.getReference().child("ProfileImage/" + userId);
        if(selectedImageUri != null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Image Uploading....");
            dialog.show();

            uploader.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dialog.dismiss();
                                    map.put("imageURL", uri.toString());

                                    FirebaseDatabase.getInstance().getReference().child("StudentDetails")
                                            .child(userId).updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(UpdateProfileActivity.this, "Failed to upload", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
                                                    finish();
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            dialog.setMessage("Uploaded : " + (int) percent + " %");
                        }
                    });
        }
        else {
            FirebaseDatabase.getInstance().getReference().child("StudentDetails")
                    .child(userId).updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated Successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfileActivity.this, "Failed To Update", Toast.LENGTH_LONG).show();
                            //startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
                            //finish();
                        }
                    });
        }
    }

    public void showAdminData(FirebaseUser firebaseUser){
        userId=firebaseUser.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Admin");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AdminData adminData=snapshot.getValue(AdminData.class);
                if(adminData!=null){
                    sname=adminData.getFullName();
                    smobile=adminData.getMobileNumber();
                    editName.setText(sname);
                    editMobile.setText(smobile);
                    String imgURL = adminData.getImageURL();

                    if(imgURL.compareTo("")==0 || imgURL == null){
                        //userImage.setImageResource(R.drawable.no_image);
                        Glide.with(getApplicationContext()).load(R.drawable.no_image).into(userImage);
                    }
                    else {
                        Glide.with(getApplicationContext()).load(imgURL).into(userImage);
                    }
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void  showFacultyData(FirebaseUser firebaseUser){
        userId=firebaseUser.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("FacultyDetails");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FacultyData facultyData=snapshot.getValue(FacultyData.class);
                if(facultyData!=null){
                    sname=facultyData.getFullName();
                    smobile=facultyData.getMobileNumber();
                    //spassword=studentData.getPassword();
                    String imgURL = facultyData.getImageURL();

                    editName.setText(sname);
                    editMobile.setText(smobile);

                    Glide.with(getApplicationContext()).load(imgURL).into(userImage);

                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showStudentData(FirebaseUser firebaseUser){

        userId=firebaseUser.getUid();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("StudentDetails");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentData studentData=snapshot.getValue(StudentData.class);
                if(studentData!=null){
                    sname=studentData.getFullName();
                    smobile=studentData.getMobileNumber();
                    //spassword=studentData.getPassword();
                    String imgURL = studentData.getImageURL();

                    editName.setText(sname);
                    editMobile.setText(smobile);

                    Glide.with(getApplicationContext()).load(imgURL).into(userImage);

                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    void imageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    userImage.setImageURI(selectedImageUri);
                }
            }
        }
    }

}





//****************
//                              if(userType.compareTo("admin")==0){
//                                      FirebaseDatabase.getInstance().getReference().child("Admin")
//                                      .child(userId).updateChildren(map)
//                                      .addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void unused) {
//        Toast.makeText(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
//        startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
//        finish();
//        }
//        })
//        .addOnFailureListener(new OnFailureListener() {
//@Override
//public void onFailure(@NonNull Exception e) {
//        Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
//        //startActivity(new Intent(UpdateProfileActivity.this, AdminHomeActivity.class));
//        //finish();
//        }
//        });
//
//        }else if(userType.compareTo("faculty")==0){
//        FirebaseDatabase.getInstance().getReference().child("FacultyDetails")
//        .child(userId).updateChildren(map)
//        .addOnSuccessListener(new OnSuccessListener<Void>() {
//@Override
//public void onSuccess(Void unused) {
//        Toast.makeText(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
//        startActivity(new Intent(UpdateProfileActivity.this, FacultyHomeActivity.class));
//        finish();
//        }
//        })
//
//        .addOnFailureListener(new OnFailureListener() {
//@Override
//public void onFailure(@NonNull Exception e) {
//        Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
//        //startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
//        //finish();
//        }
//        });
//        }




//package com.mca.mtechproject;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.OnProgressListener;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.bumptech.glide.Glide;
//import java.util.HashMap;
//import java.util.Map;
//
//public class UpdateProfileActivity extends AppCompatActivity {
//
//    private EditText editName, editMobile;
//    private FirebaseAuth auth;
//    private Button updateButton;
//    private ImageView userImage;
//
//    private Uri selectedImageUri;
//
//    private final int SELECT_PICTURE=100;
//
//    String sname, smobile, userId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_update_profile);
//
//        editName = findViewById(R.id.name);
//        editMobile = findViewById(R.id.phone);
//        updateButton = (Button) findViewById(R.id.btnUpdate);
//
//        userImage = (ImageView) findViewById(R.id.profileImage);
//        userImage.setVisibility(View.VISIBLE);
//
//        auth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = auth.getCurrentUser();
//
//        showData(firebaseUser);
//        updateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateStudentProfile(firebaseUser);
//
//            }
//        });
//    }
//
//    //Showing student profile
//    public void updateStudentProfile(FirebaseUser firebaseUser)
//    {
//        userId=firebaseUser.getUid();
//        Map<String, Object> map = new HashMap<>();
//
//        //
//        map.put("fullName", editName.getText().toString());
//        map.put("mobileNumber", editMobile.getText().toString());
//
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//
//        StorageReference uploader = storage.getReference().child("ProfileImage/" + userId);
//        if(selectedImageUri != null) {
//            ProgressDialog dialog = new ProgressDialog(this);
//            dialog.setTitle("Image Uploader");
//            dialog.show();
//
//            uploader.putFile(selectedImageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    dialog.dismiss();
//                                    map.put("imageURL", uri.toString());
//
//                                    FirebaseDatabase.getInstance().getReference().child("StudentDetails")
//                                            .child(userId).updateChildren(map)
//                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void unused) {
//                                                    Toast.makeText(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
//                                                    startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
//                                                    finish();
//                                                }
//                                            })
//
//                                            .addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
//                                                    startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
//                                                    finish();
//                                                }
//                                            });
//
//                                }
//                            });
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//                            dialog.setMessage("Uploaded : " + (int) percent + " %");
//                        }
//                    });
//        }
//        else {
//            FirebaseDatabase.getInstance().getReference().child("StudentDetails")
//                    .child(userId).updateChildren(map)
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            Toast.makeText(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
//                            finish();
//                        }
//                    })
//
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
//                            startActivity(new Intent(UpdateProfileActivity.this, StudentHomeActivity.class));
//                            finish();
//                        }
//                    });
//        }
//
//
//        //
//
//
//    }
//   public void showData(FirebaseUser firebaseUser){
//
//        userId=firebaseUser.getUid();
//        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("StudentDetails");
//        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener()
//        {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                StudentData studentData=snapshot.getValue(StudentData.class);
//                if(studentData!=null){
//                    sname=studentData.getFullName();
//                    smobile=studentData.getMobileNumber();
//                    String imgURL = studentData.getImageURL();
//
//                    editName.setText(sname);
//                    editMobile.setText(smobile);
//
//                    Glide.with(getApplicationContext()).load(imgURL).into(userImage);
//
//                } else
//                {
//                    Toast.makeText(UpdateProfileActivity.this, "Access Denied!", Toast.LENGTH_LONG).show();
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(UpdateProfileActivity.this, "Something Went Wrong!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//
//        userImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                imageChooser();
//            }
//        });
//    }
//
//    void imageChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                selectedImageUri = data.getData();
//                if (null != selectedImageUri) {
//                    userImage.setImageURI(selectedImageUri);
//                }
//            }
//        }
//    }
//
//}