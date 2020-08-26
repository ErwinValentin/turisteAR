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
import com.google.firebase.auth.FirebaseAuth;
import com.valentingonzalez.turistear.activities.MapsActivity;
import com.valentingonzalez.turistear.R;
import com.valentingonzalez.turistear.includes.BasicToolbar;
import com.valentingonzalez.turistear.models.Usuario;
import com.valentingonzalez.turistear.providers.AuthProvider;
import com.valentingonzalez.turistear.providers.UserProvider;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    UserProvider mUserProvider;
    MaterialButton mCreateButton;
    TextInputEditText nameTIET;
    TextInputEditText emailTIET;
    TextInputEditText passwordTIET;
    TextInputLayout nameTil;
    TextInputLayout emailTil;
    TextInputLayout passTil;
    AlertDialog progessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        mCreateButton = findViewById(R.id.register_button);
        BasicToolbar.show(RegisterActivity.this, "Registrarse", true);

        mAuthProvider = new AuthProvider();
        mUserProvider = new UserProvider();

        nameTIET = findViewById(R.id.nameTiet);
        emailTIET = findViewById(R.id.emailTiet);
        passwordTIET = findViewById(R.id.passTiet);
        nameTil = findViewById(R.id.nameTil);
        emailTil = findViewById(R.id.emailTil);
        passTil = findViewById(R.id.passTil);

        nameTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameTil.setError("");
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        emailTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                emailTil.setError("");
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        passwordTIET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passTil.setError("");
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
        progessDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Conectando...").build();
    }

    public void createAccount(){
        final String name = nameTIET.getText().toString();
        final String email = emailTIET.getText().toString();
        String pass = passwordTIET.getText().toString();
        boolean emptyName = false, emptyEmail = false, emptyPass= false, under6Pass= false;
        if(email.isEmpty()){
            emailTil.setError("Email no puede estar vacio");
            emptyEmail = true;
        }
        if(name.isEmpty()){
            nameTil.setError("Nombre no puede estar vacio");
            emptyName = true;
        }
        if(pass.isEmpty()){
            passTil.setError("Contraseña no puede estar vacia");
            emptyPass = true;
        }
        if(pass.length() < 6){
            passTil.setError("Contraseña debe de tener largo mayor a 6");
            under6Pass = true;
        }
        if(emptyName || emptyEmail || emptyPass || under6Pass){
            return;
        }
        progessDialog.show();
        register(email, name, pass);
    }

    public void register(final String email,final String name, String pass){
        mAuthProvider.register(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Usuario usuario = new Usuario(id, name, email);
                    createUser(usuario);
                }else{
                    Toast.makeText(RegisterActivity.this, "Error con email o password", Toast.LENGTH_SHORT).show();
                }
                progessDialog.dismiss();
            }
        });
    }

    public void createUser(Usuario usuario){
        mUserProvider.createUser(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(RegisterActivity.this, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MapsActivity.class));
                }else{
                    Toast.makeText(RegisterActivity.this, "Error con creacion de usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
