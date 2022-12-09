package com.example.bekind_v2.DataLayer;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDatabaseRepository {

    public static class User{
        private String name;
        private String surname;
        private final String birth; //is a String, not a Date.
        private String email;
        private String city;
        private String street;
        private String street_number;
        private String neighbourhoodID;

        public User(String name, String surname, String birth, String email, String city, String street, String street_number, String neighbourhoodID){
            this.name = name;
            this.surname = surname;
            this.birth = birth;
            this.email = email;
            this.city = city;
            this.street = street;
            this.street_number = street_number;
            this.neighbourhoodID = neighbourhoodID;
        }

        public String getName(){return this.name;}
        public String getSurname(){return this.surname;}
        public String getBirth(){return this.birth;}
        public String getEmail(){return this.email;}
        public String getCity(){return this.city;}
        public String getStreet(){return this.street;}
        public String getStreet_number(){return this.street_number;}
        public String getNeighbourhoodID() {return neighbourhoodID;}

        public void setName(String name){this.name=name;}
        public void setSurname(String surname){this.surname=surname;}
        public void setEmail(String eemail){this.email=email;}
        public void setCity(String city){this.city=city;}
        public void setStreet(String street){this.street=street;}
        public void setStreet_number(String street_number){this.street_number=street_number;}
        public void setNeighbourhoodID(String neighborhoodID){this.neighbourhoodID = neighborhoodID;}
    }

    public void createUser(String userID, String name, String surname, String birth, String email, String  city, String street, String street_number, String neighbourhoodID){

        //TODO: add check for neighbourhood in that repository

        User us = new User(name, surname, birth, email, city, street, street_number,neighbourhoodID);
        FirebaseFirestore.getInstance().collection("Users").document(userID).set(us);
    }

    public void updateUser(String userID, String name, String surname, String email, String  city, String street, String street_number, String neighbourhoodID){
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

    public String getName(String userID){
        /*FirebaseFirestore.getInstance().collection("Users").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                User us = task.getResult().toObject(User.class);
                us.getName();
            }
        });*/

        return "bula bula"; //TODO: make getters asynchronous
    }

    public String getSurname(String userID){
        return (String) FirebaseFirestore.getInstance().collection("Users").document(userID).get().getResult().get("surname");
    }

    public String getBirth(String userID){
        return (String) FirebaseFirestore.getInstance().collection("Users").document(userID).get().getResult().get("birth");
    }

    public String getCity(String userID){
        return (String) FirebaseFirestore.getInstance().collection("Users").document(userID).get().getResult().get("city");
    }

    public String getStreet(String userID){
        return (String) FirebaseFirestore.getInstance().collection("Users").document(userID).get().getResult().get("street");
    }

    public String getStreetNumber(String userID){
        return (String) FirebaseFirestore.getInstance().collection("Users").document(userID).get().getResult().get("street_number");
    }

    public String getNeighbourhoodID(String userID){
        return (String) FirebaseFirestore.getInstance().collection("Users").document(userID).get().getResult().get("neighbourhoodID");
    }
}
