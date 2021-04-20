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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    //Guillem
    FirebaseStorage storage;
    StorageReference storageReference;
    String filePath;
    String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Guillem
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);
        paintView = findViewById(R.id.paintView);
        toolbar = findViewById(R.id.paintToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                saveView();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

// TODO Treure comentaris loco


    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + user +"/" + UUID.randomUUID().toString() + ".jpg");
            ref.putFile(Uri.parse(filePath))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PaintActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PaintActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    //TODO: mirar de ferho sense depracated method
    public void saveView() {

        //AIXI VA PERO DEPRACATED
        paintView.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), paintView.getDrawingCache(),
                UUID.randomUUID().toString() + ".jpg", "drawing");

        //Guillem
        filePath = imgSaved;

        if (imgSaved != null) {
            Toast.makeText(this, "IMAGE SAVED: " + imgSaved, Toast.LENGTH_SHORT).show();
            uploadImage();
        } else {
            Toast.makeText(this, "ERROR NOT SAVED", Toast.LENGTH_SHORT).show();
        }
        paintView.destroyDrawingCache();
    }
}
