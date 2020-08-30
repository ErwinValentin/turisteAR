package com.valentingonzalez.turistear.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthProvider {
    var mAuth: FirebaseAuth
    fun register(email: String?, password: String?): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email!!, password!!)
    }

    fun login(email: String?, password: String?): Task<AuthResult> {
        return mAuth.signInWithEmailAndPassword(email!!, password!!)
    }

    fun currentUser(): FirebaseUser? {
        if(mAuth.currentUser != null){
            return mAuth.currentUser!!
        }else{
            return null
        }
    }
    init {
        mAuth = FirebaseAuth.getInstance()
    }
}