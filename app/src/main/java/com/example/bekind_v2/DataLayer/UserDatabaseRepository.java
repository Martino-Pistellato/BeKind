package com.example.bekind_v2.DataLayer;

import androidx.annotation.NonNull;

import com.example.bekind_v2.Utilities.MyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserDatabaseRepository {
    public static class User{
        private String name;
        private String surname;
        @ServerTimestamp
        private Date birth;
        private String email;
        private String city;
        private String street;
        private String street_number;
        private String neighbourhoodID;
        private String image;

        //empty constructor, necessary
        public User(){}

        //constructor with all fields
        public User(String name, String surname, Date birth, String email, String city, String street, String street_number, String neighbourhoodID,String image){
            this.name = name;
            this.surname = surname;
            this.birth = birth;
            this.email = email;
            this.city = city;
            this.street = street;
            this.street_number = street_number;
            this.neighbourhoodID = neighbourhoodID;
            this.image = image;
        }

        //getters and setters
        public String getName(){return this.name;}
        public String getSurname(){return this.surname;}
        public Date getBirth(){return this.birth;}
        public String getEmail(){return this.email;}
        public String getCity(){return this.city;}
        public String getStreet(){return this.street;}
        public String getStreet_number(){return this.street_number;}
        public String getNeighbourhoodID() {return neighbourhoodID;}
        public String getImage(){return this.image;}

        public void setName(String name){this.name=name;}
        public void setSurname(String surname){this.surname=surname;}
        public void setEmail(String email){this.email=email;}
        public void setCity(String city){this.city=city;}
        public void setStreet(String street){this.street=street;}
        public void setStreet_number(String street_number){this.street_number=street_number;}
        public void setNeighbourhoodID(String neighbourhoodID){this.neighbourhoodID = neighbourhoodID;}
        public void setImage(String image){this.image = image;}
    }

    //method used to create a user and add it to the database
    public static void createUser(String userID, String name, String surname, Date birth, String email, String  city, String street, String street_number, String neighbourhoodID, MyCallback<Boolean> myCallback){
        User us = new User(name, surname, birth, email, city, street, street_number,neighbourhoodID, "");

        FirebaseFirestore.getInstance().collection("Users").document(userID).set(us).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                myCallback.onCallback(task.isSuccessful());
            }
        });
    }

    //method used to update a user's information
    public static void updateUser(String userID, String name, String surname, String email, String  city, String street, String street_number, String neighbourhoodID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection("Users").document(userID);

        if(!name.isEmpty())
            doc.update("name", name);
        if(!surname.isEmpty())
            doc.update("surname", surname);
        if(!email.isEmpty())
            doc.update("email", email);
        if(!city.isEmpty())
            doc.update("city", city);
        if(!street.isEmpty())
            doc.update("street", street);
        if(!street_number.isEmpty())
            doc.update("street_number", street_number);
        if(!neighbourhoodID.isEmpty())
            doc.update("neighbourhoodID", neighbourhoodID);
    }

    //method used to get a user by giving its id
    public static void getUser(String userId, MyCallback<User> myCallback){
        FirebaseFirestore.getInstance().collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.getResult().exists())
                    myCallback.onCallback(task.getResult().toObject(User.class));
            }
        });
    }
}
