package com.appmoviles.andres.gmaps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, MarkerDialog.MarkerDialogListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private LatLng position;
    private static final int REQUEST_CODE = 11;
    private LocationManager manager;

    private Marker myLocation;
    private ArrayList<Marker> markers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        markers = new ArrayList<>();
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, REQUEST_CODE);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(3.42158, -76.5205);
        myLocation = mMap.addMarker(new MarkerOptions().position(sydney).title("Yo").icon(BitmapDescriptorFactory.fromResource(R.drawable.position)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setMyLocationEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                position = latLng;
                confirmNewMarker();
            }
        });

        mMap.setOnMarkerClickListener(this);

        //Agregar el listener de ubicacion
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                myLocation.setPosition(pos);
                cercano();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        String clickCount = marker.getTitle();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            String descripcion = (String) marker.getTag();
            double distancia = distanciaCoord(marker);

            String snippet = descripcion + ", distancia: ";
            if (descripcion.equals("") || descripcion == null) {
                snippet = "Distancia: ";
            }
            String km = new DecimalFormat("#.###").format(distancia) + " KM";
            marker.setSnippet(snippet + km);
        }

        // False para que reproduzca el comportamiento del zoom
        return false;
    }


    public void confirmNewMarker() {
        DialogFragment newFragment = new MarkerDialog();
        newFragment.show(getSupportFragmentManager(), "marker_dialog");
    }

    public void cercano() {
        Marker cercano = myLocation;
        double distancia = Double.MAX_VALUE;
        for (int i = 0; i < markers.size(); i++) {
            Marker marker = markers.get(i);
            double dist = distanciaCoord(marker);
            if (dist < distancia) {
                cercano = marker;
                distancia = dist;
            }
        }
        TextView place = findViewById(R.id.place_text);
        String km = new DecimalFormat("#.###").format(distancia) + " KM";
        if(distancia <= 0.02){
            place.setText(cercano.getTitle().toUpperCase()+" ya estas en el lugar.");
        } else {
            if(cercano == myLocation){
                place.setText("");
            } else {
                place.setText(cercano.getTitle().toUpperCase()+", distancia: "+km);
            }

        }

    }


    public double distanciaCoord(Marker lejano) {
        double lat1 = myLocation.getPosition().latitude;
        double lng1 = myLocation.getPosition().longitude;
        double lat2 = lejano.getPosition().latitude;
        double lng2 = lejano.getPosition().longitude;
        double radioTierra = 6371;//en kilómetros
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
        return radioTierra * va2;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String place_name, String place_description) {
        createMarker(place_name, place_description);
    }

    public void createMarker(String title, String description) {
        Marker newMaker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(description));
        newMaker.setTag(newMaker.getSnippet());
        markers.add(newMaker);
        cercano();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
