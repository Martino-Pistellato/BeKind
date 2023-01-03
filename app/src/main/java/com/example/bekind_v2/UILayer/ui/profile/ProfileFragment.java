package com.example.bekind_v2.UILayer.ui.profile;

import static com.example.bekind_v2.DataLayer.ProfilePictureRepository.databaseReference;
import static com.example.bekind_v2.DataLayer.ProfilePictureRepository.storageReference;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.CAMERA_REQUEST;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.IMAGEPICK_GALLERY_REQUEST;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.IMAGE_PICKCAMERA_REQUEST;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.STORAGE_REQUEST;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.imageuri;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.profileOrCoverPhoto;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.storagepath;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.time.ZoneId;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SwitchCompat simpleSwitch;
    private TextView totalActivities, scheduledateText;

    //campi aggiunti per implementare foto profilo
    CircleImageView set;
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) pickFromCamera();
            else Toast.makeText(getContext(), "Camera permissions not granted", Toast.LENGTH_LONG).show();
        });
    private final ActivityResultLauncher<String> requestGalleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) pickFromGallery();
        else Toast.makeText(getContext(), "Gallery permissions not granted", Toast.LENGTH_LONG).show();
    });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button updateUserData = binding.modifyProfileBtn, updateUserLocation = binding.modifyNeighBtn;
        TextView profileName = binding.profileName;
        simpleSwitch = root.findViewById(R.id.simpleSwitch);
        totalActivities = root.findViewById(R.id.total_activities);
        scheduledateText = root.findViewById(R.id.scheduledate_text);
        Context context = this.getContext();

        //aggiunte per foto profilo
        set = root.findViewById(R.id.user_photo);
        //pd = new ProgressDialog(this);
        //pd.setCanceledOnTouchOutside(false);

        profileViewModel.getUserName(profileName::setText);
        UserManager.getUser(UserManager.getUserId(), (user) ->{
            String image = user.getImage();
            if(!image.isEmpty()) {
                try {
                    Glide.with(this).load(image).into(set);
                } catch (Exception e) {
                    Log.e("ERROR", "Errore: immagine non presente");
                }
            }
        });

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pd.setMessage("Updating Profile Picture");
                profileOrCoverPhoto = "image";
                showImagePicDialog();
            }
        });

        updateUserData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.popup_update1);
                dialog.setCanceledOnTouchOutside(false);

                TextInputEditText userName = dialog.findViewById(R.id.modify_profile_name), userSurname = dialog.findViewById(R.id.modify_profile_surname);
                Button confirmBtn =  dialog.findViewById(R.id.confirm_button), cancelBtn =  dialog.findViewById(R.id.cancel_button);
                TextView textCredentials = dialog.findViewById(R.id.text_change_credentials);

                userName.setText(profileViewModel.getUser().getName());
                userSurname.setText(profileViewModel.getUser().getSurname());

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss(); //close dialog
                    }
                });

                confirmBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        profileViewModel.setName(userName.getText().toString().trim());
                        profileViewModel.setSurname(userSurname.getText().toString().trim());
                        profileViewModel.getUserName(profileName::setText);
                        profileViewModel.updateUser();
                        dialog.dismiss();
                    }
                });

                textCredentials.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //if you're inside a dialog, first you close the outer one and then you open a new one
                        //TODO:ask: should we implement a way to update user name/surname without closing the dialog and opening the credential one?
                        dialog.dismiss();
                        Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.popup_update2); //set content of dialog (look in layout folder for modify_profile_dialog file)
                        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

                        Button cancelBtn = dialog.findViewById(R.id.cancel_button); //button for closing dialog
                        Button confirmBtn = dialog.findViewById(R.id.confirm_button); //button to confirm profile modification

                        TextInputEditText email = dialog.findViewById(R.id.user_email);
                        TextInputEditText  password = dialog.findViewById(R.id.user_password);

                        email.setText(profileViewModel.getUser().getEmail());

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss(); //close dialog
                            }
                        });

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                profileViewModel.setEmail(email.getText().toString().trim());
                                profileViewModel.setPassword(password.getText().toString().trim());

                                profileViewModel.updateUser();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                dialog.show();
            }
        });

        updateUserLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.fragment_registration2);
                dialog.setCanceledOnTouchOutside(false);

                TextView textCreateNeighbourhood = dialog.findViewById(R.id.text_create_neigh), title = dialog.findViewById(R.id.neighbourhood_text);
                TextInputEditText street = dialog.findViewById(R.id.user_street), streetNumber = dialog.findViewById(R.id.street_number);
                Button cancelBtn = dialog.findViewById(R.id.back_button), continueBtn = dialog.findViewById(R.id.continue_button);
                AutoCompleteTextView city = dialog.findViewById(R.id.user_city), neighbourhood = dialog.findViewById(R.id.user_neigh);
                title.setText("Modifica dati residenza");//TODO: metti la stringa
                city.setText(profileViewModel.getUser().getCity());
                street.setText(profileViewModel.getUser().getStreet());
                streetNumber.setText(profileViewModel.getUser().getStreet_number());
                profileViewModel.getNeighbourhood(profileViewModel.getUser().getNeighbourhoodID(), neighbourhood::setText);

                continueBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileViewModel.setCity(city.getText().toString().trim());
                        profileViewModel.setStreet(street.getText().toString().trim());
                        profileViewModel.setStreet_number(streetNumber.getText().toString().trim());
                        profileViewModel.setNeighbourhood(neighbourhood, new MyCallback<Boolean>() {
                            @Override
                            public void onCallback(Boolean result) {
                                profileViewModel.updateUser();
                            }
                        });

                        profileViewModel.getUserName(profileName::setText);
                        dialog.dismiss();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                textCreateNeighbourhood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profileViewModel.setCity(city.getText().toString().trim());
                        profileViewModel.setStreet(street.getText().toString().trim());
                        profileViewModel.setStreet_number(streetNumber.getText().toString().trim());

                        dialog.dismiss();
                        Dialog dialog = new Dialog(getContext());
                        dialog.setContentView(R.layout.fragment_neighbourhood);
                        dialog.setCanceledOnTouchOutside(false);

                        TextInputEditText neighbourhood = dialog.findViewById(R.id.neigh_name);
                        Button backBtn = dialog.findViewById(R.id.back_button), confirmBtn = dialog.findViewById(R.id.continue_button);

                        backBtn.setText("CHIUDI"); //TODO: toString
                        confirmBtn.setText("CONFERMA"); //TODO: toString

                        backBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                profileViewModel.createNeighbourhood(neighbourhood.getText().toString().trim(), (x) -> {
                                   if (x)
                                       profileViewModel.setNeighbourhood(neighbourhood, (y) -> {
                                           profileViewModel.updateUser();

                                           profileViewModel.getUserName(profileName::setText);
                                           dialog.dismiss();
                                       });
                                   else {
                                       neighbourhood.setError("Il quartiere esiste già");
                                   }
                                });
                            }
                        });

                        dialog.show();
                    }
                });

                dialog.show();
            }
        });

        ViewPager2 viewPager2 = root.findViewById(R.id.pager);
        viewPager2.setAdapter(new ProfileViewModel.ProfileActivityViewPagerAdapter(this));
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { //when we select a tab
                viewPager2.setCurrentItem(tab.getPosition()); //the ViewPager2, using the adapter, will show the requested content
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select(); //select the correct tab based on the position of the relative page
            }
        });

        Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
        Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);

        scheduledateText = root.findViewById(R.id.scheduledate_text);
        ScheduleBar.ScheduleDate.setTextDate(scheduledateText);
        scheduledateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = ScheduleBar.ScheduleDate.showDatePickerDialog(context);

                datePickerDialog.getDatePicker().setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        ScheduleBar.ScheduleDate.setScheduleDate(calendar.getTime());
                        Utilities.day = calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    }
                });

                Button buttonOk = (Button) datePickerDialog.getButton(datePickerDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ScheduleBar.ScheduleDate.setTextDate(scheduledateText);
                        datePickerDialog.dismiss();

                        Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                        Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
                    }
                });
            }
        });

        totalActivities = root.findViewById(R.id.total_activities);

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isPressed() && !isChecked){
                    scheduledateText.setVisibility(View.VISIBLE);
                    totalActivities.setVisibility(View.INVISIBLE);
                    Utilities.day = ScheduleBar.ScheduleDate.getScheduleLocalDate();
                }
                else if (buttonView.isPressed() && isChecked){
                    scheduledateText.setVisibility(View.INVISIBLE);
                    totalActivities.setVisibility(View.VISIBLE);
                    Utilities.day = null;
                }

                Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(Utilities.day == null){
            if(!simpleSwitch.isChecked()){
                simpleSwitch.setChecked(true);
            }
            scheduledateText.setVisibility(View.INVISIBLE);
            totalActivities.setVisibility(View.VISIBLE);
        }
        else{
            if(simpleSwitch.isChecked()){
                simpleSwitch.setChecked(false);
            }
            scheduledateText.setVisibility(View.VISIBLE);
            totalActivities.setVisibility(View.INVISIBLE);
        }

        Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
        Utilities.getPosts(Utilities.day, UserManager.getUserId(), ProfileViewModel.postsFilters, PostTypes.MYPOSTS);
    }

    // requesting for camera permission if not given. Must be here because of the call inside
    private void requestCameraPermission() {
        /*requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        requestCameraPermissionsLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);*/
        //requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // checking storage permission, if given then we can add something in our storage
    private Boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    // requesting for storage permission
    private void requestStoragePermission() {
        requestCameraPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    // checking camera permission ,if given then we can click image using our camera
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Here we will click a photo and then go to startactivityforresult for updating data
    private void pickFromCamera() {
        startActivityForResult(ProfileViewModel.getCameraIntent(getContext()), IMAGE_PICKCAMERA_REQUEST);
    }

    // We will select an image from gallery
    private void pickFromGallery() {
        startActivityForResult(ProfileViewModel.getGalleryIntent(), IMAGEPICK_GALLERY_REQUEST);
    }

    private void showImagePicDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Pick Image From");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] permissions = {"Cancel", "Grant"};
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle("Grant access to camera");

                if (which == 0) {
                    if (checkCameraPermission()) pickFromCamera();
                    else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        builder1.setItems(permissions, (dialog1, which1) -> {
                            if (which1 == 1) requestCameraPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }).create().show();
                    else requestCameraPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                else if (which == 1) {
                    if (checkStoragePermission()) pickFromGallery();
                    else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE))
                        builder1.setItems(permissions, (dialog1, which1) -> {
                            if (which1 == 1) requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }).create().show();
                    else requestGalleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
        });

        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageuri = data.getData();
                uploadProfileCoverPhoto(imageuri);
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                uploadProfileCoverPhoto(imageuri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) pickFromCamera();
                    else Toast.makeText(getContext(), "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted)  pickFromGallery();
                    else  Toast.makeText(getContext(), "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    // We will upload the image from here.
    private void uploadProfileCoverPhoto(final Uri uri) { //TODO spostala da qualche altra parte
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
                                        Toast.makeText(getContext(), "Updated", Toast.LENGTH_LONG).show();
                                        Glide.with(getContext()).load(task.getResult().toString()).into(set);
                                    }
                                    else
                                        Toast.makeText(getContext(), "Error Updating ", Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: could not load the image", Toast.LENGTH_LONG).show();
            }
        });
    }
}