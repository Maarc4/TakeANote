package com.example.takeanote;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.notes.AddNoteViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ImageActivity extends AppCompatActivity {
    //EditText noteTitle, noteContent;
    MaterialToolbar toolbar;
    //private AddNoteViewModel viewModel;
    private ImageActivityViewModel viewModel;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button addImageButton;
    private Button saveButton;
    private ImageView imatge;
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        toolbar = findViewById(R.id.addImage_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(ImageActivityViewModel.class);

        //viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);

        addImageButton = findViewById(R.id.select_image);
        //saveButton = findViewById(R.id.save);
        imatge = findViewById(R.id.selected_image);


        //noteContent = findViewById(R.id.addNoteContent);
        //noteTitle = findViewById(R.id.addImageTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imatge.setVisibility(View.INVISIBLE);

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                viewModel.uploadFile(mImageUri, getFileExtension(mImageUri), progressDialog).observe( this, new Observer<Uri>() {
                    @Override
                    public void onChanged(Uri uri) {
                        onBackPressed();
                    }
                } );
                break;
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    }private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

}
