package com.example.photolink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Image request code
    private static final int PICK_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout

        // Initialize the open gallery button after setting the layout
        Button openGalleryButton = findViewById(R.id.openGalleryButton);
        openGalleryButton.setOnClickListener(v -> openGallery()); // Set onClickListener

        // Optional: Replace this with actual image data from gallery/camera
        Bitmap bitmap = getSampleBitmap();
        detectFaces(bitmap);
    }

    private void detectFaces(Bitmap bitmap) {
        // Set up the face detector with the default options
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build();

        // Get the FaceDetector client
        FaceDetector detector = FaceDetection.getClient(options);

        // Convert Bitmap to InputImage
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        // Process the image for face detection
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    // Handle faces detected
                    if (faces.size() > 0) {
                        for (Face face : faces) {
                            // You can now access face data like bounding boxes, landmarks, etc.
                            Toast.makeText(MainActivity.this, "Face detected!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No faces detected.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Face detection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Replace this method with actual logic to get image from gallery or camera
    private Bitmap getSampleBitmap() {
        return Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888); // Dummy bitmap for testing
    }

    // Open gallery to select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                detectFaces(bitmap); // Call face detection with the selected image
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}