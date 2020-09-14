package com.valentingonzalez.turistear.activities.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.activities.MapsActivity
import com.valentingonzalez.turistear.includes.BasicToolbar.show
import com.valentingonzalez.turistear.models.Usuario
import com.valentingonzalez.turistear.providers.AuthProvider
import com.valentingonzalez.turistear.providers.UserProvider
import dmax.dialog.SpotsDialog

class RegisterActivity : AppCompatActivity() , UserProvider.UserProviderListener{
    var mAuthProvider: AuthProvider? = AuthProvider()
    var mUserProvider: UserProvider = UserProvider(this)
    var mCreateButton: MaterialButton? = null
    var nameTIET: TextInputEditText? = null
    var emailTIET: TextInputEditText? = null
    var passwordTIET: TextInputEditText? = null
    var nameTil: TextInputLayout? = null
    var emailTil: TextInputLayout? = null
    var passTil: TextInputLayout? = null
    var progessDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_layout)
        mCreateButton = findViewById(R.id.register_button)
        show(this@RegisterActivity, "Registrarse", true)
        nameTIET = findViewById(R.id.nameTiet)
        emailTIET = findViewById(R.id.emailTiet)
        passwordTIET = findViewById(R.id.passTiet)
        nameTil = findViewById(R.id.nameTil)
        emailTil = findViewById(R.id.emailTil)
        passTil = findViewById(R.id.passTil)
        nameTIET?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                nameTil?.error = ""
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        emailTIET?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                emailTil?.error = ""
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        passwordTIET?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                passTil?.error = ""
            }
            override fun afterTextChanged(editable: Editable) {}
        })
        mCreateButton?.setOnClickListener{ createAccount() }
        progessDialog = SpotsDialog.Builder().setContext(this@RegisterActivity).setMessage("Conectando...").build()
    }
    fun createAccount() {
        val name = nameTIET!!.text.toString()
        val email = emailTIET!!.text.toString()
        val pass = passwordTIET!!.text.toString()
        var emptyName = false
        var emptyEmail = false
        var emptyPass = false
        var under6Pass = false
        if (email.isEmpty()) {
            emailTil!!.error = "Email no puede estar vacio"
            emptyEmail = true
        }
        if (name.isEmpty()) {
            nameTil!!.error = "Nombre no puede estar vacio"
            emptyName = true
        }
        if (pass.isEmpty()) {
            passTil!!.error = "Contraseña no puede estar vacia"
            emptyPass = true
        }
        if (pass.length < 6) {
            passTil!!.error = "Contraseña debe de tener largo mayor a 6"
            under6Pass = true
        }
        if (emptyName || emptyEmail || emptyPass || under6Pass) {
            return
        }
        progessDialog!!.show()
        register(email, name, pass)
    }

    fun register(email: String?, name: String?, pass: String?) {
        mAuthProvider!!.register(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val id = FirebaseAuth.getInstance().currentUser!!.uid
                val usuario = Usuario(id, name, email)
                createUser(usuario)
            } else {
                Toast.makeText(this@RegisterActivity, "Error con email o password", Toast.LENGTH_SHORT).show()
            }
            progessDialog!!.dismiss()
        }
    }

    fun createUser(usuario: Usuario?) {
        mUserProvider.createUser(usuario!!).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@RegisterActivity, "¡Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, MapsActivity::class.java))
            } else {
                Toast.makeText(this@RegisterActivity, "Error con creacion de usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onFavoriteChecked(isFav: List<Boolean>) {
    }
    override fun getUserName(user: Usuario) {
    }
}