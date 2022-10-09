package com.celes.lnctassist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class homeFragment extends Fragment {
    View view;
    Spinner spinner;
    EditText subject, complaint, suggestion;
    TextView subCount, compCount, suggCount;
    Button submit;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    TextView uploadImg;
    ImageView compImg;
    Uri imageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    boolean imgUp=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        spinner = view.findViewById(R.id.selectComp);
        subject=view.findViewById(R.id.compSub);
        complaint=view.findViewById(R.id.complaint);
        suggestion=view.findViewById(R.id.compSugg);
        subCount=view.findViewById(R.id.subCount);
        compCount=view.findViewById(R.id.compCount);
        suggCount=view.findViewById(R.id.suggCount);
        submit=view.findViewById(R.id.submitComp);
        uploadImg=view.findViewById(R.id.uploadImg);
        compImg=view.findViewById(R.id.compImg);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressDialog=new ProgressDialog(getActivity());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.compType, R.layout.spinnerhome);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text=adapterView.getItemAtPosition(i).toString();

                subject.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String sub = subject.getText().toString();
                        int num=100-(sub.length());
                        if(num>0){
                            String temp=Integer.toString(num);
                            subCount.setText(temp);
                        }
                        else{
                            subject.setError("characters limit exceeded");
                            subject.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                complaint.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String com = complaint.getText().toString();
                        int num=1000-(com.length());
                        if(num>0){
                            String temp=Integer.toString(num);
                            compCount.setText(temp);
                        }
                        else{
                            complaint.setError("characters limit exceeded");
                            complaint.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                suggestion.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String sug = suggestion.getText().toString();
                        int num=500-(sug.length());
                        if(num>0){
                            String temp=Integer.toString(num);
                            suggCount.setText(temp);
                        }
                        else{
                            suggestion.setError("characters limit exceeded");
                            suggestion.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });


                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        regComp(text);
                    }
                });

                uploadImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        choosePicture();
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

    private void choosePicture() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==Activity.RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            compImg.setImageURI(imageUri);
            imgUp=true;
        }
        else{
            Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadPicture(String text) {
        progressDialog.setMessage("Please wait..");
        progressDialog.setTitle("Submitting");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        String randomKey = UUID.randomUUID().toString();
        StorageReference strRef = storageReference.child("imagesComp/" + randomKey);
        strRef.putFile(imageUri)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference("Complaints").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(text).child("compImg").setValue(randomKey);
                            progressDialog.dismiss();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void regComp(String text) {
        String subj = subject.getText().toString().trim();
        String compl = complaint.getText().toString().trim();
        String sugge = suggestion.getText().toString().trim();
        String checkImg = uploadImg.getText().toString();
        String checkImgTemp = String.valueOf(R.id.uploadImg);
        databaseReference= FirebaseDatabase.getInstance().getReference("Complaints");

        if(subj.isEmpty()){
            subject.setError("Subject can't be empty");
            subject.requestFocus();
        }
        else if(compl.isEmpty()){
            complaint.setError("Please write the issue");
            complaint.requestFocus();
        }
        else if(!imgUp){
            uploadImg.setError("Please upload a picture");
            uploadImg.requestFocus();
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.setTitle("Submitting");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            Complaints complaints=new Complaints(subj, compl, sugge);
            FirebaseDatabase.getInstance().getReference("Complaints").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(text).setValue(complaints).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getContext(), "Complaint registered successfully", Toast.LENGTH_SHORT).show();
                                uploadPicture(text);
                                progressDialog.dismiss();
                            }
                            else{
                                Toast.makeText(getContext(), "Try again later", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }


    }
}