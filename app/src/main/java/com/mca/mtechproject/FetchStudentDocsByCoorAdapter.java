package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FetchStudentDocsByCoorAdapter extends FirebaseRecyclerAdapter<FileModel, FetchStudentDocsByCoorAdapter.MyViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FetchStudentDocsByCoorAdapter(@NonNull FirebaseRecyclerOptions<FileModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FetchStudentDocsByCoorAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final FileModel model)
    {
        holder.pdfName.setText(model.getFileName());
        holder.status.setText(model.getMessage());
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
    }

    @NonNull
    @Override
    public FetchStudentDocsByCoorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_for_coor_for_student_docs,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView pdfName,status;
        ImageView pdfView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfName=itemView.findViewById(R.id.fileNameInVerification);
            pdfView=itemView.findViewById(R.id.pdfViewInGuideVerification);
            status=itemView.findViewById(R.id.status);
        }
    }
}