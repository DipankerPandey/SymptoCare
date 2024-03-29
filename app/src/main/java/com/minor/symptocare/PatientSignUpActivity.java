package com.minor.symptocare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientSignUpActivity extends AppCompatActivity {

    private CircleImageView imageViewCircle;
    private TextInputEditText editTextEmailSignup, editTextPasswordSignup, editTextUsernameSignup, editTextPasswordConfirm;
    private Button buttonNext;
    boolean imageControl = false;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_sign_up);

        imageViewCircle=findViewById(R.id.imageViewCircle_Patient);
        editTextEmailSignup=findViewById(R.id.editTextEmailSignup_Patient);
        editTextPasswordSignup=findViewById(R.id.editTextPasswordSignup_Patient);
        editTextUsernameSignup=findViewById(R.id.editTextUsernameSignup_Patient);
        editTextPasswordConfirm=findViewById(R.id.editTextPasswordConfirm_Patient);
        buttonNext=findViewById(R.id.buttonNext_Patient);

        auth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        reference=database.getReference();

        firebaseStorage= FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();

        imageViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = editTextEmailSignup.getText().toString();
                String password = editTextPasswordSignup.getText().toString();
                String userName= editTextUsernameSignup.getText().toString();

                if(!email.equals("") && !password.equals("") && !userName.equals(""))
                {
                    signup(email, password,userName);
                }
            }
        });

    }

    public void imageChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewCircle);
            imageControl = true;
        }

        else {
            imageControl = false;
        }
    }

    public void signup(String email, String password, String username)
    {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    reference.child("Users").child(auth.getUid()).child("username").setValue(username);
                    if(imageControl)
                    {
                        UUID randomID= UUID.randomUUID();
                        String imageName="images/"+randomID+".jpg";
                        storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference myStorageRef= firebaseStorage.getReference(imageName);
                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String filePath=uri.toString();
                                        reference.child("Users").child(auth.getUid()).child("image").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(PatientSignUpActivity.this, "Write to database is successful", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(PatientSignUpActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                    else
                    {
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }

                    Intent intent = new Intent(PatientSignUpActivity.this, PatientSignUpActivity2.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(PatientSignUpActivity.this, "There is a problem Signing up", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}