package com.example.bekind_v2.UILayer.Authentication;

import android.util.Patterns;
import android.widget.DatePicker;

import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.NeighbourhoodRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.BottomBarViewModel;
import com.example.bekind_v2.UILayer.NeighbourhoodViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.material.textfield.TextInputEditText;
import com.example.bekind_v2.DataLayer.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AuthenticationViewModel extends ViewModel {
    private String name, surname, email, password, city, street, streetNumber, neighbourhoodName, neighbourhoodId;
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

    public void login(String email, String password, MyCallback myCallback){
        UserManager.login(email, password, myCallback);
    }

    private boolean checkEmail(TextInputEditText email, String userEmail){
        if(userEmail.isEmpty()) {
            email.setError("Questo campo non può essere vuoto");
            email.requestFocus();
            return false;
        }
        /*if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
            email.setError("L'indirizzo inserito non è nel formato corretto");
            email.requestFocus();
            return false;
        }*/

        //TODO a better solution -> send email verifications
        ArrayList<String> validServers = new ArrayList<>(Arrays.asList("@gmail.com", "@hotmail.com", "@outlook.com", "@yahoo.com", "@icloud.com", "@libero.it", "@tim.it", "@alice.it", "@tin.it"));
        boolean valid = false;
        int i = 0;
        while(i < validServers.size() && !valid){
            if ((userEmail.indexOf(validServers.get(i)) == userEmail.lastIndexOf(validServers.get(i))) && userEmail.endsWith(validServers.get(i)) && !userEmail.startsWith(validServers.get(i)))
                valid = true;
            else
                ++i;
        }
        if(!valid){
            email.setError("L'indirizzo inserito non è valido");
            email.requestFocus();
        }
        return valid;
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

        return checkEmail(email, userEmail) && checkPassword(password, userPassword);
    }


    public void checkLocationFields(TextInputEditText city, String userCity, TextInputEditText neighbourhood, String userNeighbourhood, TextInputEditText street, String userStreet, TextInputEditText streetNumber, String userStreetNumber, MyCallback myCallback){
        NeighbourhoodViewModel.doesNeighbourhoodExist(userNeighbourhood, userCity, (x)->{
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

            myCallback.onCallback(((boolean)x) && res);
        });
    }

    public void setBirthDate(DatePicker birthDate){
        Calendar calendar = Calendar.getInstance();//TODO: remove the plain text
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

    public void createUser(MyCallback myCallback){
        NeighbourhoodViewModel.getNeighbourhood(this.neighbourhoodName, this.city, (x) ->{
            if(x != null){
                UserManager.createUser(name, surname, email, password, birthDate.toString(), city, (String) x, street, streetNumber, new MyCallback() {
                    @Override
                    public void onCallback(Object result) {
                        login(email,password,myCallback);
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