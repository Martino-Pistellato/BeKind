package com.example.bekind_v2.Utilities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;

import com.example.bekind_v2.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MapViewModel extends ViewModel implements ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap[] map = null;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private LatLng defaultLocation = new LatLng(41.902782, 12.496366);

    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final int DEFAULT_ZOOM = 15;

    private Context context;
    private FragmentActivity fragmentActivity;

    public void setMap(SupportMapFragment mapFragment, LatLng coord, Marker[] marker){

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                map[0] = googleMap;
                map[0].moveCamera(CameraUpdateFactory.newLatLng(coord));
                map[0].animateCamera(CameraUpdateFactory.zoomTo(17));
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(coord.latitude, coord.longitude, 1, new Geocoder.GeocodeListener() {
                            @Override
                            public void onGeocode(@NonNull List<Address> list) {
                                if(list.size() > 0)
                                    marker[0] = map[0].addMarker(new MarkerOptions().position(coord).title(list.get(0).getAddressLine(0)));//todo add address
                            }
                        });
                    }
                    else{
                        List<Address> addresses = geocoder.getFromLocation(coord.latitude, coord.longitude, 1);
                        if(addresses.size() > 0)
                            marker[0] = map[0].addMarker(new MarkerOptions().position(coord).title(addresses.get(0).getAddressLine(0)));//todo add address

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void initializeMap(FragmentActivity activity, Context context, AutocompleteSupportFragment autocompleteFragment, SupportMapFragment mapFragment, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber, LatLng coord) {

            this.fragmentActivity = activity;
            this.context = context;
            this.map = new GoogleMap[1];

            Marker[] marker = new Marker[1];

            //set searchbar
            if (autocompleteFragment != null) {
                autocompleteFragment.getView().setBackground(ContextCompat.getDrawable(context, R.color.white));
                autocompleteFragment.setCountry("IT");
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));
                autocompleteFragment.setMenuVisibility(false);

                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        LatLng newLatLng = place.getLatLng();
                        map[0].moveCamera(CameraUpdateFactory.newLatLng(newLatLng));
                        map[0].animateCamera(CameraUpdateFactory.zoomTo(17));
                        if (marker[0] != null)
                            marker[0].remove();

                        marker[0] = map[0].addMarker(new MarkerOptions().position(newLatLng).draggable(true));

                        map[0].setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDrag(@NonNull Marker marker) { }

                            @Override
                            public void onMarkerDragEnd(@NonNull Marker marker) {
                                LatLng markercoord = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                                showGeocoderInfo(markercoord, city, street, streetNumber);
                            }

                            @Override
                            public void onMarkerDragStart(@NonNull Marker marker) { }
                        });

                        showGeocoderInfo(newLatLng, city, street, streetNumber);
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        //Log.i(TAG, "An error occurred: " + status);
                    }

                });
            }

            Places.initialize(context, context.getResources().getString(R.string.google_maps_key));
            placesClient = Places.createClient(context);

            // Construct a FusedLocationProviderClient.
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

            if (coord != null)
                setMap(mapFragment, coord, marker);
            else {
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        map[0] = googleMap;

                        updateLocationUI(map[0]);

                        // Get the current location of the device and set the position of the map.
                        //getDeviceLocation();
                    }
                });
            }

    }

    public LatLng getCoordinatesFromAddress(Context context, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber){

        Geocoder geo = new Geocoder(context, Locale.getDefault());
        LatLng[] latLng = new LatLng[1];
        latLng[0] = null;
        List<Address> address;
        String cityText = city.getText().toString().trim(), streetText = street.getText().toString().trim(),
                streetNumb =streetNumber.getText().toString().trim();

        if( ( !cityText.isEmpty() ) && ( !streetText.isEmpty() ) && ( !streetNumb.isEmpty() ) ) {
            String addressText = streetText + ", " + streetNumb + ", " + cityText;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geo.getFromLocationName(addressText, 1, new Geocoder.GeocodeListener() {
                        @Override
                        public void onGeocode(@NonNull List<Address> list) {
                            if (list.size() > 0 && list.get(0).getAddressLine(0).matches(".*\\d.*")) {
                                Address ad = list.get(0);
                                latLng[0] = new LatLng(ad.getLatitude(), ad.getLongitude());
                            }
                        }
                    });
                }
                else {
                    address = geo.getFromLocationName(addressText, 1);
                    if (address.size() > 0 && address.get(0).getAddressLine(0).matches(".*\\d.*")) {
                        Address ad = address.get(0);
                       latLng[0] = new LatLng(ad.getLatitude(), ad.getLongitude());
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        return latLng[0];
    }

    private void showGeocoderInfo(LatLng coord, TextInputEditText city, TextInputEditText street, TextInputEditText streetNumber) {
        Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());

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
                else{
                    //ToDO check if number is wrong sometimes
                    String numberText = (String)st2.nextElement();
                    if(numberText.length()<5)
                        number = numberText;
                }
                ++commas;
            }
            street.setText(streetname);
            streetNumber.setText(number);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getLocationPermission(Context context, FragmentActivity activity) {
        /* Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult. */
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressWarnings("MissingPermission")
    public void updateLocationUI(GoogleMap map) {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission(this.context, this.fragmentActivity);
            }
        } catch (SecurityException e)  { }
    }

    @SuppressWarnings("MissingPermission")
    private void getDeviceLocation() {
        Marker marker = null;
        /* Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available. */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this.fragmentActivity, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
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
                        }else {
                            map[0].moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map[0].getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  { }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) { // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI(this.map[0]);
    }
}
