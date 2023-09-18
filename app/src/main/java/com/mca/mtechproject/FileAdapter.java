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

public class FileAdapter extends FirebaseRecyclerAdapter<FileModel,FileAdapter.MyViewHolder>
{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FileAdapter(@NonNull FirebaseRecyclerOptions<FileModel> options) {
        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull final FileAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final FileModel model) {
        holder.fileName.setText(model.getFileName());
        holder.message.setText(model.getMessage());

        holder.img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.img1.getContext(), Viewpdf.class);
                intent.putExtra("fileName", model.getFileName());
                intent.putExtra("fileUrl", model.getFileUrl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.img1.getContext().startActivity(intent);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.fileName.getContext());
                builder.setTitle("Delete File Record");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String userId=getRef(position).getParent().getKey();
                        FirebaseDatabase.getInstance().getReference("uploads").child(userId)
                                .child(getRef(position).getKey()).removeValue();
                        holder.delete.getContext().startActivity(new Intent(holder.delete.getContext(), StudentFilesActivity.class));
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

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(model.getFileUrl() + ""));
                request.setTitle(model.getFileName());
                request.setMimeType("application/pdf");
                request.allowScanningByMediaScanner();
                request.setAllowedOverMetered(true);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, model.getFileName());
                DownloadManager dm = (DownloadManager) holder.download.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(holder.download.getContext(), "Downloading Please Wait...", Toast.LENGTH_LONG).show();
            }
        });
    }

    @NonNull
    @Override
    public FileAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowpdf,parent,false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView img1;
        TextView fileName,message;
        Button delete, download;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img1 = itemView.findViewById(R.id.pdfview);
            fileName = itemView.findViewById(R.id.filename);
            message=itemView.findViewById(R.id.messageForStudent1);
            delete=itemView.findViewById(R.id.delete);
            download=itemView.findViewById(R.id.download);
        }
    }
}