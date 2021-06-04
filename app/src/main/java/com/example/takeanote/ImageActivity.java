package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class ImageActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    MaterialToolbar toolbar;
    private ImageActivityViewModel viewModel;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private ImageView imatge;
    private EditText title;
    String currentPhotoPath;

    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        toolbar = findViewById(R.id.addImage_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(ImageActivityViewModel.class);


        Button takePhotoButton = findViewById(R.id.take_photo);
        Button addImageButton = findViewById(R.id.select_image);
        imatge = findViewById(R.id.selected_image);
        title = findViewById(R.id.addImageTitle);
        mImageUri = null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imatge.setVisibility(View.INVISIBLE);

        if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, TAKE_PHOTO_REQUEST);
        }

        addImageButton.setOnClickListener(view -> openFileChooser());
        takePhotoButton.setOnClickListener(view -> take_photo());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                final ProgressDialog progressDialog = new ProgressDialog(this);

                if (!title.getText().toString().equals("")) {
                    if (mImageUri != null) {
                        viewModel.uploadFile(mImageUri, getFileExtension(mImageUri), progressDialog, title.getText().toString()).observe(this, uri -> onBackPressed());
                    } else {
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_introduce_img), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_introduce_title), Toast.LENGTH_SHORT).show();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Toast.makeText(this, getApplication().getResources().getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat(getApplication().getResources().getString(R.string.date_format)).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                getApplication().getResources().getString(R.string.suffix_jpg),   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                imatge.setImageURI(null);
                imatge.setImageURI(mImageUri);
                imatge.invalidate();
                imatge.setVisibility(View.VISIBLE);
            }, 1000);

        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            imatge.setImageURI(mImageUri);
            imatge.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_top_bar, menu);
        return true;

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType(getApplication().getResources().getString(R.string.image_type));
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void take_photo() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.d("Error", "ERROR PHOTO");
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            mImageUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()), BuildConfig.APPLICATION_ID + ".provider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        }


    }
}
