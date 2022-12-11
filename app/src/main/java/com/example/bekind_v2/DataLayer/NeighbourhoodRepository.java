package com.example.bekind_v2.DataLayer;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

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

    public static void createNeighbourhood(String name, String city){
        String Id =  UUID.randomUUID().toString();
        Neighbourhood neighbourhood = new Neighbourhood(city, name, Id);
        CollectionReference db = FirebaseFirestore.getInstance().collection("Neighbourhoods");
        Query neigh_query = db.whereEqualTo("city", city).whereEqualTo("name", name);

        neigh_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                    if(task.getResult().isEmpty())
                        db.add(neighbourhood); //TODO: errors should be displayed here or in the UI layer?
            }
        });
    }

    public static void getNeighbourhood(String Id, MyCallback myCallback){
        FirebaseFirestore.getInstance().collection("Neighbourhood").document(Id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                    myCallback.onCallback(task.getResult().toObject(Neighbourhood.class));
            }
        });
    }

    public static boolean existsNeighbourhood(String Id, String city, MyCallback myCallback){
        Query existsNeighbourhood = FirebaseFirestore.getInstance().collection("Neighbourhood").whereEqualTo("", Id).whereEqualTo("city", city);
        //we should check wether the city field in the object equals to the one passed as input --> if so return true, else return false
        //we need a document snapshot to do a oncomplete listener
        /*existsNeighbourhood.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                    myCallback.onCallback(task.getResult().toObject(Neighbourhood.class));
            }
        });*/
        return false;
    }
}
