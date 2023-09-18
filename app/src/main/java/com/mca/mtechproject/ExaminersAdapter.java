package com.mca.mtechproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExaminersAdapter extends FirebaseRecyclerAdapter<FacultyData, ExaminersAdapter.MyViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ExaminersAdapter(@NonNull FirebaseRecyclerOptions<FacultyData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ExaminersAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") final int position, @NonNull final FacultyData model)
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

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = holder.add.getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                String studentId = sharedPreferences.getString("Id","");
                //Toast.makeText(holder.add.getContext(), studentId, Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference("StudentDetails").child(studentId).get()
                        .addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                StudentData data=dataSnapshot.getValue(StudentData.class);
                                if(data.getExaminer1Email().compareTo("")==0){
                                    data.setExaminer1Email(model.getEmail());
                                    FirebaseDatabase.getInstance().getReference("StudentDetails").child(studentId).setValue(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(holder.add.getContext(), "Examiner added successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else if(data.getExaminer2Email().compareTo("")==0){
                                    data.setExaminer2Email(model.getEmail());
                                    FirebaseDatabase.getInstance().getReference("StudentDetails").child(studentId).setValue(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(holder.add.getContext(), "Examiner added successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else{
                                    Toast.makeText(holder.add.getContext(), "Already have examiners", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        });
    }

    @NonNull
    @Override
    public ExaminersAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_set_examiners,parent,false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView fullName,department,email,mobileNumber;// examiner1, examiner2;
        Button add;
        CircleImageView img;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            fullName=itemView.findViewById(R.id.displayname);
            department=itemView.findViewById(R.id.displaydept);
            email=itemView.findViewById(R.id.displayemail);
            mobileNumber=itemView.findViewById(R.id.displaycontact);
            add=itemView.findViewById(R.id.add);
//            examiner1=itemView.findViewById(R.id.displayexaminer1);
//            examiner2=itemView.findViewById(R.id.displayexaminer2);
            img=itemView.findViewById(R.id.ProfileImage);

        }
    }

}
