package com.minor.symptocare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpasswordActivity extends AppCompatActivity {

    private TextInputEditText editTextForget;
    private Button buttonForget;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        editTextForget=findViewById(R.id.editTextForget);
        buttonForget=findViewById(R.id.buttonForget);

        buttonForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextForget.getText().toString();
                if(!email.equals(""))
                {
                    passwordReset(email);
                }
            }
        });

        auth = FirebaseAuth.getInstance();

    }

    public void passwordReset(String email)
    {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(forgotpasswordActivity.this, "Please check you email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(forgotpasswordActivity.this, "There is a problem", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}