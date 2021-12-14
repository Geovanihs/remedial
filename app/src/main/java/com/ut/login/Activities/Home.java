package com.ut.login.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.ut.login.R;
import com.ut.login.databinding.ActivityHomeBinding;

import java.util.Map;


public class Home extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    SharedPreferences sharedPreferences;

    ImageView profile;
    TextView name, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_firebase_test, R.id.nav_checker, R.id.nav_checker_list)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View viewProfile = navigationView.getHeaderView(0);

        profile = viewProfile.findViewById(R.id.imgProfilePhoto);
        name = viewProfile.findViewById(R.id.txtProfileName);
        email = viewProfile.findViewById(R.id.txtProfileEmail);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        getProfileData();
    }

    private void getProfileData() {
        db.collection("Users").document(mAuth.getCurrentUser().getEmail())
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        name.setText(document.getString("Nombre") + " " + document.getString("Apellido"));
                        email.setText(document.getId());

                        Picasso.get()
                                .load("https://res.cloudinary.com/dr50bobnw/image/upload/" +
                                        document.getString("photo"))
                                .into(profile);

                    } else {
                        Toast.makeText(Home.this, "el documento no existe"
                                , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Home.this, "Ocurrió un error al cargar datos"
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkPermission() {
        int permCode = 120;
        String[] perms = {Manifest.permission.CAMERA};

        int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);

        if (cameraPermission == PackageManager.PERMISSION_GRANTED ) {
            openCamera();
        } else {
            requestPermissions(perms, permCode);
        }
    }

    private void openCamera(){
        CropImage.activity().start(Home.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();

                //Toast.makeText(Home.this, "" + resultUri, Toast.LENGTH_SHORT).show();

                uploadImageToCloudinary(resultUri);
            }
        }
    }

    private void uploadImageToCloudinary(Uri resultUri) {
        MediaManager.get().upload(resultUri).unsigned("salchipapa").callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                String public_id = resultData.get("public_id").toString();
                saveImageIntoFirestore(public_id);
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {

            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch();
    }

    private void saveImageIntoFirestore(String public_id){
         db.collection("Users").document(mAuth.getCurrentUser().getEmail())
                .update("photo", public_id)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Home.this, "Foto Guardada con éxito!", Toast.LENGTH_SHORT).show();
                        Picasso.get()
                                .load("https://res.cloudinary.com/dckmux8bb/image/upload/" + public_id)
                                .into(profile);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Home.this, "Ocurrió un error al guardar la foto"
                                , Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //usuario hace click para cerrar sesión
            case R.id.action_logout:
                doLogout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//método para cerrar sesión
    private void doLogout() {
        //SharedPreferences.Editor editor = sharedPreferences.edit();
        //editor.clear();
        //editor.remove("email");
        //editor.apply();


        //funcion de firebase para cerrar sesion y borrar datos
        mAuth.signOut();
        //intent para redireccionar al login
        startActivity(new Intent(Home.this,MainActivity.class));
        //función para no permitir ir hacia atras
        finish();
    }


}