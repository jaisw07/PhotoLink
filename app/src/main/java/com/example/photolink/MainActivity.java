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

import android.graphics.Rect;
import android.widget.ImageView;

import java.util.List;
import java.util.ArrayList;

import android.graphics.RectF;

public class MainActivity extends AppCompatActivity {

    // Image request code
    private static final int PICK_IMAGE = 100;

    private FaceOverlayView faceOverlayView;
    private ImageView selectedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout

        selectedImageView = findViewById(R.id.selectedImageView);
        faceOverlayView = findViewById(R.id.faceOverlayView);

        // Initialize the open gallery button
        Button openGalleryButton = findViewById(R.id.openGalleryButton);
        openGalleryButton.setOnClickListener(v -> openGallery()); // Set onClickListener
    }

    private void detectFaces(Bitmap bitmap) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .build();

        FaceDetector detector = FaceDetection.getClient(options);

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    // Get ImageView dimensions
                    int imageViewWidth = selectedImageView.getWidth();
                    int imageViewHeight = selectedImageView.getHeight();

                    // Get Bitmap dimensions
                    int bitmapWidth = bitmap.getWidth();
                    int bitmapHeight = bitmap.getHeight();

                    // Calculate scale factors
                    float scaleX = (float) imageViewWidth / bitmapWidth;
                    float scaleY = (float) imageViewHeight / bitmapHeight;

                    // Calculate offsets (for centering if aspect ratio is different)
                    float offsetX = (imageViewWidth - (bitmapWidth * scaleX)) / 2;
                    float offsetY = (imageViewHeight - (bitmapHeight * scaleY)) / 2;

                    // Convert face bounds
                    List<RectF> faceBounds = new ArrayList<>();
                    for (Face face : faces) {
                        Rect rect = face.getBoundingBox();
                        faceBounds.add(new RectF(rect.left, rect.top, rect.right, rect.bottom));
                    }

                    // Update the FaceOverlayView with correct scaling
                    faceOverlayView.setFaceBounds(faceBounds, scaleX, scaleY, offsetX, offsetY);
                    Toast.makeText(MainActivity.this, faces.size() + " Face(s) detected!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Face detection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    selectedImageView.setImageBitmap(bitmap); // Display selected image
                    detectFaces(bitmap); // Detect faces in selected image
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Invalid image selection.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
