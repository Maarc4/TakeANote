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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddAudio extends AppCompatActivity {


    private TextView text;
    private EditText title;
    private Chronometer time = null;
    private MediaRecorder mrecorder;
    private final String fileName = null;
    private boolean isrecording = false;
    MaterialToolbar toolbar;
    FirebaseStorage storage;
    FirebaseFirestore db;
    StorageReference storageReference;
    String userid;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        ImageButton recorder = findViewById(R.id.record_btn);
        text = findViewById(R.id.record_filename);
        ImageButton recorder2 = findViewById(R.id.record2_btn);
        View b = findViewById(R.id.record2_btn);
        b.setVisibility(View.GONE);
        title = findViewById(R.id.AudioTitle);
        this.storage = FirebaseStorage.getInstance();
        this.userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        toolbar = findViewById(R.id.audioToolbar);
        setSupportActionBar(toolbar);
        mProgress = new ProgressDialog(this);


        time = findViewById(R.id.record_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ActivityCompat.checkSelfPermission(AddAudio.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(AddAudio.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        recorder.setOnClickListener(v -> {

            if (!isrecording) {
                startRecording();

                b.setVisibility(View.VISIBLE);
                text.setText(getApplication().getResources().getString(R.string.recording_started));
                isrecording = true;
            }
        });
        recorder2.setOnClickListener(v -> {
            if (isrecording) {
                stopRecording();
                b.setVisibility(View.INVISIBLE);
                text.setText(getApplication().getResources().getString(R.string.recording_finished));
                isrecording = false;

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
                Toast.makeText(this, getApplication().getResources().getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show();

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

    private void uploadAudio() {
        String nTitle = title.getText().toString();

        if (nTitle.isEmpty()) {
            text.setText(getApplication().getResources().getString(R.string.toast_empty_field));
        } else {
            mProgress.setMessage(getApplication().getResources().getString(R.string.uploading));
            mProgress.show();
            String saveUrl = getApplication().getResources().getString(R.string.audio_type) + userid + "/" + UUID.randomUUID().toString() + getApplication().getResources().getString(R.string.suffix_3gp);

            DocumentReference docref = db.collection("notes").document(userid).collection("AudioNotes").document();
            StorageReference filepath = storageReference.child(saveUrl);
            Uri uri = Uri.fromFile(new File(fileName));
            Map<String, Object> newNote = new HashMap<>();
            newNote.put("title", title.getText().toString());
            newNote.put("url", saveUrl);

            docref.set(newNote).addOnSuccessListener(aVoid -> Log.d("PAVM", "ONSUCCESS docref.setNote"))
                    .addOnFailureListener(e -> Log.d("PAVM", "FAILURE docref.setNote"));

            filepath.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                mProgress.dismiss();
                text.setText(getApplication().getResources().getString(R.string.toast_uploaded));
            });
        }
    }


}
