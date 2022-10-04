package com.celes.lnctassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button login;
    EditText username,password;
    String emailPattern="^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    //String enrollPattern="[0-9]{4}+[A-Z]{2}+[0-9]{6}+";
    TextView textView, signUP_resend, forgot_msg;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    //FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner=findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.job, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        //Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        login=findViewById(R.id.login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        textView=findViewById(R.id.textView2);
        signUP_resend=findViewById(R.id.signup);
        forgot_msg=findViewById(R.id.forgot_msg);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        //mUser=mAuth.getCurrentUser();
        if(text.equals("Student")){
            //Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show();
            //username.setHint("Enrollment No.");
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(MainActivity.this, "Login for User", Toast.LENGTH_SHORT).show();
                    performUserLogin();
                }
            });
        }
        else{
            //Toast.makeText(this, "Faculty", Toast.LENGTH_SHORT).show();
            //username.setHint("Username");
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(MainActivity.this, "Login for Authority", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void performUserLogin() {
        String emailID=username.getText().toString().trim();
        String pass=password.getText().toString();

        if(!emailID.matches(emailPattern)){
            username.setError("Invalid Email");
            username.requestFocus();
        }
        else if(pass.isEmpty() || pass.length()<7){
            password.setError("Enter a valid password");
            Toast.makeText(this, "LOL", Toast.LENGTH_SHORT).show();
            password.requestFocus();
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.setTitle("Logging In");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(emailID, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(MainActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                            tostuMain();
                        }
                        else{
                            username.setError("Please verify your email");
                            username.requestFocus();
                            textView.setText("Not received verification mail? ");
                            forgot_msg.setText(R.string.verifyHelp);
//                            forgot_msg.setOnClickListener(null);
                            signUP_resend.setText(R.string.resend);
                            signUP_resend.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this, "Verification Email Sent", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Can't send verification email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void tostuMain() {
        Intent intent=new Intent(this,studentMain.class);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void signUp(View view){
        //Toast.makeText(this, "lol", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(this, signUp.class);
        startActivity(intent);
    }
    public void forgetPassword(View view){
        //Toast.makeText(this, "Working?", Toast.LENGTH_SHORT).show();
        EditText resetMail = new EditText(view.getContext());
        resetMail.setHint("Registered email id");
        AlertDialog.Builder passwordresetDialog = new AlertDialog.Builder(view.getContext());
        passwordresetDialog.setTitle("Reset Password?");
        passwordresetDialog.setMessage("Enter your registered email");
        passwordresetDialog.setView(resetMail);
        passwordresetDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String mail=resetMail.getText().toString();
                if(!(mail.matches(emailPattern))){
                    resetMail.setError("Enter a valid email");
                    resetMail.requestFocus();
                }
                mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Try Again after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        passwordresetDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        passwordresetDialog.create().show();

    }
}