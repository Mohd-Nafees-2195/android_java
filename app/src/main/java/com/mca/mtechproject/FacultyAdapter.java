package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class FacultyAdapter extends FirebaseRecyclerAdapter<FacultyData, FacultyAdapter.MyViewHolder>
{
    public FacultyAdapter(@NonNull FirebaseRecyclerOptions<FacultyData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final FacultyData model)
    {
        holder.fullName.setText(model.getFullName());
        holder.department.setText(model.getDepartment());
        holder.email.setText(model.getEmail());
        holder.mobileNumber.setText(model.getMobileNumber());
//        holder.examiner1.setText(model.getExaminer1());
//        holder.examiner2.setText(model.getExaminer2());

        if(model.getImageURL() != null && model.getImageURL().compareTo("")!=0)
            Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);
        else
            Glide.with(holder.img.getContext()).load(R.drawable.student);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_faculty,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,department,email,mobileNumber;// examiner1, examiner2;
        CircleImageView img;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName=itemView.findViewById(R.id.displayname);
            department=itemView.findViewById(R.id.displaydept);
            email=itemView.findViewById(R.id.displayemail);
            mobileNumber=itemView.findViewById(R.id.displaycontact);
//            examiner1=itemView.findViewById(R.id.displayexaminer1);
//            examiner2=itemView.findViewById(R.id.displayexaminer2);
            img=itemView.findViewById(R.id.ProfileImage);

        }
    }
}
