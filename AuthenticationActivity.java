package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mychat.databinding.ActivityAuthenticationBinding;
import com.example.mychat.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends AppCompatActivity {
    String name,email,pass;
    DatabaseReference databaseReference;
    ActivityAuthenticationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=binding.email.getText().toString();
                pass=binding.password.getText().toString();
                login();
            }
        });
        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=binding.name.getText().toString();
                email=binding.email.getText().toString();
                pass=binding.password.getText().toString();
                signup();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
            if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                startActivity(new Intent(AuthenticationActivity.this,MainActivity.class));
                finish();
            }
    }

    private void login(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(),pass).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startActivity(new Intent(AuthenticationActivity.this,MainActivity.class));
                        finish();
                    }
                });
    }
    private void signup(){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(),pass).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        UserProfileChangeRequest u=new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                        firebaseUser.updateProfile(u);
                        UserModel userModel=new UserModel(FirebaseAuth.getInstance().getUid(),name,email,pass);
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel);
                        startActivity(new Intent(AuthenticationActivity.this,MainActivity.class));
                        finish();
                    }
                });
    }
}
