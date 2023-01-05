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

        public Neighbourhood(){}

        public Neighbourhood(String city, String name, String id){
            this.name = name;
            this.id = id;
            this.city = city;
        }

        public String getName(){return this.name;}
        public String getId(){return this.id;}
        public String getCity(){return this.city;}
    }

    public static void createNeighbourhood(String name, String city, MyCallback<Boolean> myCallback){
        doesNeighbourhoodExist(name, city, new MyCallback<Boolean>() {
            @Override
            public void onCallback(Boolean result) {
                if(!result){ //if there is no neighbourhood with the same name in the same city
                    String Id =  UUID.randomUUID().toString();
                    Neighbourhood neighbourhood = new Neighbourhood(city, name, Id);

                    FirebaseFirestore.getInstance().collection("Neighbourhoods").document(Id).set(neighbourhood).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                                myCallback.onCallback(task.isSuccessful());
                        }
                    });
                }
                else //if there is a neighbourhood with the same name in the same city
                    myCallback.onCallback(false);
            }
        });
    }

    public static void getNeighbourhood(String name, String city, MyCallback<String> myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("city",city).whereEqualTo("name",name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().isEmpty()) {
                        Neighbourhood neighbourhood = new Neighbourhood();
                        for (DocumentSnapshot snap : task.getResult())
                             neighbourhood = snap.toObject(Neighbourhood.class);
                        myCallback.onCallback(neighbourhood.getId());
                    }
                }
                else
                    myCallback.onCallback(null);
            }
        });
    }

    public static void getNeighbourhood(String id, MyCallback<String> myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()) {
                        Neighbourhood neighbourhood = new Neighbourhood();
                        neighbourhood = task.getResult().toObject(Neighbourhood.class);
                        myCallback.onCallback(neighbourhood.getName());
                    }
                }
                else
                    myCallback.onCallback(null);
            }
        });
    }

    public static void doesNeighbourhoodExist(String name, String city, MyCallback<Boolean> myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("name", name).whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                    myCallback.onCallback(!task.getResult().isEmpty());
            }
        });
    }

    public static void getNeighbourhoods(String city, MyCallback myCallback) {
            ArrayList<String> res = new ArrayList<>();

            FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot snap : task.getResult()) {
                        String name = snap.getString("name");
                        res.add(name.substring(0, 1).toUpperCase() + name.substring(1));
                    }

                    myCallback.onCallback(res);
                }
            });
    }

    public static void getCities(MyCallback myCallback) {
        ArrayList<String> res = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Neighbourhoods").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot snap : task.getResult()){
                    String city = snap.getString("city");
                    res.add(city.substring(0, 1).toUpperCase() + city.substring(1));
                }

                myCallback.onCallback(res);
            }
        });
    }
}
