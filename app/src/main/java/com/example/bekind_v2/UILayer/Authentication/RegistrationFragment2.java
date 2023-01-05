package com.example.bekind_v2.UILayer.Authentication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.bekind_v2.DataLayer.NeighbourhoodRepository;
import com.example.bekind_v2.R;
//import com.example.bekind_v2.UILayer.NeighbourhoodActivity;
import com.example.bekind_v2.UILayer.BottomBar;
import com.example.bekind_v2.UILayer.NeighbourhoodFragment;
import com.example.bekind_v2.Utilities.MapViewModel;
import com.example.bekind_v2.Utilities.MyCallback;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class RegistrationFragment2 extends Fragment {

    private AuthenticationViewModel authenticationViewModel;
    private MapViewModel mapViewModel;

    /*
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
    */

    public RegistrationFragment2(AuthenticationViewModel authenticationViewModel, MapViewModel mapViewModel) {
        this.authenticationViewModel = authenticationViewModel;
        this.mapViewModel = mapViewModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_registration2, container, false);

        TextView textCreateNeighbourhood = view.findViewById(R.id.text_create_neigh);
        TextInputEditText city = view.findViewById(R.id.user_city), //neighbourhood = view.findViewById(R.id.user_neigh),
                street = view.findViewById(R.id.user_street), streetNumber = view.findViewById(R.id.street_number);
        Button backBtn = view.findViewById(R.id.back_button), continueBtn = view.findViewById(R.id.continue_button);
        AutoCompleteTextView neighbourhood = view.findViewById(R.id.user_neigh);
        //ProgressBar progressBar = view.findViewById(R.id.progressbar);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapViewModel.initializeMap(getActivity(), getContext(), autocompleteFragment, mapFragment, city, street, streetNumber);

        authenticationViewModel.getLocationData(city, neighbourhood, street, streetNumber);

        if (city.getText().toString().isEmpty())
            neighbourhood.setEnabled(false);

        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!city.getText().toString().isEmpty())
                    neighbourhood.setEnabled(true);
                else
                    neighbourhood.setEnabled(false);
            }
        });

        neighbourhood.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    NeighbourhoodRepository.getNeighbourhoods(city.getText().toString().toLowerCase(), new MyCallback() {
                        @Override
                        public void onCallback(Object result) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, (ArrayList<String>) result);
                            neighbourhood.setAdapter(adapter);
                        }
                    });
                }
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                        userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();

                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                authenticationViewModel.changeFragment(getActivity(), R.id.fragment_registration2, authenticationViewModel, mapViewModel);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCity = city.getText().toString().trim(), userNeighbourhood = neighbourhood.getText().toString().trim(),
                        userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();

                authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);
                authenticationViewModel.checkLocationFields(city, userCity, neighbourhood, userNeighbourhood, street, userStreet, streetNumber, userStreetNumber, new MyCallback() {
                    @Override
                    public void onCallback(Object result) {
                        if (!(boolean) result) {
                            Toast.makeText(getContext(), "Errore: i campi non sono stati riempiti correttamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Create USER STEP 1", "call to authentication view model");
                            authenticationViewModel.createUser(getContext(), new MyCallback() {
                                @Override
                                public void onCallback(Object result) {
                                    if (result != null) {
                                        Log.e("STEP 7", "here if link is clicked, ready to start app");
                                        startActivity(new Intent(getContext(), BottomBar.class));
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        textCreateNeighbourhood.setOnClickListener((v) -> {
            String userCity = city.getText().toString().trim(), userNeighbourhood = "",
                    userStreet = street.getText().toString().trim(), userStreetNumber = streetNumber.getText().toString().trim();
            authenticationViewModel.saveLocationData(userCity, userNeighbourhood, userStreet, userStreetNumber);

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            NeighbourhoodFragment neighbourhoodFragment = new NeighbourhoodFragment(authenticationViewModel, mapViewModel);
            fragmentTransaction.replace(R.id.fragment_container, neighbourhoodFragment).commit();
        }); //TODO:make a generic replacefragment

        return view;
    }
}

//TODO cancel all down here










/*
    private void initializeMap(Context context, GoogleMap[] map, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber){

        //set searchbar
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.getView().setBackground(ContextCompat.getDrawable(getContext(), R.color.white));
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
                        showGeocoderInfo(markercoord, city, street, streetNumber);
                    }

                    @Override
                    public void onMarkerDragStart(@NonNull Marker marker) {

                    }
                });
                showGeocoderInfo(newLatLng, city, street, streetNumber);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }

        });

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        Places.initialize(getContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(getContext());

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map[0] = googleMap;

                updateLocationUI();

                // Get the current location of the device and set the position of the map.
                getDeviceLocation();
            }
        });
    }

    private void showGeocoderInfo(LatLng coord, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         *//*
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        mapViewModel.updateLocationUI(getContext(), getActivity());
    }


    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (map[0] == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map[0].setMyLocationEnabled(true);
                map[0].getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map[0].setMyLocationEnabled(false);
                map[0].getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }*/
/*
    @SuppressWarnings("MissingPermission")
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         *//*
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.e("HERE", "FIND YOR LOCATION" + lastKnownLocation.toString());
                                map[0].moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                LatLng home = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                                map[0].addMarker(new MarkerOptions()
                                        .position(home));
                            }else {
                                map[0].moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                                map[0].getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }*/
