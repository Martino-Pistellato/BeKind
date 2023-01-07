package com.example.bekind_v2.DataLayer;


import android.content.Context;
import android.widget.Toast;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Date;

public class UserManager {

    //method used to create a user in the app
    public static void createUser(Context context, String name, String surname, String email, String password, Date birth, String city, String neighbourhoodID, String street, String street_number, MyCallback<Boolean> myCallback){
        //it attempts to register the new user
        UserLoginRepository.register(context, email, password, (x)->{
            if(x.isSuccessful()) //if it has success, add user to the database
                UserDatabaseRepository.createUser(getUserId(), name, surname, birth, email, city, street, street_number, neighbourhoodID, myCallback);
            else{ //else, check if there has been an email address collision
                if(x.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(context, "L'indirizzo email inserito risulta già registrato", Toast.LENGTH_LONG).show();
                    myCallback.onCallback(false);
                }
            }

        });
    }

    //method used to login a user in the app
    public static void login(Context context, String email, String password, MyCallback<Boolean> myCallback){
        UserLoginRepository.login(context, email, password, myCallback);
    }

    //method used to logout from the app
    public static void logout(){
        UserLoginRepository.logout();
    }

    //method used to check if current user is logged
    public static boolean isLogged(){
        return UserLoginRepository.isLogged();
    }

    //method used to get current user id
    public static String getUserId(){
        return UserLoginRepository.getUid();
    }

    //methdo to get current user email
    public static String getEmail(){
        return UserLoginRepository.getEmail();
    }

    //method used to update user's credentials
    public static void updateUser(String name, String surname, String email, String city, String street, String street_number, String setNeighbourhoodID, String oldPassword, String newPassword){
        UserLoginRepository.updateCredentials(email,oldPassword,newPassword);
        UserDatabaseRepository.updateUser(getUserId(), name, surname, email, city, street, street_number, setNeighbourhoodID);
    }

    //method used to get a certain user by giving its id
    public static void getUser(String userId, MyCallback<UserDatabaseRepository.User> myCallback){
        UserDatabaseRepository.getUser(userId, myCallback);
    }
}
