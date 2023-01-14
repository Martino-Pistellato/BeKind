package com.example.bekind_v2.UILayer.ui.profile;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.ProfilePictureRepository;
import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.NeighbourhoodViewModel;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewModel extends ViewModel {
    public static ArrayList<String> postsFilters = new ArrayList<>();
    public static ArrayList<String> proposedFilters = new ArrayList<>();
    private UserDatabaseRepository.User user;
    private String password;
    public static Uri imageuri;


    public static class ProfileActivityViewPagerAdapter extends FragmentStateAdapter {
        //constructor, necessary
        public MapViewModel mapViewModel;
        public ProfileActivityViewPagerAdapter(@NonNull Fragment fragment, MapViewModel mapViewModel) {
            super(fragment);
            this.mapViewModel = mapViewModel;
        }

        //return new Fragment based on position
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if(position == 1) {
                return new MyPostsFragment();
            }
            else {
                return new ProposedFragment(this.mapViewModel);
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
    
    public void setNeighbourhood(Context context, EditText neighbourhood, MyCallback<Boolean> myCallback){
        String name = Utilities.convertToProperForm(neighbourhood.getText().toString().trim());
        NeighbourhoodViewModel.doesNeighbourhoodExist(name, user.getCity(), new MyCallback<Boolean>() {
            @Override
            public void onCallback(Boolean result) {
                if(result) {
                    NeighbourhoodViewModel.getNeighbourhood(name, user.getCity(), new MyCallback<String>() {
                        @Override
                        public void onCallback(String result) {
                            user.setNeighbourhoodID(result);
                            myCallback.onCallback(true);
                        }
                    });
                }
                else{
                    if (!name.isEmpty()) {
                        neighbourhood.setError(context.getString(R.string.not_existing_neighbourhood));
                        neighbourhood.requestFocus();
                    }
                    else
                        myCallback.onCallback(true);
                }
            }
        });
    }

    public void updateUser(){ 
        UserManager.updateUser(user.getName(), user.getSurname(), user.getEmail(), user.getCity(), user.getStreet(),
                               user.getStreet_number(), user.getNeighbourhoodID(), password);
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

    public static void setImageUri(Context context){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Profile_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Profile picture");
        imageuri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }
    
    // checking storage permission, if given then we can add something in our storage
    static Boolean checkStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    // checking camera permission ,if given then we can click image using our camera
    static Boolean checkCameraPermission(Context context) {
        boolean result = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public static void uploadProfileCoverPhoto(final Uri uri, Context context, CircleImageView profilePic, ProgressBar progressBar) { //TODO spostala da qualche altra parte
        progressBar.setVisibility(View.VISIBLE);
        profilePic.setVisibility(View.GONE);
        ProfilePictureRepository.uploadProfileCoverPhoto(uri, context,profilePic, progressBar);
    }
}