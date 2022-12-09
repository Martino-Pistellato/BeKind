package com.example.bekind_v2.DataLayer;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserLoginRepository {
    public static void login(String email, String password){
        if(!email.isEmpty() && !password.isEmpty())
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password); //there isn't a onCompleteListener
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static void register(String email, String password){
        if(!email.isEmpty() && !password.isEmpty())
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password);
    }

    public static void updateCredentials(String email, String oldPassword, String newPassword){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (email != null)
            user.updateEmail(email);
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
