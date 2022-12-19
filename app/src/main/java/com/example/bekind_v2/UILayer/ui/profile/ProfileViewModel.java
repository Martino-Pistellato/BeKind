package com.example.bekind_v2.UILayer.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.Utilities.MyCallback;

public class ProfileViewModel extends ViewModel {
    private UserDatabaseRepository.User user;
    private String password;

    public ProfileViewModel(){
        UserManager.getUser(UserManager.getUserId(), new MyCallback<UserDatabaseRepository.User>() {
            @Override
            public void onCallback(UserDatabaseRepository.User result) {
                user = result;
            }
        });
    }

    public String getUserName(){
        return user.getName() + " " + user.getSurname();
    }

    public void logout(){
        UserManager.logout();
    }

    public void setName(String name){
        if(!name.isEmpty()) user.setName(name);
    }

    public void setSurname(String surname){
        if(!surname.isEmpty()) user.setSurname(surname);
    }

    public void setEmail(String email){
        if(!email.isEmpty()) user.setSurname(email);
    }
    
    public void setPassword(String password){
        if(!password.isEmpty()) this.password = password;
    }
    
    public void setCity(String city){
       if(!city.isEmpty()) user.setCity(city);
    }

    public void setStreet(String street){
        if(!street.isEmpty()) user.setStreet(street);
    }
    
    public void setStreet_number(String Nstreet){
        if(!Nstreet.isEmpty()) user.setStreet_number(Nstreet);
    }
    
    public void setNeighbourhoodId(String NeighID){ 
        if(!NeighID.isEmpty()) user.setNeighbourhoodId(NeighID);
    }

    public void updateUser(){
        
    }

}