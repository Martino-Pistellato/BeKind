package com.example.bekind_v2.UILayer.Authentication;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.example.bekind_v2.UILayer.NeighbourhoodViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.example.bekind_v2.DataLayer.UserManager;

import java.util.Calendar;
import java.util.Date;

public class AuthenticationViewModel extends ViewModel {
    private String name, surname, email, password, city, street, streetNumber, neighbourhoodName, neighbourhoodID;
    private Date birthDate;

    public boolean checkCredentials(TextInputEditText email, String userEmail, TextInputEditText password, String userPassword){
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

    public void login(Context context, String email, String password, MyCallback<Boolean> myCallback){
        UserManager.login(context, email, password, myCallback);
    }

    private boolean checkPassword(TextInputEditText password, String userPassword) {

        if(userPassword.isEmpty()){
            password.setError("Questo campo non può essere vuoto");
            password.requestFocus();
            return false;
        }
        if(userPassword.length() < 8){
            password.setError("La password deve essere lunga almeno 8 caratteri");
            password.requestFocus();
            return false;
        }

        boolean hasUpper, hasLower, hasDigit;
        hasDigit = hasLower = hasUpper = false;

        for(char i : userPassword.toCharArray()){
            if(Character.isUpperCase(i))
                hasUpper = true;
            else if(Character.isLowerCase(i))
                hasLower = true;
            else if(Character.isDigit(i))
                hasDigit = true;
        }

        if(!(hasDigit && hasUpper && hasLower)) {
            password.setError("La password deve contenere almeno un carattere maiuscolo, un carattere minuscolo e un numero");
            password.requestFocus();
        }

        return hasDigit && hasUpper && hasLower;
    }

    public boolean checkUserFields(TextInputEditText name, String userName, TextInputEditText surname, String userSurname, TextInputEditText email, String userEmail, TextInputEditText password, String userPassword){
        //boolean res = true;

        if(userName.isEmpty()){
            name.setError("Questo campo non può essere vuoto");
            name.requestFocus();
            return false;
        }
        if(userSurname.isEmpty()){
            surname.setError("Questo campo non può essere vuoto");
            surname.requestFocus();
            return false;
        }

        if(userEmail.isEmpty()) {
            email.setError("Questo campo non può essere vuoto");
            email.requestFocus();
            return false;
        }

        return checkPassword(password, userPassword);
    }


    public void checkLocationFields(TextInputEditText city, String userCity, TextInputEditText neighbourhood, String userNeighbourhood, TextInputEditText street, String userStreet, TextInputEditText streetNumber, String userStreetNumber, MyCallback<Boolean> myCallback){
        NeighbourhoodViewModel.doesNeighbourhoodExist(userNeighbourhood.toLowerCase(), userCity.toLowerCase(), new MyCallback<Boolean>() {
            @Override
            public void onCallback(Boolean result) {
                boolean res = true;

                if(userCity.isEmpty()){
                    city.setError("Questo campo non può essere vuoto");
                    city.requestFocus();
                    res = false;
                }
                if(userNeighbourhood.isEmpty()){
                    neighbourhood.setError("Questo campo non può essere vuoto");
                    neighbourhood.requestFocus();
                    res = false;
                }
                if(!result){
                    neighbourhood.setError("Questo quartiere non esiste. Crealo!");
                    neighbourhood.requestFocus();
                    res = false;
                }
                if(userStreet.isEmpty()){
                    street.setError("Questo campo non può essere vuoto");
                    street.requestFocus();
                    res = false;
                }
                if(userStreetNumber.isEmpty()){
                    streetNumber.setError("Questo campo non può essere vuoto");
                    streetNumber.requestFocus();
                    res = false;
                }

                myCallback.onCallback(result && res);
            }
        });
    }

    public void setBirthDate(DatePicker birthDate){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -100);
        //calendar.set(1920,1,1);
        Date minDate = calendar.getTime(), maxDate;
        calendar.add(Calendar.YEAR, 82);
        //calendar.set(2008, 11, 31);
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

    public void saveLocationData(String city, String neighbourhoodName, String street, String streetNumber){
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.neighbourhoodName = neighbourhoodName;
    }

    public static Date toDate(DatePicker date){
        return BottomBarViewModel.toDate(date.getYear(), date.getMonth(), date.getDayOfMonth(), 0, 0);
    }

    public void getUserData(TextInputEditText name, TextInputEditText surname, TextInputEditText email, TextInputEditText password,  DatePicker date){
        name.setText(this.name);
        surname.setText(this.surname);
        email.setText(this.email);
        password.setText(this.password);

        if (birthDate != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthDate);

            date.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    public void getLocationData(TextInputEditText city, TextInputEditText neighbourhood, TextInputEditText street, TextInputEditText streetNumber){
        city.setText(this.city);
        neighbourhood.setText(this.neighbourhoodName);
        street.setText(this.street);
        streetNumber.setText(this.streetNumber);
    }

    public void createUser(Context context, MyCallback<Boolean> myCallback){
        NeighbourhoodViewModel.getNeighbourhood(this.neighbourhoodName, this.city, (x) ->{
            if(x != null){
                Log.e("USER CREATE STEP 2", "call to usermanager");
                UserManager.createUser(context, name, surname, email, password, birthDate, city.toLowerCase(), x, street.toLowerCase(), streetNumber.toLowerCase(), new MyCallback<Object>() {
                    @Override
                    public void onCallback(Object result) {
                        Log.e("STEP 5", "call to login");
                        login(context, email,password,myCallback);
                    }
                });
            }
            else{
                myCallback.onCallback(null);
            }
        });
    }

    public String getCity(){
        return this.city;
    }
    
    public void setNeighbourhood(String neighbourhoodName){
        this.neighbourhoodName = neighbourhoodName;
    }
}