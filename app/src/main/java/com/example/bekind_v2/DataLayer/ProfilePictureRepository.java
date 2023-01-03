package com.example.bekind_v2.DataLayer;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//https://www.geeksforgeeks.org/implementing-edit-profile-data-functionality-in-social-media-android-app/

public class ProfilePictureRepository {
    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public static CollectionReference databaseReference = FirebaseFirestore.getInstance().collection("Users");
    
    //forse da mettere qui o forse in locale su ProfileFragment
    public static String profileOrCoverPhoto = "";

}
