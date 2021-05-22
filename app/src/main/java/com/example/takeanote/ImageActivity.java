package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.ByteArrayOutputStream;


public class ImageActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //EditText noteTitle, noteContent;
    MaterialToolbar toolbar;
    //private AddNoteViewModel viewModel;
    private ImageActivityViewModel viewModel;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int TAKE_PHOTO_REQUEST = 2;
    private Button addImageButton;
    private Button takePhotoButton;
    private Button saveButton;
    private ImageView imatge;
    private EditText title;

    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_image );
        toolbar = findViewById( R.id.addImage_toolbar );
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        viewModel = new ViewModelProvider( this ).get( ImageActivityViewModel.class );

        //viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);

        takePhotoButton = findViewById(R.id.take_photo);
        addImageButton = findViewById( R.id.select_image );
        //saveButton = findViewById(R.id.save);
        imatge = findViewById( R.id.selected_image );
        title = findViewById( R.id.addImageTitle );


        mImageUri = null;


        //noteContent = findViewById(R.id.addNoteContent);
        //noteTitle = findViewById(R.id.addImageTitle);

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        imatge.setVisibility( View.INVISIBLE );

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //DisplayMetrics metrics = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        addImageButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        } );

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                take_photo();
            }
        });
    }


    public void take_photo(){
        if(ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{
                Manifest.permission.CAMERA
            },TAKE_PHOTO_REQUEST);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,TAKE_PHOTO_REQUEST);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.save:
                final ProgressDialog progressDialog = new ProgressDialog( this );
                //viewModel.uploadImage(progressDialog,  title.getText().toString());
                //mImageUri = viewModel.ImageUri;

                if(!title.getText().toString().equals("")) {
                    if(mImageUri != null) {
                        viewModel.uploadFile(mImageUri, getFileExtension(mImageUri), progressDialog, title.getText().toString()).observe(this, new Observer<Uri>() {
                            @Override
                            public void onChanged(Uri uri) {
                                onBackPressed();
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplication().getApplicationContext(), "Introduce an image  " , Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplication().getApplicationContext(), "Introduce a title  " , Toast.LENGTH_SHORT).show();
                }

                break;
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Toast.makeText( this, "Coming soon.", Toast.LENGTH_SHORT ).show();

        }
        return super.onOptionsItemSelected( item );
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );


        if (requestCode == TAKE_PHOTO_REQUEST && resultCode == RESULT_OK){
            Handler handler = new Handler( Looper.getMainLooper() );
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mImageUri = data.getData();
                    imatge.setImageURI(mImageUri);
                    //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    //imatge.setImageBitmap(bitmap);
                    imatge.setVisibility( View.VISIBLE );
                }
            },5000);



        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            imatge.setImageURI( mImageUri );
            imatge.setVisibility( View.VISIBLE );
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.add_note_top_bar, menu );
        return true;

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType( cR.getType( uri ) );
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( intent, PICK_IMAGE_REQUEST );
    }

}
