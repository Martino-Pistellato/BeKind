package com.example.bekind_v2.DataLayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLoginRepository {
    private FirebaseAuth mAuth;

    public UserLoginRepository(){
        mAuth = FirebaseAuth.getInstance();
    }

    public void login(String email, String password){
        if(!email.isEmpty() && !password.isEmpty())
            mAuth.signInWithEmailAndPassword(email, password); //there isn't a onCompleteListener
    }

    public void logout(){
        mAuth.signOut();
    }

    public void register(String email, String password){
        if(!email.isEmpty() && !password.isEmpty())
            mAuth.createUserWithEmailAndPassword(email, password);
    }

    public void updateCredentials(String email, String oldPassword, String newPassword){
        FirebaseUser user = mAuth.getCurrentUser();
        if (email != null)
            user.updateEmail(email);
        if (newPassword != null)
            user.updatePassword(newPassword); //TODO: add check for oldPassword.equals(storedPassword)
    }

    public boolean isLogged(){
        return (mAuth.getCurrentUser() != null);
    }

    public String getEmail(){
        return mAuth.getCurrentUser().getEmail();
    }

    public String getUid(){
        return mAuth.getCurrentUser().getUid();
    }
}
