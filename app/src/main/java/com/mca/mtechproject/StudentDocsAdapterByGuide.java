package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class StudentDocsAdapterByGuide extends FirebaseRecyclerAdapter<FileModel, StudentDocsAdapterByGuide.MyViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public StudentDocsAdapterByGuide(@NonNull FirebaseRecyclerOptions<FileModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final StudentDocsAdapterByGuide.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final FileModel model)
    {
        holder.pdfName.setText(model.getFileName());
        holder.pdfView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.pdfView.getContext(), Viewpdf.class);
                intent.putExtra("fileName", model.getFileName());
                intent.putExtra("fileUrl", model.getFileUrl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.pdfView.getContext().startActivity(intent);
            }
        });

        String studentId=getRef(position).getParent().getKey();
        if(model.getVisibility()>=1){
            holder.verifyButton.setBackgroundColor(android.graphics.Color.GREEN);
            holder.verifyButton.setText("Verified");
            holder.verifyButton.setTextColor(android.graphics.Color.WHITE);
        }
        if(model.getMessage().compareTo("Rejected By Guide")==0){
            holder.rejectButton.setBackgroundColor(Color.RED);
            holder.rejectButton.setText("REJECTED");
            holder.rejectButton.setTextColor(android.graphics.Color.WHITE);
        }
        holder.verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getVisibility()==0&&model.getMessage().compareTo("Not Verified")==0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.pdfName.getContext());
                    builder.setTitle("Verify Student");
                    builder.setMessage("Are you sure?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            model.setVisibility(1);
                            model.setMessage("Verified By Guide");
                            FirebaseDatabase.getInstance().getReference("uploads").child(studentId).child(getRef(position).getKey())
                            .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            holder.verifyButton.setBackgroundColor(android.graphics.Color.GREEN);
                                            holder.verifyButton.setText("Verified");
                                            holder.verifyButton.setTextColor(android.graphics.Color.WHITE);
                                            Intent intent=new Intent(holder.verifyButton.getContext(),FetchStudentDocsByGuideActivity.class);
                                            intent.putExtra("studentId",studentId);
                                            holder.verifyButton.getContext().startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });

        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getVisibility()==0&&model.getMessage().compareTo("Not Verified")==0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.pdfName.getContext());
                    builder.setTitle("Reject Docs");
                    builder.setMessage("Are you sure?");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // model.setVisibility(0);
                            model.setMessage("Rejected By Guide");
                            FirebaseDatabase.getInstance().getReference("uploads").child(studentId).child(getRef(position).getKey())
                                    .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            holder.rejectButton.setBackgroundColor(Color.RED);
                                            holder.rejectButton.setText("REJECTED");
                                            holder.rejectButton.setTextColor(android.graphics.Color.WHITE);
                                            String studentId=getRef(position).getParent().getKey();
                                            Intent intent=new Intent(holder.rejectButton.getContext(),FetchStudentDocsByGuideActivity.class);
                                            intent.putExtra("studentId",studentId);
                                            holder.rejectButton.getContext().startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();
                }
            }
        });

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(model.getFileUrl() + ""));
                request.setTitle(model.getFileName());
                request.setMimeType("application/pdf");
                request.allowScanningByMediaScanner();
                request.setAllowedOverMetered(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, model.getFileName());
                DownloadManager dm = (DownloadManager) holder.downloadButton.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(holder.downloadButton.getContext(), "Downloading Please Wait...", Toast.LENGTH_LONG).show();
            }
        });

    }

    @NonNull
    @Override
    public StudentDocsAdapterByGuide.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_for_fetch_student_docs,parent,false);
        return new MyViewHolder(view);
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pdfName;
        Button verifyButton, rejectButton;// downloadButton;
        ImageView pdfView,downloadButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfName=itemView.findViewById(R.id.fileNameInVerification);
            pdfView=itemView.findViewById(R.id.pdfViewInGuideVerification);
            rejectButton=itemView.findViewById(R.id.cancelDocsByGuideButton);
            verifyButton=itemView.findViewById(R.id.verifyDocsByGuideButton);
            downloadButton=itemView.findViewById(R.id.download);
        }
    }
}
