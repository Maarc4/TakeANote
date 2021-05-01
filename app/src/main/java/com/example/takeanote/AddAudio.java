package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

public class AddAudio extends AppCompatActivity {


    private ImageButton recorder,recorder2;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private TextView text;
    private Chronometer time = null;
    private MediaRecorder mrecorder;
    private String fileName = null;
    private boolean isrecording = false;
    MaterialToolbar toolbar;
    FirebaseStorage storage;
    StorageReference storageReference;
    public static final String TAG = "DatabaseAdapter";
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userid;
    String filePath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        recorder = findViewById(R.id.record_btn);
        text = findViewById(R.id.record_filename);
        recorder2 = findViewById(R.id.record2_btn);
        View b = findViewById(R.id.record2_btn);
        b.setVisibility(View.GONE);
        DateFormat df = new SimpleDateFormat("yyMMddHHmmss", Locale.GERMANY);
        String date = df.format(Calendar.getInstance().getTime());
        this.storage = FirebaseStorage.getInstance();
        this.userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //this.path = getExternalCacheDir().getAbsolutePath()+ File.separator +date+".3gp";
        storageReference = storage.getReference();



        toolbar = findViewById(R.id.audioToolbar);
        setSupportActionBar(toolbar);

        time = findViewById(R.id.record_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (!isrecording) {
                        if (ActivityCompat.checkSelfPermission(AddAudio.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(AddAudio.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

                        } else {

                            startRecording();

                        }

                        b.setVisibility(View.VISIBLE);
                        text.setText(R.string.recording_started);
                        isrecording = true;

                }
            }
        });
        recorder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isrecording){
                    stopRecording();
                    b.setVisibility(View.INVISIBLE);
                    text.setText("Recording Finshed");
                    isrecording=false;

                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                uploadAudio();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void startRecording() {
        time.setBase(SystemClock.elapsedRealtime());
        Log.d("startRecording", "startRecording");
        time.start();
        mrecorder = new MediaRecorder();
        mrecorder.reset();
        mrecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mrecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mrecorder.setOutputFile(fileName);
        mrecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mrecorder.prepare();
            mrecorder.start();
        } catch (IOException e) {
            Log.d("startRecording", "prepare() failed");
        }


    }

    private void stopRecording() {
        time.stop();
        try {
            mrecorder.prepare();
            mrecorder.stop();

        } catch (IOException e) {
            Log.d("stopRecording", "prepare() failed");
        }
        mrecorder.release();
        mrecorder = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_top_bar, menu);
        return true;
    }
    public void uploadAudio() {
        String fileName = UUID.randomUUID().toString();
        /*String imgSaved = MediaStore.Images.Media.insertImage(
                getApplication().getContentResolver(), paintView.getDrawingCache(),
                UUID.randomUUID().toString() + ".3gp", "Recording Audio");*/
        //Create album folder if it doesn't exist
        File mImageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PODCASTS), "MyAlbumName");
        //Retrieve the path with the folder/filename concatenated
        File mImageFilePath = new File(new File(mImageDir, fileName).getAbsolutePath());

        //Create new content values
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.ImageColumns.DATA, String.valueOf(mImageFilePath));
        //Add whatever other content values you need
        ContentResolver contentResolver = AddAudio.this.getContentResolver();
        Object mUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        //Guillem
         filePath = mImageFilePath.getAbsolutePath();

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog( getApplication() );
            progressDialog.setTitle("Uploading...");
            //progressDialog.show();

            StorageReference ref = storageReference.child("audios/" + userid + "/" + fileName + ".3gp");
            Log.d("STATE","FILEPATHHHHHHHHHHHH: " + filePath);
            Log.d("STATE","REFFFFFFFFFFFFFFFFFFFFF: " + ref);
            ref.putFile( Uri.parse(filePath))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplication().getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplication().getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    /*public void saveDocumentWithFile (String userid, String path) {

        Uri file = Uri.fromFile(new File(path));
        StorageReference storageRef = storage.getReference();
        StorageReference audioRef = storageRef.child("audio/" + userid + "/" + UUID.randomUUID().toString() + ".3gp");
        ProgressDialog progressDialog = new ProgressDialog(this);

        audioRef.putFile( Uri.parse(fileName))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
       /* UploadTask uploadTask = audioRef.putFile(file);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return audioRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    saveDocument(userid, downloadUri.toString());
                } else {
                   
                }
            }
        });


        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });

    }
    public void saveDocument(String userid, String url) {

        // Create a new user with a first and last name
        Map<String, Object> note = new HashMap<>();

        note.put("userid", userid);
        note.put("url", url);

        Log.d(TAG, "saveDocument");
        // Add a new document with a generated ID
        db.collection("audioCards")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }*/
    /*public void showPopup(View anchorView) {

        View popupView = getLayoutInflater().inflate(R.layout.audio_card_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 800, 600);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Initialize objects from layout
        TextInputLayout saveDescr = popupView.findViewById(R.id.noteTitle);
        Button saveButton = popupView.findViewById(R.id.save);
        saveButton.setOnClickListener((v) -> {
            String text = saveDescr.getEditText().getText().toString();

            popupWindow.dismiss();
        });*/
    }


}