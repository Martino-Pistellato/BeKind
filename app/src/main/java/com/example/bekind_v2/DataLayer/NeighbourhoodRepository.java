package com.example.bekind_v2.DataLayer;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.UUID;

public class NeighbourhoodRepository {
    public static class Neighbourhood{
        private  String city, name, id;

        public Neighbourhood(){
        }

        public Neighbourhood(String city, String name, String id){
            this.name = name;
            this.id = id;
            this.city = city;
        }

        public String getName(){return this.name;}
        public String getId(){return this.id;}
        public String getCity(){return this.city;}
    }

    public static void createNeighbourhood(String name, String city, MyCallback myCallback){
        doesNeighbourhoodExist(name, city, new MyCallback() {
            @Override
            public void onCallback(Object result) {
                if(!(boolean)result){ //if there is no neighbourhood with the same name in the same city
                    String Id =  UUID.randomUUID().toString();
                    Neighbourhood neighbourhood = new Neighbourhood(city, name, Id);
                    FirebaseFirestore.getInstance().collection("Neighbourhoods").add(neighbourhood).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            myCallback.onCallback(task.isSuccessful());
                        }
                    });
                }
                else //if there is a neighbourhood with the same name in the same city
                    myCallback.onCallback((boolean)result);
            }
        });
    }

    public static void getNeighbourhood(String name, String city, MyCallback myCallback){
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

    public static void doesNeighbourhoodExist(String name, String city, MyCallback myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhoods").whereEqualTo("name", name).whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                    myCallback.onCallback(!task.getResult().isEmpty());
            }
        });
    }
}
