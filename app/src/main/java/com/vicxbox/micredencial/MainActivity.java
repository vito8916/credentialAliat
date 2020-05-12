package com.vicxbox.micredencial;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ValueAnimator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView videoview;

    private static Context context;

    Button btnCredencial, btnTakePhoto;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*make activity full screen*/
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        context = getApplicationContext();

        videoview = findViewById(R.id.videoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+ R.raw.video1);
        videoview.setVideoURI(uri);
        videoview.start();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        btnCredencial = findViewById(R.id.btn_create_credential);
        btnTakePhoto = findViewById(R.id.btn_take_picture);

        btnCredencial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent credencial = new Intent(MainActivity.this, VerifyPhotoActivity.class);
                startActivity(credencial);
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CapturePicture = new Intent(MainActivity.this, VerifyPhotoActivity.class);
                startActivity(CapturePicture);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoview.stopPlayback();
    }

    public static Context getAppContext()
    {
        return MainActivity.context;
    }
}
