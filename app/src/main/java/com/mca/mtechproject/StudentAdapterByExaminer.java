package com.mca.mtechproject;

import android.annotation.SuppressLint;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAdapterByExaminer extends FirebaseRecyclerAdapter<StudentData, StudentAdapterByExaminer.MyViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public StudentAdapterByExaminer(@NonNull FirebaseRecyclerOptions<StudentData> options) {
        super(options);

    }
    @Override
    protected void onBindViewHolder(@NonNull final StudentAdapterByExaminer.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final StudentData model)
    {

        holder.fullName.setText(model.getFullName());
        holder.rollNumber.setText(model.getRollNumber());
        holder.course.setText(model.getCourse());
        holder.email.setText(model.getEmail());
        holder.mobileNumber.setText(model.getMobileNumber());

        holder.docsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start activity to view all pdf of those student who are working under those  guide
                Intent intent=new Intent(holder.docsButton.getContext(),FetchStudentDocsByExaminerActivity.class);
                String studentId=getRef(position).getKey().toString();
                intent.putExtra("studentId",studentId);
                holder.docsButton.getContext().startActivity(intent);
            }
        });

        if(model.getImageURL() != null && model.getImageURL().compareTo("") !=0)
            Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);
        else
            Glide.with(holder.img.getContext()).load(R.drawable.student).into(holder.img);

    }

    @NonNull
    @Override
    public StudentAdapterByExaminer.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.student_single_row_for_examiner,parent,false);
        return new MyViewHolder(view);
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,rollNumber,course,email,mobileNumber;
        Button docsButton;
        CircleImageView img;


        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName=itemView.findViewById(R.id.displayNameForExaminer);
            rollNumber=itemView.findViewById(R.id.displayRollForExaminer);
            course=itemView.findViewById(R.id.displayCourseForExaminer);
            email=itemView.findViewById(R.id.displayEmailForExaminer);
            mobileNumber=itemView.findViewById(R.id.displayContactForExaminer);
            img = itemView.findViewById(R.id.ProfileImage);
            docsButton=itemView.findViewById(R.id.docsButtonForExaminer);
        }
    }
}
