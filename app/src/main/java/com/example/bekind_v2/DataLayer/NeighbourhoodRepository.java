package com.example.bekind_v2.DataLayer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class NeighbourhoodRepository {

    public static class Neighbourhood{
        private String city, name, id;

        //empty constructor, necessary
        public Neighbourhood(){}

        //constructor with all the fields
        public Neighbourhood(String city, String name, String id){
            this.name = name;
            this.id = id;
            this.city = city;
        }

        //TODO should we make the fields final?
        //getters, no setter
        public String getName(){return this.name;}
        public String getId(){return this.id;}
        public String getCity(){return this.city;}
    }

    //method used to create a neighbourhood and add it to the database
    public static void createNeighbourhood(String name, String city, MyCallback<Boolean> myCallback){
        doesNeighbourhoodExist(name, city, new MyCallback<Boolean>() { //first, we check if the neighbourhood already exists
            @Override
            public void onCallback(Boolean result) {
                if(!result){ //if there is no neighbourhood with the same name in the same city, we create it
                    String Id =  UUID.randomUUID().toString();
                    Neighbourhood neighbourhood = new Neighbourhood(city, name, Id);

                    FirebaseFirestore.getInstance().collection("Neighbourhoods").document(Id).set(neighbourhood).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                myCallback.onCallback(task.isSuccessful());
                        }
                    });
                }
                else //if there is a neighbourhood with the same name in the same city, a message will appear and the neighbourhood won't be created again
                    myCallback.onCallback(false);
            }
        });
    }

    //method to get a neighbourhood id by giving its name and its city name in input
    public static void getNeighbourhood(String name, String city, MyCallback<String> myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("city",city).whereEqualTo("name",name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()) { //if we got something
                        Neighbourhood neighbourhood = new Neighbourhood();
                        for (DocumentSnapshot snap : task.getResult())
                             neighbourhood = snap.toObject(Neighbourhood.class); //we convert the result to a neighbourhood
                        myCallback.onCallback(neighbourhood.getId());
                    }
                }
                else
                    myCallback.onCallback(null);
            }
        });
    }

    //method to get a neighbourhood name by giving its id in input
    public static void getNeighbourhood(String id, MyCallback<String> myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()) {
                        Neighbourhood neighbourhood = task.getResult().toObject(Neighbourhood.class);
                        myCallback.onCallback(neighbourhood.getName());
                    }
                }
                else
                    myCallback.onCallback(null);
            }
        });
    }

    //method used to check if a neighbourhood with a certain name in a certain city is already present in the database
    public static void doesNeighbourhoodExist(String name, String city, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("name", name).whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    myCallback.onCallback(!task.getResult().isEmpty()); //if we got something, the neighbourhood exists (true), else false
                }
            }
        });
    }

    //method used to get all the neighbourhood associated with a certain city
    public static void getNeighbourhoods(String city, MyCallback<ArrayList<String>> myCallback) {
            ArrayList<String> res = new ArrayList<>();

            FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot snap : task.getResult()) {
                        res.add(snap.getString("name")); //we add all neighbourhoods names to the arraylist
                    }

                    myCallback.onCallback(res);
                }
            });
    }

    //TODO since it is no longer used (no more drop down in the city field in registration since we are using Maps), should we delete it?
    //method used to get all cities in the database
    public static void getCities(MyCallback<ArrayList<String>> myCallback) {
        ArrayList<String> res = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Neighbourhoods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snap : task.getResult()){
                    res.add(snap.getString("city")); //we add al cities names to the arraylist
                }

                myCallback.onCallback(res);
            }
        });
    }
}
