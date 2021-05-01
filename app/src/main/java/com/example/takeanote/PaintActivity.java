package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class PaintActivity extends AppCompatActivity {

    private PaintView paintView;
    private int width;
    private int color;
    MaterialToolbar toolbar;
    private PaintActivityViewModel paintActivityViewModel;

    //Guillem

    //TODO moure permisos a main

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Guillem
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        paintView = findViewById(R.id.paintView);
        toolbar = findViewById(R.id.paintToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paintActivityViewModel = new ViewModelProvider( this ).get( PaintActivityViewModel.class );

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        width = PaintView.BRUSH_SIZE;
        color = PaintView.DEFAULT_COLOR;
        ActivityCompat.requestPermissions(PaintActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                paintActivityViewModel.saveView(paintView).observe( this, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        final ProgressDialog progressDialog = new ProgressDialog( PaintActivity.this );
                        paintActivityViewModel.uploadImage(progressDialog);
                        //TODO potser no va quan fem lo de recuperar del firebase
                        //onBackPressed();

                    }
                } );
                break;
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
