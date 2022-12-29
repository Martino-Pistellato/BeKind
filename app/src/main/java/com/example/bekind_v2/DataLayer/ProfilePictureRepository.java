package com.example.bekind_v2.DataLayer;


import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//https://www.geeksforgeeks.org/implementing-edit-profile-data-functionality-in-social-media-android-app/

public class ProfilePictureRepository {
    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public static String[] cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String storagepath = "Users_Profile_Cover_image/";
    //String uid;
    //ImageView set;
    //ProgressDialog pd;
    public static final int CAMERA_REQUEST = 100;
    public static final int STORAGE_REQUEST = 200;
    public static final int IMAGEPICK_GALLERY_REQUEST = 300;
    public static final int IMAGE_PICKCAMERA_REQUEST = 400;
    public static Uri imageuri;
    public static String profileOrCoverPhoto;
    
    
    // checking storage permission ,if given then we can add something in our storage
    private Boolean checkStoragePermission(Context context) {
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
     
    // requesting for storage permission
    private void requestStoragePermission() {
        //requestPermissions(storagePermission, STORAGE_REQUEST);
    }
    //DA DOVE ARRIVA requestPermissions?
     
    // checking camera permission ,if given then we can click image using our camera
    private Boolean checkCameraPermission(Context context) {
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    
     // requesting for camera permission if not given
     private void requestCameraPermission() {
        //requestPermissions(cameraPermission, CAMERA_REQUEST);
      }
}
