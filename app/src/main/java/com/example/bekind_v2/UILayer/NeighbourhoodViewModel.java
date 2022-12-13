package com.example.bekind_v2.UILayer;

import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.NeighbourhoodRepository;
import com.example.bekind_v2.Utilities.MyCallback;

import com.google.android.material.textfield.TextInputEditText;

public class NeighbourhoodViewModel extends ViewModel {

    public void createNeighbourhood(String name, String city, MyCallback myCallback){
        NeighbourhoodRepository.createNeighbourhood(name.toLowerCase(), city.toLowerCase(), myCallback);
    }

    public static void doesNeighbourhoodExist(String name, String city, MyCallback myCallback){
        NeighbourhoodRepository.doesNeighbourhoodExist(name, city, myCallback);
    }

    public static void getNeighbourhood(String name, String city, MyCallback myCallback){
        NeighbourhoodRepository.getNeighbourhood(name.toLowerCase(),city.toLowerCase(),myCallback);
    }

    public boolean checkNeighbourhoodName(TextInputEditText name, String neighbourhoodName){
        if(neighbourhoodName.isEmpty()){
            name.setError("Questo campo non può essere vuoto");
            name.requestFocus();
            return false;
        }
        return true;
    }
}