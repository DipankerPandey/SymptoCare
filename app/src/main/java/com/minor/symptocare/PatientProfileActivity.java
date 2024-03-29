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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientProfileActivity extends AppCompatActivity {

    private CircleImageView imageViewCircleProfile;
    private TextInputEditText editTextUserNameProfile, editTextAgeProfile, editTextDegreeProfile;
    private Button buttonUpdate, buttonSignOut;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    String image;

    Uri imageUri;
    boolean imageControl=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        imageViewCircleProfile = findViewById(R.id.imageViewCircleProfile_Patient);
        editTextUserNameProfile = findViewById(R.id.editTextUserNameProfile_Patient);
        buttonUpdate = findViewById(R.id.buttonUpdate_Patient);
        editTextAgeProfile = findViewById(R.id.editTextAgeProfile_Patient);
        editTextDegreeProfile = findViewById(R.id.editTextDisablityProfile_Patient);
        buttonSignOut = findViewById(R.id.LogOut_Patient);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        firebaseStorage= FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();

        getUserInfo();

        imageViewCircleProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imageChooser();

            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });

        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(PatientProfileActivity.this,SignInActivity.class));
                finish();
            }
        });
    }

    public void updateProfile()
    {
        String username = editTextUserNameProfile.getText().toString();
        String age = editTextAgeProfile.getText().toString();
        String degree = editTextDegreeProfile.getText().toString();
        reference.child("Users").child(firebaseUser.getUid()).child("username").setValue(username);
        reference.child("Users").child(firebaseUser.getUid()).child("Age").setValue(age);
        reference.child("Users").child(firebaseUser.getUid()).child("Disability").setValue(degree);

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
                                    Toast.makeText(PatientProfileActivity.this, "Write to database is successful", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PatientProfileActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
        else
        {
            reference.child("Users").child(auth.getUid()).child("image").setValue(image);
        }

        Intent intent = new Intent(PatientProfileActivity.this, MainActivity.class);
        intent.putExtra("Username",username);
        startActivity(intent);
        finish();

    }

    public void getUserInfo()
    {
        reference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name = snapshot.child("username").getValue().toString();
                image = snapshot.child("image").getValue().toString();
                String age = snapshot.child("Age").getValue().toString();
                String degree = snapshot.child("Disability").getValue().toString();
                editTextUserNameProfile.setText(name);
                editTextAgeProfile.setText(age);
                editTextDegreeProfile.setText(degree);

                if(image.equals("null")) {
                    imageViewCircleProfile.setImageResource(R.drawable.accountcircle);
                }
                else
                {
                    Picasso.get().load(image).into(imageViewCircleProfile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            Picasso.get().load(imageUri).into(imageViewCircleProfile);
            imageControl = true;
        }

        else {
            imageControl = false;
        }
    }

}