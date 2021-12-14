package com.ut.login.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ut.login.R;

public class MainActivity extends AppCompatActivity {

    Button login;
    TextView goToSignUp;
    EditText txtEmail, txtPassword;
    Switch switchRemember;


    private FirebaseAuth mAuth;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);


        login= findViewById(R.id.btnLogIn);
        goToSignUp = findViewById(R.id.txtLogin);
        txtEmail = findViewById(R.id.txtRegisterCorreo);
        txtPassword = findViewById(R.id.txtRegisterPassword);
        switchRemember = findViewById(R.id.switchRemember);

        txtEmail.setText(sharedPreferences.getString("rememberEmail",""));
        txtPassword.setText(sharedPreferences.getString("rememberPassword", ""));
        switchRemember.setChecked(sharedPreferences.getBoolean("remember", false));

        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Registro.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void doLogin(){
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setRemember(switchRemember.isChecked());

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
                        }
                    }
                });
    };

    public void setRemember(boolean checked){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(checked){
            editor.putString("rememberEmail", txtEmail.getText().toString().trim());
            editor.putString("rememberPassword", txtPassword.getText().toString().trim());
            editor.putBoolean("remember", checked);
        }else{
            editor.clear();
        }
        editor.apply();

    };

    private void updateUI(FirebaseUser user){
        if(user !=null){
            //SharedPreferences.Editor editor = sharedPreferences.edit();
           // editor.putString("email", user.getEmail());
            //editor.apply();

            goToHome();
        }else{
            Toast.makeText(MainActivity.this,
                    "Ocurrió un error al iniciar sesión",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToHome() {
        startActivity(new Intent(MainActivity.this, Home.class));

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToHome();
        }
    }
}