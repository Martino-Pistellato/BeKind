package com.example.bekind_v2.DataLayer;

import android.util.Log;

import com.example.bekind_v2.Utilities.MyCallback;

public class UserManager {
    public static void createUser(String name, String surname, String email, String password, String birth, String city, String neighbourhoodID, String street, String street_number, MyCallback myCallback){
        Log.e("CRTD_USR", "before auth registration");
        UserLoginRepository.register(email, password, (x)->{
            UserDatabaseRepository.createUser(getUserId(), name, surname, birth, email, city, street, street_number, neighbourhoodID, myCallback);
        });
    }

    public static void login(String email, String password, MyCallback myCallback){
        UserLoginRepository.login(email, password, myCallback);
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

    public static void updateUser(String name, String surname, String email, String city, String street, String street_number, String neighborhoodId, String oldPassword, String newPassword){
        UserLoginRepository.updateCredentials(email,oldPassword,newPassword);
        UserDatabaseRepository.updateUser(getUserId(), name, surname, email, city, street, street_number, neighborhoodId);
    }

    public static void getUser(String userId, MyCallback myCallback){
        UserDatabaseRepository.getUser(userId, myCallback);
    }
}
