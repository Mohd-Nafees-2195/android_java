package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class FacultyAdapterManage extends FirebaseRecyclerAdapter<FacultyData, FacultyAdapterManage.MyViewHolder>
{

    public FacultyAdapterManage(@NonNull FirebaseRecyclerOptions<FacultyData> options) {
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

        if(model.getImageURL() != null && model.getImageURL().compareTo("") != 0)
            Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);
        else
            Glide.with(holder.img.getContext()).load(R.drawable.student).into(holder.img);

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] items=new String[]{"Faculty","Faculty & Coordinator"};
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(holder.edit.getContext(),android.R.layout.simple_spinner_dropdown_item,items);

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.fullName.getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialogcontent_faculty))
                        .setExpanded(true,1100)
                        .create();


                View myview = dialogPlus.getHolderView();
                final EditText name = myview.findViewById(R.id.name);
                final EditText dept = myview.findViewById(R.id.dept);
//                final EditText email = myview.findViewById(R.id.email);
                final EditText mobile = myview.findViewById(R.id.mobile);
//                final EditText examiner1 = myview.findViewById(R.id.examin1);
//                final EditText examiner2 = myview.findViewById(R.id.examin2);
                final Spinner role = myview.findViewById(R.id.spinnerForRole);

                Button submit = myview.findViewById(R.id.ssubmit);

                name.setText(model.getFullName());
                dept.setText(model.getDepartment());
//                email.setText(model.getEmail());
                mobile.setText(model.getMobileNumber());
//                examiner1.setText(model.getExaminer1());
//                examiner2.setText(model.getExaminer2());
                role.setAdapter(adapter);
                role.setBackgroundColor(Color.WHITE);
                dialogPlus.show();
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Object> map = new HashMap<>();
                        map.put("fullName", name.getText().toString());
                        map.put("department", dept.getText().toString());
//                        map.put("email", email.getText().toString());
                        map.put("mobileNumber", mobile.getText().toString());
//                        map.put("examiner1", examiner1.getText().toString());
//                        map.put("examiner2", examiner2.getText().toString());
                        map.put("role", role.getSelectedItem().toString());

                        FirebaseDatabase.getInstance().getReference().child("FacultyDetails")
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
                builder.setTitle("Delete Faculty Record");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference("FacultyDetails")
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

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_faculty_manage,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,department,email,mobileNumber, examiner1, examiner2;
        ImageView edit, delete;
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
            edit=itemView.findViewById(R.id.edit);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
