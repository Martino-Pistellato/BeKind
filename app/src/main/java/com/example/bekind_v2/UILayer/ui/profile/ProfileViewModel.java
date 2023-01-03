package com.example.bekind_v2.UILayer.ui.profile;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.UILayer.NeighbourhoodViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.PostRepository;

import java.util.ArrayList;

public class ProfileViewModel extends ViewModel {
    //public static ArrayList<String> filters = new ArrayList<>();
    //we'll need it later to implement manageFilter
    public static ArrayList<String> postsFilters = new ArrayList<>();
    public static ArrayList<String> proposedFilters = new ArrayList<>();
    private UserDatabaseRepository.User user;
    private String password;

    public static String[] cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String[] storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public static String storagepath = "Users_Profile_Cover_image/";
    public static final int STORAGE_REQUEST = 200;
    public static final int CAMERA_REQUEST = 100;
    public static final int IMAGEPICK_GALLERY_REQUEST = 300;
    public static final int IMAGE_PICKCAMERA_REQUEST = 400;
    public static Uri imageuri;
    public static String profileOrCoverPhoto;
    //String uid;
    //ImageView set;
    //ProgressDialog pd;

    public static class ProfileActivityViewPagerAdapter extends FragmentStateAdapter {
        //constructor, necessary
        public ProfileActivityViewPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        //return new Fragment based on position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 1) {
                return new MyPostsFragment();
            }
            else {
                return new ProposedFragment();
            }
        }

        //return number of Fragments
        @Override
        public int getItemCount() {
            return 2;
        }
    }

    public void getUserName(MyCallback<String> myCallback){
        if (user == null) {
            UserManager.getUser(UserManager.getUserId(), new MyCallback<UserDatabaseRepository.User>() {
                @Override
                public void onCallback(UserDatabaseRepository.User result) {
                    user = result;
                    myCallback.onCallback(user.getName() + " " + user.getSurname());
                }
            });
        }
        else{
            myCallback.onCallback(user.getName() + " " + user.getSurname());
        }
    }

    public UserDatabaseRepository.User getUser(){return user;}

    public void getNeighbourhood(String id, MyCallback<String> myCallback){
        NeighbourhoodViewModel.getNeighbourhood(id, myCallback);
    }

    public void setName(String name){
        if(!name.isEmpty()) user.setName(name);
    }

    public void setSurname(String surname){
        if(!surname.isEmpty()) user.setSurname(surname);
    }

    public void setEmail(String email){
        if(!email.isEmpty()) user.setEmail(email);
    }
    
    public void setPassword(String password){
        if(!password.isEmpty()) this.password = password;
    }
    
    public void setCity(String city){
       if(!city.isEmpty()) user.setCity(city);
    }

    public void setStreet(String street){
        if(!street.isEmpty()) user.setStreet(street);
    }
    
    public void setStreet_number(String StreetNumber){
        if(!StreetNumber.isEmpty()) user.setStreet_number(StreetNumber);
    }
    
    public void setNeighbourhood(EditText neighbourhood, MyCallback<Boolean> myCallback){
        String name = neighbourhood.getText().toString().trim().toLowerCase();
        NeighbourhoodViewModel.doesNeighbourhoodExist(name, user.getCity().toLowerCase(), new MyCallback<Boolean>() {
            @Override
            public void onCallback(Boolean result) {
                if(result) {
                    NeighbourhoodViewModel.getNeighbourhood(name, user.getCity().toLowerCase(), new MyCallback<String>() {
                        @Override
                        public void onCallback(String result) {
                            user.setNeighbourhoodID(result);
                            myCallback.onCallback(true);
                        }
                    });
                }
                else{
                    if (!name.isEmpty()) {
                        neighbourhood.setError("Il quartiere non esiste");
                        neighbourhood.requestFocus();
                    }
                    else
                        myCallback.onCallback(true);
                }
            }
        });
    }

    public void updateUser(){ //TODO: should we check the old password?
        UserManager.updateUser(user.getName(), user.getSurname(), user.getEmail(), user.getCity(), user.getStreet(),
                               user.getStreet_number(), user.getNeighbourhoodID(), null, password);
    }

    public void createNeighbourhood (String name, MyCallback<Boolean> myCallback){
        if(!name.isEmpty())
            NeighbourhoodViewModel.createNeighbourhood(name.toLowerCase(), user.getCity().toLowerCase(), myCallback);
        else
            myCallback.onCallback(true);
    }

    public static void managePostsFilter(String filter){
        Utilities.manageFilter(filter, postsFilters);
        PostRepository.getPosts(Utilities.day, UserManager.getUserId(), postsFilters, PostTypes.MYPOSTS, new MyCallback<ArrayList<PostRepository.Post>>() {
            @Override
            public void onCallback(ArrayList<PostRepository.Post> result) {
                Utilities.SharedViewModel.postsViewModel.getMyPosts().setValue(result);
                    }
                });
    }

     public static void manageProposedFilter(String filter){
        Utilities.manageFilter(filter, proposedFilters);
        ProposalRepository.getProposals(Utilities.day, UserManager.getUserId(), proposedFilters, Types.PROPOSED, new MyCallback<ArrayList<ProposalRepository.Proposal>>() {
            @Override
            public void onCallback(ArrayList<ProposalRepository.Proposal> result) {
                Utilities.SharedViewModel.proposalsViewModel.getProposed().setValue(result);
            }
        });
     }

    public static Intent getCameraIntent(Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Profile_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Profile picture");
        imageuri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
        return cameraIntent;
    }

    public static Intent getGalleryIntent(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        return galleryIntent;
    }









}