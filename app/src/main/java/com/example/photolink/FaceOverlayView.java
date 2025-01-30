package com.example.photolink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

public class FaceOverlayView extends View {
    private List<RectF> faceBounds;
    private Paint paint;
    private ImageView imageView; // Reference to the ImageView
    private float scaleX, scaleY, offsetX, offsetY; // Scaling factors for bounding boxes

    public FaceOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
    }

    // Link the ImageView for scaling
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public void setFaceBounds(List<RectF> faceBounds, float scaleX, float scaleY, float offsetX, float offsetY) {
        this.faceBounds = faceBounds;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        invalidate(); // Refresh the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceBounds != null) {
            for (RectF rect : faceBounds) {
                // Adjust coordinates to match ImageView scaling
                float left = rect.left * scaleX + offsetX;
                float top = rect.top * scaleY + offsetY;
                float right = rect.right * scaleX + offsetX;
                float bottom = rect.bottom * scaleY + offsetY;
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
