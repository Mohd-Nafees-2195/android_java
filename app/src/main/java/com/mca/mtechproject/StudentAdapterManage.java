package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentAdapterManage extends FirebaseRecyclerAdapter<StudentData, StudentAdapterManage.MyViewHolder>
{
    public StudentAdapterManage(@NonNull FirebaseRecyclerOptions<StudentData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final StudentData model)
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

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.fullName.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialogcontent_student))
                        .setExpanded(true,1310)
                        .create();

                View myview = dialogPlus.getHolderView();
                final EditText name = myview.findViewById(R.id.sname);
                final EditText roll = myview.findViewById(R.id.rollno);
                final EditText course = myview.findViewById(R.id.course);
//                final EditText email = myview.findViewById(R.id.semail);
                final EditText mobile = myview.findViewById(R.id.smobile);
                Button submit = myview.findViewById(R.id.ssubmit);

                name.setText(model.getFullName());
                roll.setText(model.getRollNumber());
                course.setText(model.getCourse());
//                email.setText(model.getEmail());
                mobile.setText(model.getMobileNumber());

                dialogPlus.show();

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("fullName", name.getText().toString());
                        map.put("rollNumber", roll.getText().toString());
                        map.put("course", course.getText().toString());
//                        map.put("email", email.getText().toString());
                        map.put("mobileNumber", mobile.getText().toString());

                        FirebaseDatabase.getInstance().getReference().child("StudentDetails")
                                .child(getRef(position).getKey()).updateChildren(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        dialogPlus.dismiss();
                                    }
                                })

                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialogPlus.dismiss();
                                    }
                                });

                    }
                });
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.fullName.getContext());
                builder.setTitle("Delete Student Record");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference("StudentDetails")
                                .child(getRef(position).getKey()).removeValue();
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_student_manage,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,rollNumber,course,email,mobileNumber;
        ImageView edit, delete;
        CircleImageView img;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName=itemView.findViewById(R.id.displayname);
            rollNumber=itemView.findViewById(R.id.displayroll);
            course=itemView.findViewById(R.id.displaycourse);
            email=itemView.findViewById(R.id.displayemail);
            mobileNumber=itemView.findViewById(R.id.displaycontact);
            img = itemView.findViewById(R.id.ProfileImage);
            edit=itemView.findViewById(R.id.edit);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
