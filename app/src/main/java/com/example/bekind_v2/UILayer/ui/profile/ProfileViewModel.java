package com.example.bekind_v2.UILayer.ui.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.UILayer.NeighbourhoodViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Locale;

public class ProfileViewModel extends ViewModel {

    public static class ProfileActivityViewPagerAdapter extends FragmentStateAdapter {
        //constructor, necessary
        public ProfileActivityViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        //return new Fragment based on position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 1) {
                return new MyPostsFragment();
            }
            else {
                return new ProposedFragment();
            }
        }

        //return number of Fragments
        @Override
        public int getItemCount() {
            return 2;
        }
    }

    public static ArrayList<String> filters = new ArrayList<>();
    private UserDatabaseRepository.User user;
    private String password;

    public void getUserName(MyCallback<String> myCallback){
        if (user == null) {
            UserManager.getUser(UserManager.getUserId(), new MyCallback<UserDatabaseRepository.User>() {
                @Override
                public void onCallback(UserDatabaseRepository.User result) {
                    user = result;
                    myCallback.onCallback(user.getName() + " " + user.getSurname());
                }
            });
        }
        else{
            myCallback.onCallback(user.getName() + " " + user.getSurname());
        }
    }

    public UserDatabaseRepository.User getUser(){return user;}

    public void getNeighbourhood(String id, MyCallback<String> myCallback){
        NeighbourhoodViewModel.getNeighbourhood(id, myCallback);
    }

    public void setName(String name){
        if(!name.isEmpty()) user.setName(name);
    }

    public void setSurname(String surname){
        if(!surname.isEmpty()) user.setSurname(surname);
    }

    public void setEmail(String email){
        if(!email.isEmpty()) user.setEmail(email);
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
    
    public void setStreet_number(String StreetNumber){
        if(!StreetNumber.isEmpty()) user.setStreet_number(StreetNumber);
    }
    
    public void setNeighbourhood(TextInputEditText neighbourhood, MyCallback<Boolean> myCallback){
        String name = neighbourhood.getText().toString().trim().toLowerCase();

        NeighbourhoodViewModel.doesNeighbourhoodExist(name, user.getCity().toLowerCase(), new MyCallback<Boolean>() {
            @Override
            public void onCallback(Boolean result) {
                if(result) {
                    NeighbourhoodViewModel.getNeighbourhood(name, user.getCity().toLowerCase(), new MyCallback<String>() {
                        @Override
                        public void onCallback(String result) {
                            user.setNeighbourhoodID(result);
                            myCallback.onCallback(true);
                        }
                    });
                }
                else{
                    if (!name.isEmpty()) {
                        neighbourhood.setError("Il quartiere non esiste");
                        neighbourhood.requestFocus();
                    }
                    else
                        myCallback.onCallback(true);
                }
            }
        });
    }

    public void updateUser(){ //TODO: should we check the old password?
        UserManager.updateUser(user.getName(), user.getSurname(), user.getEmail(), user.getCity(), user.getStreet(),
                               user.getStreet_number(), user.getNeighbourhoodID(), null, password);
    }

    public void createNeighbourhood (String name, MyCallback<Boolean> myCallback){
        if(!name.isEmpty())
            NeighbourhoodViewModel.createNeighbourhood(name.toLowerCase(), user.getCity().toLowerCase(), myCallback);
        else
            myCallback.onCallback(true);
    }

}