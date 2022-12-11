package com.example.bekind_v2.UILayer.Authentication;

import android.util.Log;
import android.widget.DatePicker;

import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.example.bekind_v2.DataLayer.UserManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AuthenticationViewModel extends ViewModel {
    private String name, surname, email, password, city, street, streetNumber, neighbourhoodId;
    private Date birthDate;
    // TODO: Implement the ViewModel

    public boolean checkCredentials(TextInputEditText email, String userEmail, TextInputEditText password, String userPassword){
        Log.e("PSD_EMAIL", userEmail);
        Log.e("PSD_PSSWD", userPassword);

        if(userEmail.isEmpty()) { //we check if all fields have been properly filled
            email.setError("Per accedere devi inserire una email");
            email.requestFocus(); //turns the field red
            return false;
        }
        else if(userPassword.isEmpty()) {
            password.setError("Per accedere devi inserire una password");
            password.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isLogged(){
        return UserManager.isLogged();
    }

    public void login(String email, String password, MyCallback myCallback){
        UserManager.login(email, password, myCallback);
    }

    public boolean checkUserFiedls(TextInputEditText name, String userName, TextInputEditText surname, String userSurname, TextInputEditText email, String userEmail, TextInputEditText password, String userPassword){
        if(userName.isEmpty()){
            name.setError("Questo campo non può essere vuoto");
            name.requestFocus();
            return false;
        }else if(userSurname.isEmpty()){
            surname.setError("Questo campo non può essere vuoto");
            surname.requestFocus();
            return false;
        }else if(userEmail.isEmpty()){
            email.setError("Questo campo non può essere vuoto");
            email.requestFocus();
            return false;
        }else if(userPassword.isEmpty()){
            password.setError("Questo campo non può essere vuoto");
            password.requestFocus();
            return false;
        }
        return true;
    }

    public boolean checkLocationFiedls(TextInputEditText city, String userCity, TextInputEditText neighbourhood, String userNeighbourhood, TextInputEditText street, String userStreet, TextInputEditText streetNumber, String userStreetNumber){
        if(userCity.isEmpty()){
            city.setError("Questo campo non può essere vuoto");
            city.requestFocus();
            return false;
        }else if(userNeighbourhood.isEmpty()){
            neighbourhood.setError("Questo campo non può essere vuoto");
            neighbourhood.requestFocus();
            return false;
        }else if(userStreet.isEmpty()){
            street.setError("Questo campo non può essere vuoto");
            street.requestFocus();
            return false;
        }else if(userStreetNumber.isEmpty()){
            streetNumber.setError("Questo campo non può essere vuoto");
            streetNumber.requestFocus();
            return false;
        }
        //there should also be a check to see if the neighbourhood exists
        /*else if (!existsNeighbourhood(userNeighbourhood, userCity)){
            neighbourhood.setError("Questo quartiere non esiste");
            neighbourhood.requestFocus();
            return false;
        }*/
        //they should not be elses and if we return false as soon as we catch the first empty field, if there are other empty fields they won't be higlighted, same problem in checkUserFields
        return true;
    }

    public static void setBirthDate(DatePicker birthDate){
        Calendar calendar = Calendar.getInstance();
        calendar.set(1920,1,1);
        Date minDate = calendar.getTime(), maxDate;
        calendar.set(2008, 11, 31);
        maxDate = calendar.getTime();
        birthDate.setMaxDate(maxDate.getTime());
        birthDate.setMinDate(minDate.getTime());
    }

    public void changeFragment(FragmentActivity activity, int fragmentId, AuthenticationViewModel authenticationViewModel){
        //we start a fragment transaction and we replace current view with the new fragment
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();

        if(fragmentId == R.id.fragment_registration1) {
            RegistrationFragment2 registrationFragment2 = new RegistrationFragment2(authenticationViewModel);
            fragmentTransaction.replace(R.id.fragment_container, registrationFragment2).commit();
        }else{
            RegistrationFragment1 registrationFragment1 = new RegistrationFragment1(authenticationViewModel);
            fragmentTransaction.replace(R.id.fragment_container, registrationFragment1).commit();
        }
    }

    public void saveUserData(String name, String surname, String email, String password, Date birthDate){ //TODO: we also need a getter if we reach fragment1 from fragment2
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public void saveLocationData(String city, String street, String streetNumber, String neighbourhoodId){
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.neighbourhoodId = neighbourhoodId;
    }

    public static Date toDate(DatePicker date){
        return BottomBarViewModel.toDate(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0);
    }

    public ArrayList<String> getUserFields(){
        //do we return an ArrayList? we do a method to get the single fiedls?

        return null;
    }
}