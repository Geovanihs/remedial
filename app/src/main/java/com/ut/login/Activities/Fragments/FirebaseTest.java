package com.ut.login.Activities.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.login.Activities.ShowList;
import com.ut.login.R;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FirebaseTest extends Fragment {

    View view;
    //declarando
    //FusedLocationProviderClient fusedLocationClient;
    double latitude = 0, longitude = 0;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    Button btnSendCoordinates;

    Button btnSendList;

    FirebaseFirestore db;

    String TAG = "FirebaseTest";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_firebase_test, container, false);

        db = FirebaseFirestore.getInstance();

        btnSendCoordinates = view.findViewById(R.id.btnSendCoordinates);

        btnSendList = view.findViewById(R.id.btnSendList);

        btnSendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToList();
            }
        });

        btnSendCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCoordinates();
            }
        });

        //inicializando
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null){
                    return;
                }

                for (Location location : locationResult.getLocations()){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                }
            }
        };

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        checkPermission();
    }

    private void checkPermission() {
        int permCode = 120;
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        int accessFineLocation = getActivity().
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        int accessCoarseLocation = getActivity().
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED &&
                accessCoarseLocation == PackageManager.PERMISSION_GRANTED) {
            checkGPSSensor();
        } else {
            requestPermissions(perms, permCode);
        }
    }

    private void checkGPSSensor() {
        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getCoordinates();
        } else {
            AlertNoGPS();
        }
    }

    private void AlertNoGPS() {
        //inicialización de alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //titulo
        builder.setTitle("Solicitud de Permisos");

        //mensaje o body
        builder.setMessage("Se requieren permisos de ubicación para obtener coordenadas.");

        //boton aceptar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        //boton cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //crear alertdialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }else{
            /*fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    try {
                        latitude=location.getLatitude();
                        longitude=location.getLongitude();

                        Log.d("LocationTest",latitude + " , " + longitude);
                    }catch (Exception e){
                        Log.d("LocationTest",latitude + " , " + longitude);
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al obtener coordenadas"
                                , Toast.LENGTH_SHORT).show();
                    }

                }
            });*/

           fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
           LocationRequest locationRequest = LocationRequest.create();
           locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
           locationRequest.setInterval(20000);
           locationRequest.setFastestInterval(10000);

           fusedLocationClient.requestLocationUpdates(
             locationRequest,
             locationCallback,
                   Looper.getMainLooper()
           );
        }

    }

    public void sendCoordinates(){
        //Crear la estructura
        Map<String, Object> coordinate = new HashMap<>();
        coordinate.put("Latitud", latitude);
        coordinate.put("Longitud", longitude);
        coordinate.put("date", new Date());

        db.collection("Coordinates")
                .add(coordinate)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getContext(), "Se ha guardado correctamente con el ID: "
                                + documentReference, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Coordenadas guardadas con el ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "No se pudieron guardar " +
                                " las coordenadas", Toast.LENGTH_SHORT).show();

                        Log.w(TAG, "No se guardaron las coordenadas", e);
                    }
                });
    }

    public void goToList(){
        startActivity(new Intent(getContext(), ShowList.class));

        Toast.makeText(getContext(), "Bienvenido a la lista", Toast.LENGTH_SHORT).show();

    }


    
}