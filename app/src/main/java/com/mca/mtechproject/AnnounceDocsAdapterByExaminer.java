package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;

public class AnnounceDocsAdapterByExaminer extends FirebaseRecyclerAdapter<FileModel,AnnounceDocsAdapterByExaminer.MyViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AnnounceDocsAdapterByExaminer(@NonNull FirebaseRecyclerOptions<FileModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final AnnounceDocsAdapterByExaminer.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final FileModel model) {
        holder.pdfNameInFaculty.setText(model.getFileName());
        holder.imgViewByFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.imgViewByFaculty.getContext(), Viewpdf.class);
                intent.putExtra("fileName", model.getFileName());
                intent.putExtra("fileUrl", model.getFileUrl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.imgViewByFaculty.getContext().startActivity(intent);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.pdfNameInFaculty.getContext());
                builder.setTitle("Delete File Record");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String userId=getRef(position).getParent().getKey();
                        FirebaseDatabase.getInstance().getReference("UploadFilesByExaminer").child(userId)
                                .child(getRef(position).getKey()).removeValue();
                        holder.deleteButton.getContext().startActivity(new Intent(holder.deleteButton.getContext(),FetchAnnounceDocsByExaminerActivity.class));
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
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
    public AnnounceDocsAdapterByExaminer.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_announce_docs_for_faculty,parent,false);
        return new MyViewHolder(view);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imgViewByFaculty;
        TextView pdfNameInFaculty;
        Button deleteButton, downloadButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewByFaculty=itemView.findViewById(R.id.pdfViewInFaculty);
            pdfNameInFaculty=itemView.findViewById(R.id.fileNameInFaculty);
            deleteButton=itemView.findViewById(R.id.deleteDocsByFacultyButton);
            downloadButton=itemView.findViewById(R.id.download);
        }
    }
}
