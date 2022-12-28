package com.example.bekind_v2.DataLayer;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MyCallback;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLoginRepository {
    public static void login(Context context, String email, String password, MyCallback<Boolean> myCallback){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete())
                    myCallback.onCallback(task.isSuccessful());
                    //add here email verification
            }
        });
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static void register(Context context, String email, String password, MyCallback<Boolean> myCallback){
        if(!email.isEmpty() && !password.isEmpty())
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    myCallback.onCallback(task.isSuccessful());
                    //add here email verification
                }
            });
    }

    public static void updateCredentials(String email, String oldPassword, String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (email != null) {
            Log.e("UPDATE", "before email: "+email);
            user.updateEmail(email);
            Log.e("UPDATE", "after email: "+email);
            Log.e("UPDATE", "get email: "+user.getEmail());
        }
        if (newPassword != null)
            user.updatePassword(newPassword); //TODO: add check for oldPassword.equals(storedPassword)
    }

    public static boolean isLogged(){
        return (FirebaseAuth.getInstance().getCurrentUser() != null);
    }

    public static String getEmail(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static String getUid(){
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
