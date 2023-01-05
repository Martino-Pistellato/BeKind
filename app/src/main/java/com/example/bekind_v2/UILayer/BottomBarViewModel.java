package com.example.bekind_v2.UILayer;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.DataLayer.PostRepository;
import com.example.bekind_v2.DataLayer.UserDatabaseRepository;
import com.example.bekind_v2.R;
import com.example.bekind_v2.UILayer.ui.profile.ProfileViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
import com.example.bekind_v2.Utilities.RepublishTypes;
import com.example.bekind_v2.Utilities.Types;
import com.example.bekind_v2.Utilities.Utilities;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import com.example.bekind_v2.DataLayer.ProposalRepository;
import com.example.bekind_v2.DataLayer.UserManager;

public class BottomBarViewModel extends ViewModel {
    private ArrayList<String> filtersProposal;
    private ArrayList<String> filtersPost;

    private String proposalTitle, proposalBody;
    private Date proposalExpd;


    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private LatLng defaultLocation = new LatLng(41.902782, 12.496366);

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;


    public BottomBarViewModel() {
        filtersProposal = new ArrayList<>();
        filtersPost = new ArrayList<>();
        proposalTitle = "";
        proposalBody = "";
        proposalExpd = null;
    }

    public void createProposal(String title, String body, int max, Date expiringDate, double lat, double longitude, RepublishTypes choice, MyCallback<Boolean> myCallback){
        String userId = UserManager.getUserId();
        UserManager.getUser(userId, user -> {
            if(user != null)
                ProposalRepository.createProposal(title, body, expiringDate, userId, user.getNeighbourhoodID(), max, lat, longitude, choice, filtersProposal, myCallback);
        });
    }

    public static Date toDate(int year, int month, int day, int hour, int minute){
        Calendar expiringDate = Calendar.getInstance();
        expiringDate.set(year, month, day, hour, minute);
        return expiringDate.getTime();
    }

    public void createPost(String title, String body, MyCallback<Boolean> myCallback){
        PostRepository.createPost(title, body, filtersPost, result -> {
            filtersPost.clear();
            myCallback.onCallback(result);
        });
    }

    public void manageFilterProposal(String filter){
        Utilities.manageFilter(filter,filtersProposal);
    }

    public void manageFilterPost(String filter){
        Utilities.manageFilter(filter,filtersPost);
    }

    public void setExpiringHour(TimePicker expiringHour){
        //by default, we set time in timepicker to the hour and minute when the add button is clicked (+1 because of LocalDateTime implementation)
        expiringHour.setIs24HourView(true);  //time picker use 24h format
        if(this.proposalExpd==null){
            expiringHour.setHour(LocalDateTime.now().getHour() + 1);
            expiringHour.setMinute(LocalDateTime.now().getMinute());
        }else{
            expiringHour.setHour(LocalDateTime.ofInstant(this.proposalExpd.toInstant(), ZoneId.systemDefault()).getHour());
            expiringHour.setMinute(LocalDateTime.ofInstant(this.proposalExpd.toInstant(), ZoneId.systemDefault()).getMinute());
        }
    }

    public void setExpiringDate(DatePicker expiringDate){
        //by default, we set date in datepicker to today when the add button is clicked (+1/-1 because of LocalDateTime implementation)
        if(this.proposalExpd == null){
            expiringDate.updateDate(LocalDateTime.now().getYear() - 1, LocalDateTime.now().getMonthValue() + 1, LocalDateTime.now().getDayOfMonth());
        }else{
            //for some reason, if we are using the previously saved date when getting back from second page, there is no need for -1 in year, and month must be -1 and not +1
            LocalDateTime toSet = LocalDateTime.ofInstant(this.proposalExpd.toInstant(), ZoneId.systemDefault());
            expiringDate.updateDate(toSet.getYear(), toSet.getMonthValue() - 1, toSet.getDayOfMonth());
        }
        //we cannot create an activity with an expiringDate that comes before the moment we clicked the add activity button (-1000 (one second) cause we cannot use the EXACT moment)
        expiringDate.setMinDate(System.currentTimeMillis() - 1000);
    }

    public boolean checkDateConstraint(Date expiringDate){
        LocalDateTime current = LocalDateTime.now();
        Date currentDate = toDate(current.getYear(), current.getMonthValue() - 1, current.getDayOfMonth(), current.getHour(), current.getMinute());
        return expiringDate.after(currentDate);
    }

    public boolean checkConstraints(TextInputEditText title, String proposalTitle, TextInputEditText body, String proposalBody, Date proposalExpiringDate){
        if(proposalTitle.isEmpty()){ //checks if the title is empty, in that case it blocks the creation until it is filled
            title.setError("Questo campo non può essere vuoto");
            title.requestFocus();
            return false;
        }
        else if(proposalBody.isEmpty()){ //checks if the body is empty, in that case it blocks the creation until it is filled
            body.setError("Questo campo non può essere vuoto");
            body.requestFocus();
            return false;
        }
        else return checkDateConstraint(proposalExpiringDate);
    }

    public boolean checkGroupProposalConstraints(TextInputEditText maxparticipants, String proposalPartcipants) {
        if(proposalPartcipants.isEmpty()){
            maxparticipants.setError("Questo campo non può essere vuoto");
            maxparticipants.requestFocus();
            return false;
        }
        if(Integer.valueOf(proposalPartcipants) <=1){
            maxparticipants.setError("Un'attività di gruppo prevede un minimo di 2 partecipanti");
            maxparticipants.requestFocus();
            return false;
        }
        if(Integer.valueOf(proposalPartcipants) >20){
            maxparticipants.setError("Un'attività di gruppo prevede un massimo di 20 partecipanti");
            maxparticipants.requestFocus();
            return false;
        }
        return true;
    }

    public boolean checkPostConstraints(TextInputEditText title, String postTitle, TextInputEditText body, String postBody){
        if(postTitle.isEmpty()){ //checks if the title is empty, in that case it blocks the creation until it is filled
            title.setError("Questo campo non può essere vuoto");
            title.requestFocus();
            return false;
        }
        else if(postBody.isEmpty()){ //checks if the body is empty, in that case it blocks the creation until it is filled
            body.setError("Questo campo non può essere vuoto");
            body.requestFocus();
            return false;
        }
        return true;
    }
    public static void clearProposals(){
        ProposalRepository.clearProposals();
    }

    public static void clearPosts(){
        PostRepository.clearPosts();
    }


    public void showFirstPopupProposal(Context applicationContext, Dialog dialog, Dialog choose_dialog, GoogleMap[] map) {
        TextInputEditText title, body;
        DatePicker expiringDate;
        TimePicker expiringHour;
        Chip shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip;
        Button closeBtn, continueBtn;

        dialog.setContentView(R.layout.add_proposal_popup); //set content of dialog (look in layout folder for new_activity_dialog file)
        dialog.setCanceledOnTouchOutside(false); //prevents dialog to close when clicking outside of it

        shoppingChip = dialog.findViewById(R.id.shopping_chip_popup);
        houseworksChip = dialog.findViewById(R.id.houseworks_chip_popup);
        cleaningChip = dialog.findViewById(R.id.cleaning_chip_popup);
        transportChip = dialog.findViewById(R.id.transport_chip_popup);
        randomChip = dialog.findViewById(R.id.random_chip_popup);

        shoppingChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(shoppingChip.getText().toString());
            }
        });
        houseworksChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(houseworksChip.getText().toString());
            }
        });
        cleaningChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(cleaningChip.getText().toString());
            }
        });
        transportChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(transportChip.getText().toString());
            }
        });
        randomChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageFilterProposal(randomChip.getText().toString());
            }
        });

        closeBtn = dialog.findViewById(R.id.close_btn); //button for closing dialog
        continueBtn = dialog.findViewById(R.id.continue_btn);

        title = dialog.findViewById(R.id.activity_title);
        body = dialog.findViewById(R.id.activity_body);
        expiringDate = dialog.findViewById(R.id.date_picker);
        expiringHour = dialog.findViewById(R.id.time_picker);

        title.setText(proposalTitle);
        body.setText(proposalBody);
        setSelectedChips(shoppingChip, houseworksChip, cleaningChip, transportChip, randomChip);

        setExpiringHour(expiringHour);
        setExpiringDate(expiringDate);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss(); //close dialog
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date proposalExpiringDate = BottomBarViewModel.toDate(expiringDate.getYear(), expiringDate.getMonth(), expiringDate.getDayOfMonth(), expiringHour.getHour()-1, expiringHour.getMinute());
                String proposalTitle = title.getText().toString().trim(); //gets the content of the title
                String proposalBody = body.getText().toString().trim(); //gets the content of the body

                if(!checkConstraints(title, proposalTitle, body, proposalBody, proposalExpiringDate))
                    Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                else{
                    saveProposalData(proposalTitle, proposalBody, proposalExpiringDate);
                    showSecondPopupProposal(applicationContext,dialog,choose_dialog, map);
                }
            }
        });
        dialog.show();
    }

    private void setSelectedChips(Chip shoppingChip, Chip houseworksChip, Chip cleaningChip, Chip transportChip, Chip randomChip) {
        for(String s : filtersProposal){
            switch(s){
                case "Spesa": shoppingChip.setChecked(true);break;
                case "Lavori Domestici":houseworksChip.setChecked(true);break;
                case "Pulizie": cleaningChip.setChecked(true);break;
                case "Trasporto": transportChip.setChecked(true); break;
                case "Varie": randomChip.setChecked(true);break;
            }
        }
    }

    private void showSecondPopupProposal(Context applicationContext, Dialog dialog, Dialog choose_dialog, GoogleMap[] map) {
        CheckBox groupProposal, periodicProposal;
        TextInputEditText maxParticipants;
        ListView listView;
        Button backBtn, publishBtn;

        dialog.setContentView(R.layout.add_proposal_popup2);

        TextInputEditText city = dialog.findViewById(R.id.user_city), //neighbourhood = view.findViewById(R.id.user_neigh),
                     street = dialog.findViewById(R.id.user_street), streetNumber = dialog.findViewById(R.id.street_number);

        groupProposal = dialog.findViewById(R.id.group_checkbox);
        maxParticipants = dialog.findViewById(R.id.activity_maxparticipants);

        groupProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    maxParticipants.setVisibility(View.VISIBLE);
                else {
                    maxParticipants.setText("");
                    maxParticipants.setVisibility(View.GONE);
                }
            }
        });

        periodicProposal = dialog.findViewById(R.id.periodic_checkbox);
        listView = dialog.findViewById(R.id.periodic_choices);

        ArrayList<String> types = new ArrayList<>(Arrays.asList(RepublishTypes.DAILY.getNameToDisplay(), RepublishTypes.WEEKLY.getNameToDisplay(), RepublishTypes.MONTHLY.getNameToDisplay(), RepublishTypes.ANNUALLY.getNameToDisplay()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(applicationContext, android.R.layout.simple_list_item_single_choice, types);
        listView.setAdapter(adapter);
        RepublishTypes[] choice = new RepublishTypes[1];
        choice[0] = RepublishTypes.NEVER;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                choice[0] = RepublishTypes.getValue((String) listView.getItemAtPosition(i));
            }
        });

        periodicProposal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                    listView.setVisibility(View.VISIBLE);
                else
                    listView.setVisibility(View.GONE);
            }
        });

        UserManager.getUser(UserManager.getUserId(), new MyCallback<UserDatabaseRepository.User>() {
            @Override
            public void onCallback(UserDatabaseRepository.User result) {
                String cityName = result.getCity();
                city.setText(cityName.substring(0, 1).toUpperCase() + cityName.substring(1));
                city.setTextColor(applicationContext.getResources().getColor(R.color.black, applicationContext.getTheme()));
                city.setEnabled(false);
            }
        });



        backBtn = dialog.findViewById(R.id.back_btn);
        publishBtn = dialog.findViewById(R.id.publish_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFirstPopupProposal(applicationContext, dialog, choose_dialog, map);
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int proposalMaxParticipants= 1;
                boolean publish = true;
                String cityText = city.getText().toString();
                String streetNumb = streetNumber.getText().toString();
                String streetText = street.getText().toString();

                if(groupProposal.isChecked()) {
                    if (!checkGroupProposalConstraints(maxParticipants, maxParticipants.getText().toString().trim())) {
                        Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                    else{
                        proposalMaxParticipants = Integer.parseInt(maxParticipants.getText().toString().trim());
                    }
                }

                if(periodicProposal.isChecked()){
                    if(choice[0] == RepublishTypes.NEVER){
                        Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                        publish = false;
                    }
                }

                if(!checkAddress(city, cityText, street,streetText, streetNumber, streetNumb)){
                    Toast.makeText(applicationContext, "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                    publish = false;
                }

                Geocoder geo = new Geocoder(applicationContext);
                List<Address> address;
                String addresstext = streetText + ", "+streetNumb+", "+cityText;
                double lat = 0, longitude = 0;

                try {
                    address = geo.getFromLocationName(addresstext, 1);
                    if(address.size() > 0 && address.get(0).getAddressLine(0).matches(".*\\d.*")){
                        Log.e("ADDRESS", address.get(0).getAddressLine(0));
                        lat = address.get(0).getLatitude();
                        longitude = address.get(0).getLongitude();
                    }else{
                        Toast.makeText(applicationContext, "Errore: l'indirizzo inserito non è corretto", Toast.LENGTH_SHORT).show();
                        publish = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    publish = false;
                }

                if(publish){

                    createProposal(getProposalTitle(), getProposalBody(), proposalMaxParticipants, getProposalExpd(), lat, longitude, choice[0], (result -> {
                        if (result) Utilities.getProposals(Utilities.day, UserManager.getUserId(), ProfileViewModel.proposedFilters, Types.PROPOSED);
                        else Toast.makeText(applicationContext, "Errore nella pubblicazione dell'attività", Toast.LENGTH_SHORT).show();
                    }));

                    setProposalBody("");
                    setProposalTitle("");
                    setProposalExpd(null);
                    dialog.dismiss();
                    choose_dialog.dismiss();
                }

            }
        });
        dialog.show();
    }

    private boolean checkAddress(TextInputEditText city, String cityText, TextInputEditText street, String streetText, TextInputEditText streetNumber, String streetNumb) {
        if(cityText.isEmpty()){
            city.requestFocus();
            city.setError("Questo campo non può essere vuoto");
            return false;
        }
        if(streetNumb.isEmpty()){
            streetNumber.requestFocus();
            streetNumber.setError("Questo campo non può essere vuoto");
            return false;
        }
        if(streetText.isEmpty()){
            street.requestFocus();
            street.setError("Questo campo non può essere vuoto");
            return false;
        }
        return true;
    }

    private void saveProposalData(String proposalTitle, String proposalBody, Date proposalExpd) {
        this.proposalTitle=proposalTitle;
        this.proposalBody=proposalBody;
        this.proposalExpd = proposalExpd;
    }

    public String getProposalTitle() {
        return proposalTitle;
    }

    public String getProposalBody() {
        return proposalBody;
    }

    public Date getProposalExpd() {
        return proposalExpd;
    }

    public void setProposalTitle(String proposalTitle) {
        this.proposalTitle = proposalTitle;
    }

    public void setProposalBody(String proposalBody) {
        this.proposalBody = proposalBody;
    }

    public void setProposalExpd(Date proposalExpd) {
        this.proposalExpd = proposalExpd;
    }

    private void initializeMap(Context context, GoogleMap[] map, AutocompleteSupportFragment autocompleteFragment, SupportMapFragment mapFragment, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber){

        //set searchbar
        autocompleteFragment.getView().setBackground(ContextCompat.getDrawable(context, R.color.white));
        autocompleteFragment.setCountry("IT");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
        autocompleteFragment.setMenuVisibility(false);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(Place place) {
            // TODO: Get info about the selected place.
            LatLng newLatLng = place.getLatLng();
            map[0].moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
            map[0].animateCamera(CameraUpdateFactory.zoomTo(17));
            map[0].addMarker(new MarkerOptions().position(newLatLng).draggable(true));

            map[0].setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(@NonNull Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(@NonNull Marker marker) {
                    LatLng markercoord = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                    showGeocoderInfo(context, markercoord, city, street, streetNumber);
                }

                @Override
                public void onMarkerDragStart(@NonNull Marker marker) {

                }
            });
            showGeocoderInfo(context, newLatLng, city, street, streetNumber);

        }

        @Override
        public void onError(Status status) {
            // TODO: Handle the error.
            //Log.i(TAG, "An error occurred: " + status);
        }

    });

        Places.initialize(context, context.getResources().getString(R.string.google_maps_key));
    placesClient = Places.createClient(context);

    // Construct a FusedLocationProviderClient.
    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            map[0] = googleMap;

            //updateLocationUI();

            // Get the current location of the device and set the position of the map.
            //getDeviceLocation();
        }
    });
}
    private void showGeocoderInfo(Context context, LatLng coord, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(coord.latitude, coord.longitude, 1);
            city.setText(addresses.get(0).getLocality());
            String address = addresses.get(0).getAddressLine(0);
            StringTokenizer st2 = new StringTokenizer(address, ",");
            int commas=0;
            String streetname="";
            String number="";
            while (st2.hasMoreElements() && commas <2) {
                if(commas==0)
                    streetname = (String)st2.nextElement();
                else
                    number =(String)st2.nextElement();
                ++commas;
            }
            street.setText(streetname);
            streetNumber.setText(number);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
