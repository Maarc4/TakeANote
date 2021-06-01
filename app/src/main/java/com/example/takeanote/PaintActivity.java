package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class PaintActivity extends AppCompatActivity {
    Intent data;
    private PaintView paintView;
    private int width;
    private int color;
    private EditText title;
    private PaintActivityViewModel paintActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        data = getIntent();

        paintView = findViewById(R.id.paintView);
        title = findViewById(R.id.PaintTitle);
        MaterialToolbar toolbar = findViewById(R.id.paintToolbar);
        ImageView imageView = findViewById(R.id.imageView4);


        imageView.setVisibility(View.INVISIBLE);
        if (data.getExtras() != null) {
            String uriPath = data.getExtras().get("uriPath").toString();
            Log.d("URISS", "URI DINS: " + uriPath);
            title.setText(data.getStringExtra("title"));
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paintActivityViewModel = new ViewModelProvider(this).get(PaintActivityViewModel.class);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        width = PaintView.BRUSH_SIZE;
        color = PaintView.DEFAULT_COLOR;
        ActivityCompat.requestPermissions(PaintActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions( PaintActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1 );


    }


    public String getStringTitle() {
        return title.getText().toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.paint_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                break;
            case R.id.emboss:
                paintView.emboss();
                break;
            case R.id.blur:
                paintView.blur();
                break;
            case R.id.clear:
                paintView.clear();
                break;
            case R.id.eraser:
                paintView.setBrushColor(Color.WHITE);
                paintView.setBrushWidth(30);
                break;
            case R.id.cRed:
                color = Color.RED;
                paintView.setBrushColor(color);
                paintView.setBrushWidth(width);
                break;
            case R.id.cGreen:
                color = Color.GREEN;
                paintView.setBrushColor(color);
                paintView.setBrushWidth(width);
                break;
            case R.id.cBlue:
                color = Color.BLUE;
                paintView.setBrushColor(color);
                paintView.setBrushWidth(width);
                break;
            case R.id.cBlack:
                color = Color.BLACK;
                paintView.setBrushColor(color);
                paintView.setBrushWidth(width);
                break;
            case R.id.br5:
                width = 5;
                paintView.setBrushWidth(width);
                paintView.setBrushColor(color);
                break;
            case R.id.br10:
                width = 10;
                paintView.setBrushWidth(width);
                paintView.setBrushColor(color);
                break;
            case R.id.br20:
                width = 20;
                paintView.setBrushWidth(width);
                paintView.setBrushColor(color);
                break;
            case R.id.save:
                if (!title.getText().toString().isEmpty()) {
                    paintActivityViewModel.saveView(paintView).observe(this, s -> {
                        final ProgressDialog progressDialog = new ProgressDialog(PaintActivity.this);
                        paintActivityViewModel.uploadImage(progressDialog, title.getText().toString());
                    });
                } else {
                    Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_empty_field), Toast.LENGTH_SHORT).show();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
