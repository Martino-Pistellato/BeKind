package com.example.bekind_v2.DataLayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePictureRepository {
    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    public static CollectionReference databaseReference = FirebaseFirestore.getInstance().collection("Users");
    public static String storagepath = "Users_Profile_Cover_image/";
    public static String profileOrCoverPhoto;

    public static void uploadProfileCoverPhoto(final Uri uri, Context context, CircleImageView profilePic, ProgressBar progressBar) {
        if (uri == null) {
            progressBar.setVisibility(View.GONE);
            profilePic.setVisibility(View.VISIBLE);
            return;
        }

        String filepathname = storagepath + "" + profileOrCoverPhoto + "_" + UserManager.getUserId(); // We are taking the filepath as storagepath + firebaseauth.getUid()+".png"
        storageReference.child(filepathname).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                            databaseReference.document(UserManager.getUserId()).update("image", task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show();
                                        Glide.with(context).load(task.getResult().toString()).addListener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBar.setVisibility(View.GONE);
                                                profilePic.setVisibility(View.VISIBLE);
                                                return false;
                                            }
                                        }).into(profilePic);
                                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                                        Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
                                    }
                                    else
                                        Toast.makeText(context, "Error Updating", Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                });
            }
        }).addOnFailureListener(e -> {Toast.makeText(context, "Error: could not load the image", Toast.LENGTH_LONG).show();});
    }
}
