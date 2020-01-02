package com.example.introduce;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView name, intro, job, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        checkPermission();

        name = findViewById(R.id.name_txt);
        intro = findViewById(R.id.intro_txt);
        job = findViewById(R.id.job_txt);
        email = findViewById(R.id.email_txt);
        phone = findViewById(R.id.phone_txt);

        LinearLayout shareBtn = findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText = "Name: "+name.getText().toString()
                    +"\nIntro: "+intro.getText().toString()
                    +"\nJob: "+job.getText().toString()
                    +"\nEmail: "+email.getText().toString()
                    +"\nPhone: "+phone.getText().toString();
                share(shareText);
            }
        });
    }


    private void checkPermission(){
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                    }
                })
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    private void share(final String text){
        final LinearLayout box = findViewById(R.id.box);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("공유 방법 선택") //Dialog 타이틀 설정
                .setMessage("프로필을 공유할 방법을 선택하세요")
                .setPositiveButton("이미지로 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/"
                                + (new Date()).getTime() + ".png";
                        OutputStream out;
                        File file = new File(path);
                        try {
                            out = new FileOutputStream(file);
                            getBitmapFromView(box).compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Uri bmpUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.restaurants.fileprovider", file);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                        shareIntent.setType("image/png");
                        startActivity(Intent.createChooser(shareIntent, "이미지로 공유하기"));
                    }
                })
                .setNegativeButton("텍스트로 전송", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text)+text);
                        shareIntent.setType("text/plain");

                        startActivity(Intent.createChooser(shareIntent, "텍스트로 공유하기"));
                    }
                });
        builder.create();
        builder.show();
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
}
