package com.valentingonzalez.turistear.providers;

import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.valentingonzalez.turistear.models.Usuario;

import java.util.HashMap;
import java.util.Map;

public class UserProvider {
    DatabaseReference mFirebaseDatabase;

    public UserProvider(){
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Usuarios");
    }

    public Task<Void> createUser(Usuario usuario){
        Map<String, Object> map = new HashMap<>();
        map.put("name", usuario.getName());
        map.put("email", usuario.getEmail());
        return mFirebaseDatabase.child(usuario.getId()).setValue(map);
    }
    public void  getUser(final TextView nameView){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("PROVIDER",currentUser);
        mFirebaseDatabase.child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Usuario u = snapshot.getValue(Usuario.class);
                nameView.setText(u.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
