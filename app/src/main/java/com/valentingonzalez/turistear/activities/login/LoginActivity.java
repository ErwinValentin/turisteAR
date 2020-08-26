package com.valentingonzalez.turistear.activities.login;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.valentingonzalez.turistear.activities.MapsActivity;
import com.valentingonzalez.turistear.R;
import com.valentingonzalez.turistear.providers.AuthProvider;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    AuthProvider mAuthProvider;
    MaterialButton mLoginButton;
    MaterialButton mRegisterButton;
    TextInputEditText emailTIET;
    TextInputEditText passwordTIET;
    TextInputLayout emailTIL;
    TextInputLayout passTIL;

    AlertDialog progessDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        emailTIET = findViewById(R.id.emailTiet);
        passwordTIET = findViewById(R.id.passTiet);
        emailTIL = findViewById(R.id.emailTil);
        passTIL = findViewById(R.id.passTil);
        emailTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailTIL.setError("");
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        passwordTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passTIL.setError("");
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        mAuthProvider = new AuthProvider();
        progessDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Conectando...").build();
        mLoginButton = findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        mRegisterButton = findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }
    public void login(){
        String email = emailTIET.getText().toString();
        String pass = passwordTIET.getText().toString();
        boolean emptyEmail = false, emptyPass= false, under6Pass= false;
        if(email.isEmpty()){
            emailTIL.setError("Email no puede estar vacio");
            emptyEmail = true;
        }
        if(pass.isEmpty()){
            passTIL.setError("Contraseña no puede estar vacia");
            emptyPass = true;
        }
        if(pass.length() < 6){
            passTIL.setError("Contraseña debe de tener largo mayor a 6");
            under6Pass = true;
        }
        if(emptyEmail || emptyPass || under6Pass){
            return;
        }
        progessDialog.show();
        mAuthProvider.login(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Error con email o password", Toast.LENGTH_SHORT).show();
                }
                progessDialog.dismiss();
            }
        });
    }

}
