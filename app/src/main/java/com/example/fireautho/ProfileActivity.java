package com.example.fireautho;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fireautho.model.Add_Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =20 ;
    CircleImageView circleImageView;
    Button edit,choose;
    Intent i;
    public static final int REQ_CODE = 2001;
    Bitmap bitmap;

    EditText name;
    EditText aboutme;
    EditText profexp;
    EditText mno;
    EditText aadress;
    EditText shopname;
    Button button;
    FirebaseAuth mAuth;

    private DatabaseReference mDatabase;
    private Uri filePath;

    // instance for firebase storage and StorageReference
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_expanded);

        mAuth = FirebaseAuth.getInstance();
        //All data register
        circleImageView = findViewById(R.id.showimage);
        edit = findViewById(R.id.edit_image);

        name = findViewById(R.id.name);
        aboutme = findViewById(R.id.aboutme);
        profexp = findViewById(R.id.exp);
        mno = findViewById(R.id.mobileno);
        aadress = findViewById(R.id.address);
        shopname=findViewById(R.id.shopname);
        button = findViewById(R.id.button);
        choose =findViewById(R.id.choose_image);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Profile");
        mDatabase.keepSynced(true);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //capture image
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              //  startActivityForResult(i, REQ_CODE);
                SelectImage();
            }
        });

        // on pressing btnUpload uploadImage() is called
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 uploadImage();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //     String mImage=imageView.getText().toString().trim();

                addProfile();


            }
        });
    }



    private void addProfile() {

        String mName = name.getText().toString().trim();
        String mAbout = aboutme.getText().toString().trim();
        String mProfExperience = profexp.getText().toString().trim();
        String mMobno = mno.getText().toString().trim();
        String mAddress = aadress.getText().toString().trim();
        String mShopename=shopname.getText().toString().trim();
        String id = mDatabase.push().getKey();


        if (!TextUtils.isEmpty(mName)) {
            Add_Profile data = new Add_Profile(mName, mAbout, mProfExperience, mMobno, mAddress,mShopename, id);
            mDatabase.child(id).setValue(data);
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this, "Plese enter valid Servise", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            bitmap = (Bitmap) bundle.get("data");

            circleImageView.setImageBitmap(bitmap);
        }

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                circleImageView.setImageBitmap(bitmap);
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //select Image
    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method


    private void uploadImage() {

        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ProfileActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ProfileActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }

    }
}