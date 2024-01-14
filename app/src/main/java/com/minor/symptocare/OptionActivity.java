package com.minor.symptocare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionActivity extends AppCompatActivity {

    private Button patient, doctor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //Initialize id
        patient= findViewById(R.id.patient);
        doctor= findViewById(R.id.doctor);

        //Page switch by option
        patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(OptionActivity.this, PatientSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(OptionActivity.this, DoctorSignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}