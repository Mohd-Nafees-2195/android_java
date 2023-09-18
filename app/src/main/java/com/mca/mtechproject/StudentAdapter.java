package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.type.Color;

public class StudentAdapter extends FirebaseRecyclerAdapter<StudentData, StudentAdapter.MyViewHolder>
{
    public StudentAdapter(@NonNull FirebaseRecyclerOptions<StudentData> options) {
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

        if(model.getGuideEmail().compareTo("")!=0){
            holder.accept.setBackgroundColor(Color.RED_FIELD_NUMBER);
            holder.accept.setText("ACCEPTED");
        }

        if(model.getImageURL() != null && model.getImageURL().compareTo("") !=0)
            Glide.with(holder.img.getContext()).load(model.getImageURL()).into(holder.img);
        else
            Glide.with(holder.img.getContext()).load(R.drawable.student).into(holder.img);

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.getGuideEmail().compareTo("")==0){
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                //Fetching faculty Details
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = firebaseUser.getUid();
                DatabaseReference databaseReferenceOfFaculty = firebaseDatabase.getReference("FacultyDetails").child(userId);
                databaseReferenceOfFaculty.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        FacultyData data = dataSnapshot.getValue(FacultyData.class);
                        String guidEmail = data.getEmail();
//                        String examiner1Email = data.getExaminer1();
//                        String examiner2Email = data.getExaminer2();


                        //Creating Dialoag box
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.fullName.getContext());
                        builder.setTitle("Accept Student");
                        builder.setMessage("Are you sure?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //setting emails of guide and examiners
                                model.setGuideEmail(guidEmail);
//                                model.setExaminer1Email(examiner1Email);
//                                model.setExaminer2Email(examiner2Email);

                            FirebaseDatabase.getInstance().getReference().child("StudentDetails").child(getRef(position).getKey()).setValue(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //show success diloag
                                            holder.accept.setBackgroundColor(Color.GREEN_FIELD_NUMBER);
//                                            Intent intent=new Intent(holder.accept.getContext(),StudentFetchDataActivity.class);
//                                            intent.putExtra("RoleTypeToFetchStuData","faculty");
//                                            holder.accept.getContext().startActivity(intent);
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
          }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_select_student,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,rollNumber,course,email,mobileNumber;
        Button accept;
        ImageView img;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName=itemView.findViewById(R.id.displayname);
            rollNumber=itemView.findViewById(R.id.displayroll);
            course=itemView.findViewById(R.id.displaycourse);
            email=itemView.findViewById(R.id.displayemail);
            mobileNumber=itemView.findViewById(R.id.displaycontact);
            accept=itemView.findViewById(R.id.acceptButton);
            img=itemView.findViewById(R.id.ProfileImage);
        }
    }
}
