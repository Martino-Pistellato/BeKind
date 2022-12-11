package com.example.bekind_v2.DataLayer;

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
        private final String city, name, Id;

        public Neighbourhood(String city, String name, String Id){
            this.name = name;
            this.Id = Id;
            this.city = city;
        }

        public String getName(){return this.name;}
        public String getId(){return this.Id;}
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
        FirebaseFirestore.getInstance().collection("Neighbourhood").whereEqualTo("name",name).whereEqualTo("city",city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<String> myList = new ArrayList<>();
                    for (DocumentSnapshot snap :  task.getResult())
                        myList.add(snap.toObject(String.class));
                    myCallback.onCallback(myList.get(0));
                }
                else
                    myCallback.onCallback(null);
            }
        });
    }

    public static void doesNeighbourhoodExist(String name, String city, MyCallback myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhood").whereEqualTo("name", name).whereEqualTo("city", city).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                    myCallback.onCallback(!task.getResult().isEmpty());
            }
        });
    }
}
