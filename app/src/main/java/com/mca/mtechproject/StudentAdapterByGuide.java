package com.mca.mtechproject;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.type.Color;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAdapterByGuide extends FirebaseRecyclerAdapter<StudentData, StudentAdapterByGuide.MyViewHolder> {
    private Context context;
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public StudentAdapterByGuide(@NonNull FirebaseRecyclerOptions<StudentData> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull final StudentAdapterByGuide.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final StudentData model)
    {
            holder.fullName.setText(model.getFullName());
            holder.rollNumber.setText(model.getRollNumber());
            holder.course.setText(model.getCourse());
            holder.email.setText(model.getEmail());
            holder.mobileNumber.setText(model.getMobileNumber());

            if(model.getImageURL() != null && model.getImageURL().compareTo("") != 0)
                Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);
            else
                Glide.with(holder.img.getContext()).load(R.drawable.student).into(holder.img);

        holder.docsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start activity to view all pdf of those student who are working under those  guide

                Intent intent=new Intent(holder.docsButton.getContext(),FetchStudentDocsByGuideActivity.class);
                String studentId=getRef(position).getKey().toString();
                intent.putExtra("studentId",studentId);
                holder.docsButton.getContext().startActivity(intent);
            }
        });

        holder.examiner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.examiner.getContext(),ExaminersActivity.class);
                String studentId=getRef(position).getKey();
                intent.putExtra("studentIdForExaminer",studentId);
                holder.examiner.getContext().startActivity(intent);
            }
        });
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.fullName.getContext());
                builder.setTitle("Remove Student");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Write code to remove the information of guide and examiner from student details
                        model.setGuideEmail("");
                        model.setExaminer1Email("");
                        model.setExaminer2Email("");

                        FirebaseDatabase.getInstance().getReference().child("StudentDetails").child(getRef(position).getKey()).setValue(model)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //show success diloag
                                        holder.removeButton.setBackgroundColor(Color.GREEN_FIELD_NUMBER);
                                       // holder.removeButton.getContext().startActivity(new Intent(holder.removeButton.getContext(),StudentFetchDataByGuideActivity.class));
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
        });

    }

    @NonNull
    @Override
    public StudentAdapterByGuide.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_single_row_for_guide,parent,false);
        return new MyViewHolder(view);
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,rollNumber,course,email,mobileNumber;
        Button docsButton,removeButton,examiner;
        CircleImageView img;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName=itemView.findViewById(R.id.displayNameForGuid);
            rollNumber=itemView.findViewById(R.id.displayRollForGuide);
            course=itemView.findViewById(R.id.displayCourseForGuide);
            email=itemView.findViewById(R.id.displayEmailForGuide);
            mobileNumber=itemView.findViewById(R.id.displayContactForGuide);
            docsButton=itemView.findViewById(R.id.docsButtonForGuide);
            removeButton=itemView.findViewById(R.id.deleteButtonForGuide);
            examiner=itemView.findViewById(R.id.examinerButtonForGuide);
            img=itemView.findViewById(R.id.ProfileImage);
        }
    }

}
