package com.ut.login.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.ut.login.R;

import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    Button signup;
    TextView goToLogin;
    EditText txtEmail, txtPassword, txtSignUpName, txtSignUpLastName,txtPhone;
    RadioGroup radioGroupGender;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    int gender;

    ProgressDialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db  = FirebaseFirestore.getInstance();

        dialog = new ProgressDialog(Registro.this);


        signup = findViewById(R.id.btnLogIn);
        goToLogin = findViewById(R.id.txtLogin);
        txtEmail = findViewById(R.id.txtRegisterCorreo);
        txtPassword = findViewById(R.id.txtRegisterPassword);
        txtSignUpName = findViewById(R.id.txtSignUpName);
        txtSignUpLastName = findViewById(R.id.txtSignUpLastName);
        txtPhone = findViewById(R.id.txtPhone);
        radioGroupGender = findViewById(R.id.radioGroupGender);



        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radioButtonMale){
                    gender = 0;
                }else{
                    gender = 1;
                }
            }
        });




        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginMethod();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(false);
                dialog.setTitle("Registro");
                dialog.setMessage("Validando usuario...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                verifyUser();
            }
        });
    }

    public void verifyUser(){
        db.collection("Users").document(txtEmail.getText().toString().trim())
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        dialog.dismiss();
                        Toast.makeText(Registro.this, "Usuario Registrado anteriormente, " +
                                "Inicie Sesión", Toast.LENGTH_SHORT).show();
                        goToLoginMethod();
                    } else {
                        addUserToDatabase();
                    }
                } else {
                    dialog.dismiss();
                }
            }
        });

    }

    private void addUserToDatabase(){
        dialog.setMessage("Agregando a base de datos");
        Map<String, Object> user = new HashMap<>();
        user.put("Nombre", txtSignUpName.getText().toString());
        user.put("Apellido", txtSignUpLastName.getText().toString());
        user.put("Teléfono", txtPhone.getText().toString());
        user.put("Género", gender);
        user.put("photo", "");

        db.collection("Users").document(txtEmail.getText().toString().trim())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        signUpUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(Registro.this, "No se ha podido registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToLoginMethod(){
        startActivity(new Intent(Registro.this, MainActivity.class));
    }




    private void signUpUser(){
        dialog.setMessage("Registrando usuario");
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    };

    private void updateUI(FirebaseUser user){
        if(user !=null){
            Toast.makeText(Registro.this,
                    "Registro Exitoso!!",
                    Toast.LENGTH_SHORT).show();
            goToLoginMethod();
        }else{
            dialog.dismiss();
            Toast.makeText(Registro.this,
                    "Ocurrió un error al registrar",
                    Toast.LENGTH_SHORT).show();
        }
    }
}