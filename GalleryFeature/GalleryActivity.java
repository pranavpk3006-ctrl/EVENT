package com.example.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GalleryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView recyclerViewGallery;
    private FloatingActionButton fabUploadImage;
    private ProgressBar progressBar;

    private GalleryAdapter galleryAdapter;
    private List<ImageModel> imageList;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    // TODO: Determine user role dynamically. Set to true if Admin, false if User.
    private boolean isAdmin = false; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery); // Ensure you update `R` imports for your package

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Gallery");
        storageReference = FirebaseStorage.getInstance().getReference("gallery_images");

        // Initialize UI Views
        recyclerViewGallery = findViewById(R.id.recyclerViewGallery);
        fabUploadImage = findViewById(R.id.fabUploadImage);
        progressBar = findViewById(R.id.progressBar);

        // Check if Admin
        checkUserRole();

        // Setup RecyclerView with 3 columns
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this, 3));
        imageList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(this, imageList);
        recyclerViewGallery.setAdapter(galleryAdapter);

        // Fetch Images from DB
        fetchImagesFromDatabase();

        // Handle Image Upload Action (Visible to Admins Only)
        fabUploadImage.setOnClickListener(v -> openFileChooser());
    }

    private void checkUserRole() {
        // Implement logic to check if current user is an Admin
        // This could be fetching user details from Firebase User Database
        
        // For demonstration, you might toggle this boolean:
        // isAdmin = true;

        if (isAdmin) {
            fabUploadImage.setVisibility(View.VISIBLE);
        } else {
            fabUploadImage.setVisibility(View.GONE);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            
            Uri imageUri = data.getData();
            uploadFileToStorage(imageUri);
        }
    }

    private void uploadFileToStorage(Uri imageUri) {
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE);

            String randomString = UUID.randomUUID().toString();
            StorageReference fileReference = storageReference.child(randomString + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // After successful upload, get download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            saveImageUrlToDatabase(downloadUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(GalleryActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveImageUrlToDatabase(String downloadUrl) {
        String uploadId = databaseReference.push().getKey();

        if (uploadId != null) {
            ImageModel imageUpload = new ImageModel(uploadId, downloadUrl);
            databaseReference.child(uploadId).setValue(imageUpload)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(GalleryActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GalleryActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void fetchImagesFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ImageModel model = postSnapshot.getValue(ImageModel.class);
                    imageList.add(model);
                }
                galleryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(GalleryActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
