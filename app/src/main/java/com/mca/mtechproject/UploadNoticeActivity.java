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

public class UploadNoticeActivity extends AppCompatActivity {
    ImageView selectFile,upload,fileLogo,cancleFile;
    Uri fileUri;
    TextView fileTitle;
    FirebaseStorage storage;
    FirebaseDatabase db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_notice);


        auth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        storage=FirebaseStorage.getInstance();//Returns an object of Firebase Storage
        db = FirebaseDatabase.getInstance();  // Returns an object of Firebase database
        fileTitle=findViewById(R.id.fileTitleForNotice);

        selectFile=findViewById(R.id.selectFileForNotice);
        upload=findViewById(R.id.uploadFileForNotice);

        fileLogo=findViewById(R.id.fileLogoForNotice);
        cancleFile=findViewById(R.id.cancelFileForNotice);

        fileLogo.setVisibility(View.INVISIBLE);
        cancleFile.setVisibility(View.INVISIBLE);

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

                                Toast.makeText(UploadNoticeActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(UploadNoticeActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UploadNoticeActivity.this, "Please Select a file", Toast.LENGTH_SHORT).show();
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
                                FileModel obj=new FileModel(fileTitle.getText().toString(),uri.toString(),"New Notice By Guide",0);

                                //Toast.makeText(UploadFileActivity.this, "XXXX"+userEmail1, Toast.LENGTH_SHORT).show();
                                DatabaseReference databaseReference=db.getReference("uploadNotices");
                                databaseReference.child(userId).child(databaseReference.push().getKey()).setValue(obj);

                                pd.dismiss();
                                Toast.makeText(UploadNoticeActivity.this, "File Successfully Uploaded", Toast.LENGTH_SHORT).show();

                                fileLogo.setVisibility(View.INVISIBLE);
                                cancleFile.setVisibility(View.INVISIBLE);
                                selectFile.setVisibility(View.VISIBLE);
                                fileTitle.setText("");

                                startActivity(new Intent(UploadNoticeActivity.this,StudentFetchDataByGuideActivity.class));
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadNoticeActivity.this, "Uploading Failed", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(UploadNoticeActivity.this, "Please Provide Permission", Toast.LENGTH_SHORT).show();
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