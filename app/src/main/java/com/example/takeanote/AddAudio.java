package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

public class AddAudio extends AppCompatActivity {


    private ImageButton recorder, recorder2;
    private TextView text;
    private Chronometer time = null;
    private MediaRecorder mrecorder;
    private String fileName = null;
    private boolean isrecording = false;
    MaterialToolbar toolbar;
    FirebaseStorage storage;
    FirebaseFirestore db;
    StorageReference storageReference;
    String userid;
    String filePath;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += ("/audiorecordtest.3gp");
        setContentView(R.layout.activity_add_audio);
        recorder = findViewById(R.id.record_btn);
        text = findViewById(R.id.record_filename);
        recorder2 = findViewById(R.id.record2_btn);
        View b = findViewById(R.id.record2_btn);
        b.setVisibility(View.GONE);

        this.storage = FirebaseStorage.getInstance();
        this.userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = storage.getReference();

        toolbar = findViewById(R.id.audioToolbar);
        setSupportActionBar(toolbar);
        mProgress = new ProgressDialog(this);

        time = findViewById(R.id.record_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ActivityCompat.checkSelfPermission(AddAudio.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(AddAudio.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isrecording) {
                    startRecording();

                    b.setVisibility(View.VISIBLE);
                    text.setText(R.string.recording_started);
                    isrecording = true;

                }
            }
        });
        recorder2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isrecording) {
                    stopRecording();
                    b.setVisibility(View.INVISIBLE);
                    text.setText("Recording Finshed");
                    isrecording = false;

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
        mrecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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
        mrecorder.stop();
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

        StorageReference filepath = storageReference.child("audio/" + userid + "/" + UUID.randomUUID().toString() + ".3gp");
        Uri uri = Uri.fromFile(new File(fileName));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddAudio.this, "Audio UPLOADED", Toast.LENGTH_SHORT).show();
                text.setText("Audio UPLOADED");
            }
        });

    }

}
