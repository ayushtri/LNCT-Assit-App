package com.celes.lnctassist;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

public class stayLoggedIn extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if((FirebaseAuth.getInstance().getCurrentUser())!=null){
            Intent intent=new Intent(this, studentMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
