package com.example.bekind_v2.UILayer.ui.profile;

import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.checkCameraPermission;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.checkStoragePermission;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.imageuri;
import static com.example.bekind_v2.DataLayer.ProfilePictureRepository.profileOrCoverPhoto;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.setImageUri;
import static com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel.uploadProfileCoverPhoto;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.bekind_v2.DataLayer.UserManager;
import com.example.bekind_v2.R;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.UpdateUserLocationDialog;
import com.example.bekind_v2.Utilities.PostTypes;
import com.example.bekind_v2.Utilities.ScheduleBar;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.example.bekind_v2.databinding.FragmentProfileBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.time.ZoneId;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private SwitchCompat simpleSwitch;
    private TextView totalActivities, scheduledateText;
    ProgressBar progressBar;

    CircleImageView profilePic;
    private final ActivityResultLauncher<String> requestWriteExternalStoragePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) pickFromCamera();
        else Toast.makeText(getContext(), "Write external storage permissions not granted", Toast.LENGTH_LONG).show();
    });
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) requestWriteExternalStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            else Toast.makeText(getContext(), "Camera permissions not granted", Toast.LENGTH_LONG).show();
    });
    private final ActivityResultLauncher<String> requestGalleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) pickFromGallery();
        else Toast.makeText(getContext(), "Gallery permissions not granted", Toast.LENGTH_LONG).show();
    });
    private final ActivityResultLauncher<Uri> pickFromCamera = registerForActivityResult(new ActivityResultContracts.TakePicture(), res -> { if(res) uploadProfileCoverPhoto(imageuri, getContext(), profilePic, progressBar); });
    private final ActivityResultLauncher<String> pickFromGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), res -> { uploadProfileCoverPhoto(imageuri = res, getContext(), profilePic, progressBar); });

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Context context = this.getContext();
        Button updateUserData = binding.modifyProfileBtn, updateUserLocation = binding.modifyNeighBtn;
        TextView profileName = binding.profileName;
        ViewPager2 viewPager2 = root.findViewById(R.id.pager);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        simpleSwitch = root.findViewById(R.id.simpleSwitch);
        totalActivities = root.findViewById(R.id.total_activities);
        scheduledateText = root.findViewById(R.id.scheduledate_text);
        profilePic = root.findViewById(R.id.user_photo);
        scheduledateText = root.findViewById(R.id.scheduledate_text);
        totalActivities = root.findViewById(R.id.total_activities);
        progressBar = root.findViewById(R.id.progressbar);

        progressBar.setVisibility(View.VISIBLE);
        UserManager.getUser(UserManager.getUserId(), (user) ->{
            String image = user.getImage();
            if(!image.isEmpty()) {
                Glide.with(this).load(image).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(profilePic);
            }
        });

        profileViewModel.getUserName(profileName::setText);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                DialogFragment dialog = new UpdateUserLocationDialog(profileViewModel, mapViewModel, profileName);


                dialog.show(getChildFragmentManager(), null);
            }
        });

        viewPager2.setAdapter(new ProfileViewModel.ProfileActivityViewPagerAdapter(this));
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

    private void pickFromCamera() {
        setImageUri(getContext());
        pickFromCamera.launch(imageuri);
    }

    private void pickFromGallery() {
        pickFromGallery.launch("image/*");
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
                    if (checkCameraPermission(getContext())) pickFromCamera();
                    else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        builder1.setItems(permissions, (dialog1, which1) -> {
                            if (which1 == 1) requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        }).create().show();
                    else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
                else if (which == 1) {
                    if (checkStoragePermission(getContext())) pickFromGallery();
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
}