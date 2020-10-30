package com.valentingonzalez.turistear.features.login

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
import com.valentingonzalez.turistear.R
import com.valentingonzalez.turistear.features.map.MapsActivity
import com.valentingonzalez.turistear.providers.AuthProvider
import dmax.dialog.SpotsDialog

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuthProvider: AuthProvider
    private var mLoginButton: MaterialButton? = null
    private var mRegisterButton: MaterialButton? = null
    private var emailTIET: TextInputEditText? = null
    private var passwordTIET: TextInputEditText? = null
    private var emailTIL: TextInputLayout? = null
    private var passTIL: TextInputLayout? = null
    private var progressDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        mAuthProvider = AuthProvider()
        if(mAuthProvider.currentUser()!=null){
            startActivity(Intent(this@LoginActivity, MapsActivity::class.java))
        }

        emailTIET = findViewById(R.id.emailTiet)
        passwordTIET = findViewById(R.id.passTiet)
        emailTIL = findViewById(R.id.emailTil)
        passTIL = findViewById(R.id.passTil)
        emailTIET?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                emailTIL?.error = ""
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        passwordTIET?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                passTIL?.error = ""
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        progressDialog = SpotsDialog.Builder().setContext(this@LoginActivity).setMessage("Conectando...").build()
        mLoginButton = findViewById(R.id.login_button)
        mLoginButton?.setOnClickListener{ login() }

        mRegisterButton = findViewById(R.id.register_button)
        mRegisterButton?.setOnClickListener{ startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
    }

    fun login() {
        val email = emailTIET!!.text.toString()
        val pass = passwordTIET!!.text.toString()
        var emptyEmail = false
        var emptyPass = false
        var under6Pass = false
        if (email.isEmpty()) {
            emailTIL!!.error = "Email no puede estar vacio"
            emptyEmail = true
        }
        if (pass.isEmpty()) {
            passTIL!!.error = "Contraseña no puede estar vacia"
            emptyPass = true
        }
        if (pass.length < 6) {
            passTIL!!.error = "Contraseña debe de tener largo mayor a 6"
            under6Pass = true
        }
        if (emptyEmail || emptyPass || under6Pass) {
            return
        }
        progressDialog!!.show()
        mAuthProvider.login(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                startActivity(Intent(this@LoginActivity, MapsActivity::class.java))
            } else {
                Toast.makeText(this@LoginActivity, "Error con email o password", Toast.LENGTH_SHORT).show()
            }
            progressDialog!!.dismiss()
        }
    }
}