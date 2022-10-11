package com.celes.lnctassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class stuCompActivity extends AppCompatActivity {
    TextView subComp, comp, sugges;
    ImageView compImgView;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_comp);

        Intent intent = getIntent();
        String compID = intent.getStringExtra("key");
        String compType = intent.getStringExtra("comtype");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(stuCompActivity.this, studentMain.class);
                startActivity(i);
            }
        });

        subComp=findViewById(R.id.subComp);
        comp=findViewById(R.id.comp);
        sugges=findViewById(R.id.sugges);
        compImgView=findViewById(R.id.compImgView);
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
        databaseReference.child(compType).child(compID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String subject = String.valueOf(snapshot.child("subject").getValue());
                    String complaint = String.valueOf(snapshot.child("complaint").getValue());
                    String suggestion = String.valueOf(snapshot.child("suggestions").getValue());
                    String compImg = String.valueOf(snapshot.child("compImg").getValue());
                    subComp.setText(subject);
                    comp.setText(complaint);
                    sugges.setText(suggestion);

                    storageReference= FirebaseStorage.getInstance().getReference().child("imagesComp/"+compImg);
                    try{
                        final File localFile = File.createTempFile(compImg, "jpg");
                        storageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                if(task.isSuccessful()){
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    compImgView.setImageBitmap(bitmap);
                                }
                                else{
                                    Toast.makeText(stuCompActivity.this, "Error in loading image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(stuCompActivity.this, "Try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}