package com.example.bekind_v2.DataLayer;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.UUID;

public class NeighbourhoodRepository {
    public class Neighbourhood{
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

    public void createNeighbourhood(String name, String city){
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

    public String getName(String Id){
        return ""; //TODO: get the neighbourhood and then the field name
    }

    public String getId(String city, String name){
        return ""; //TODO: get the neighbourhood and then the field id
    }

    public String getCity(String Id){
        return ""; //TODO: get the neighbourhood and then the field city
    }
}
