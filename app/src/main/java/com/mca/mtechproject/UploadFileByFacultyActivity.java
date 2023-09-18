package com.mca.mtechproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class UploadFileByFacultyActivity extends AppCompatActivity {
    ImageView selectFile,upload,fileLogo,cancleFile, backArrow;
    Uri fileUri;
    TextView fileTitle, title;
    FirebaseStorage storage;
    FirebaseDatabase db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file_by_faculty);


        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        storage=FirebaseStorage.getInstance();//Returns an object of Firebase Storage
        db = FirebaseDatabase.getInstance();  // Returns an object of Firebase database

        fileTitle=findViewById(R.id.fileTitleByFaculty);

        selectFile=findViewById(R.id.selectFileByFaculty);
        upload=findViewById(R.id.uploadFileByFaculty);

        fileLogo=findViewById(R.id.fileLogoByFaculty);
        cancleFile=findViewById(R.id.cancelFileByFaculty);

        fileLogo.setVisibility(View.INVISIBLE);
        cancleFile.setVisibility(View.INVISIBLE);

        title = findViewById(R.id.toolbar_title);
        backArrow = findViewById(R.id.left_arrow);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadFileByFacultyActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        title.setText("Upload Notice");

        cancleFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileLogo.setVisibility(View.INVISIBLE);
                cancleFile.setVisibility(View.INVISIBLE);
                selectFile.setVisibility(View.VISIBLE);
                fileTitle.setText("NO File Selected");
                fileUri=null;
            }
        });


        //Code for Select File Button
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent=new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);//to fetch files
                                startActivityForResult(Intent.createChooser(intent, "Select Pdf Files"),86);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                                Toast.makeText(UploadFileByFacultyActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        //Code for Upload Button
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fileUri!=null)
                    uploadFile(fileUri,firebaseUser);
                else
                {
                    Toast.makeText(UploadFileByFacultyActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check user has selected a file or not
        if(requestCode==86 && resultCode==RESULT_OK && data!=null)
        {
            fileUri=data.getData();//Returns the uri of selected file
            fileTitle.setText("");
            fileTitle.setText(data.getData().getLastPathSegment());
            fileLogo.setVisibility(View.VISIBLE);
            cancleFile.setVisibility(View.VISIBLE);
            selectFile.setVisibility(View.INVISIBLE);
        }
        else
        {
            Toast.makeText(UploadFileByFacultyActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
        }
    }

    private  void uploadFile(Uri fileUri,FirebaseUser firebaseUser)
    {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setTitle("Uploading File...");
        pd.setProgress(0);
        pd.show();
        String fileName=System.currentTimeMillis()+".pdf";
        String userId=firebaseUser.getUid();

        StorageReference storageReference=storage.getReference();
        final StorageReference reference=storageReference.child("Uploads/"+fileName);
        reference.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Toast.makeText(UploadFileActivity.this, "XXXX"+userEmail1, Toast.LENGTH_SHORT).show();
                                String uploadUser=getIntent().getStringExtra("UploadUser");

                                FileModel obj;
                                if(uploadUser.compareTo("Guide")==0){
                                     obj=new FileModel(fileTitle.getText().toString(),uri.toString(),"Uploaded By Guide",0);
                                }else if(uploadUser.compareTo("Coordinator")==0){
                                     obj=new FileModel(fileTitle.getText().toString(),uri.toString(),"Uploaded By Coordinator",0);
                                } else{
                                     obj=new FileModel(fileTitle.getText().toString(),uri.toString(),"Uploaded By Examiner",0);
                                }

                                if(uploadUser.compareTo("Guide")==0){
                                    DatabaseReference databaseReference=db.getReference("UploadFilesByGuide");
                                    databaseReference.child(userId).child(databaseReference.push().getKey()).setValue(obj);
                                } else if(uploadUser.compareTo("Coordinator")==0){
                                    DatabaseReference databaseReference=db.getReference("uploads");
                                    databaseReference.child(userId).child(databaseReference.push().getKey()).setValue(obj);
                                }
                                else{
                                    DatabaseReference databaseReference=db.getReference("UploadFilesByExaminer");
                                    databaseReference.child(userId).child(databaseReference.push().getKey()).setValue(obj);
                                }


                                pd.dismiss();
                                Toast.makeText(UploadFileByFacultyActivity.this, "File Successfully Uploaded", Toast.LENGTH_SHORT).show();

                                fileLogo.setVisibility(View.INVISIBLE);
                                cancleFile.setVisibility(View.INVISIBLE);
                                selectFile.setVisibility(View.VISIBLE);
                                fileTitle.setText("");

                                if(uploadUser.compareTo("Guide")==0){
                                    Intent intent=new Intent(UploadFileByFacultyActivity.this,FetchAnnounceDocsByGuideActivity.class);
                                    //intent.putExtra("AnnouncementUser","Guide");
                                    startActivity(intent);
                                }else if (uploadUser.compareTo("Examiner")==0){
                                    Intent intent=new Intent(UploadFileByFacultyActivity.this,FetchAnnounceDocsByExaminerActivity.class);
                                    //intent.putExtra("AnnouncementUser","Examiner");
                                    startActivity(intent);
                                }
                                else {
                                    Intent intent=new Intent(UploadFileByFacultyActivity.this,FetchAnnounceDocsByCoordinatorActivity.class);
                                    //intent.putExtra("AnnouncementUser","Examiner");
                                    startActivity(intent);
                                }
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadFileByFacultyActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        int currentProgress=(int) (100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                        pd.setProgress(currentProgress);
                    }
                });

    }


    //Handling when Permission Results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==9 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            selectPDF();
        }
        else
        {
            Toast.makeText(UploadFileByFacultyActivity.this, "Please Provide Permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPDF() {
        //To offer user to select a file using file manager
        //we will be using an Intent
        Intent intent=new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);//to fetch files
        startActivityForResult(intent,86);
    }
}