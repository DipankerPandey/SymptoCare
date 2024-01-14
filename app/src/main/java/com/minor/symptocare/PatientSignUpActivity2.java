package com.minor.symptocare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PatientSignUpActivity2 extends AppCompatActivity {

    private TextInputEditText editTextAgeSignup, editTextDegreeSignup;
    private TextView textViewSign;
    private Button buttonSignup;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up2);

        editTextAgeSignup=findViewById(R.id.editTextAgeSignup_Patient);
        editTextDegreeSignup=findViewById(R.id.editTextDisablitySignup_Patient);
        textViewSign=findViewById(R.id.textViewSign_Patient);
        buttonSignup=findViewById(R.id.buttonSignup_Patient);

        database= FirebaseDatabase.getInstance();
        reference=database.getReference();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String age= editTextAgeSignup.getText().toString();
                String degree= editTextDegreeSignup.getText().toString();
                reference.child("Users").child(firebaseUser.getUid()).child("Age").setValue(age);
                reference.child("Users").child(firebaseUser.getUid()).child("Disability").setValue(degree);
                reference.child("Users").child(firebaseUser.getUid()).child("Occupation").setValue("Patient");
                Intent intent= new Intent(PatientSignUpActivity2.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        textViewSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(PatientSignUpActivity2.this,SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}