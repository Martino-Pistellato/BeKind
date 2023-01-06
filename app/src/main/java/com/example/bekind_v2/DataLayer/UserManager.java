package com.example.bekind_v2.DataLayer;


import android.content.Context;
import android.widget.Toast;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Date;

public class UserManager {
    public static void createUser(Context context, String name, String surname, String email, String password, Date birth, String city, String neighbourhoodID, String street, String street_number, MyCallback<Boolean> myCallback){
        UserLoginRepository.register(context, email, password, (x)->{
            if(x.isSuccessful())
                UserDatabaseRepository.createUser(getUserId(), name, surname, birth, email, city, street, street_number, neighbourhoodID, myCallback);
            else{
                if(x.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(context, "L'indirizzo email inserito risulta già registrato", Toast.LENGTH_LONG).show();
                    myCallback.onCallback(false);
                }
            }

        });
    }

    public static void login(Context context, String email, String password, MyCallback<Boolean> myCallback){
        UserLoginRepository.login(context, email, password, myCallback);
    }

    public static void logout(){
        UserLoginRepository.logout();
    }

    public static boolean isLogged(){
        return UserLoginRepository.isLogged();
    }

    public static String getUserId(){
        return UserLoginRepository.getUid();
    }

    public static String getEmail(){
        return UserLoginRepository.getEmail();
    }

    public static void updateUser(String name, String surname, String email, String city, String street, String street_number, String setNeighbourhoodID, String oldPassword, String newPassword){
        UserLoginRepository.updateCredentials(email,oldPassword,newPassword);
        UserDatabaseRepository.updateUser(getUserId(), name, surname, email, city, street, street_number, setNeighbourhoodID);
    }

    public static void getUser(String userId, MyCallback<UserDatabaseRepository.User> myCallback){
        UserDatabaseRepository.getUser(userId, myCallback);
    }
}
