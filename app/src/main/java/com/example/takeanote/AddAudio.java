package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.notes.AddNoteViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddAudio extends AppCompatActivity {


    private ImageButton recorder, recorder2;
    private TextView text;
    private EditText title;
    private Chronometer time = null;
    private MediaRecorder mrecorder;
    private String fileName = null;
    private boolean isrecording = false;
    MaterialToolbar toolbar;
    private AddAudioViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += ("/audio_recorded.3gp");
        setContentView(R.layout.activity_add_audio);

        viewModel = new ViewModelProvider(this).get(AddAudioViewModel.class);


        recorder = findViewById(R.id.record_btn);
        text = findViewById(R.id.record_filename);
        recorder2 = findViewById(R.id.record2_btn);
        View b = findViewById(R.id.record2_btn);
        b.setVisibility(View.GONE);
        title = findViewById(R.id.AudioTitle);

        toolbar = findViewById(R.id.audioToolbar);
        setSupportActionBar(toolbar);

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
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_empty_field), Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.uploadAudio(fileName, title.getText().toString());
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
}
