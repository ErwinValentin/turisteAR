package com.valentingonzalez.turistear.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class AuthProvider {
    var mAuth: FirebaseAuth
    fun register(email: String?, password: String?): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email!!, password!!)
    }

    fun login(email: String?, password: String?): Task<AuthResult> {
        return mAuth.signInWithEmailAndPassword(email!!, password!!)
    }

    init {
        mAuth = FirebaseAuth.getInstance()
    }
}