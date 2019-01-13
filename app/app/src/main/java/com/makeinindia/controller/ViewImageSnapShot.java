package com.makeinindia.controller;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ViewImageSnapShot extends Activity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.SCREEN_BRIGHTNESS_CHANGED, WindowManager.LayoutParams.SCREEN_BRIGHTNESS_CHANGED);    // Removes notification bar

        setContentView(R.layout.view_image_snap);
        imageView = (ImageView) findViewById(R.id.imageView);
        Intent data = getIntent();
        String imageFile = " ";

        Bundle bundle = data.getExtras();

        if (bundle != null) {

            imageFile = bundle.getString("pos");


            Bitmap bitmap = BitmapFactory.decodeFile(imageFile);
            imageView.setImageBitmap(bitmap);


            // Start timer and launch main activity

        }
    }
}