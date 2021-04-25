package com.example.takeanote;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
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

import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;

//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

public class AddAudio extends AppCompatActivity {


    private ImageButton recorder,recorder2;
    private TextView text;
    private Chronometer time = null;
    private MediaRecorder mrecorder;
    private String fileName = null;
    private boolean isrecording = false;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        recorder = findViewById(R.id.record_btn);
        text = findViewById(R.id.record_filename);
        recorder2 = findViewById(R.id.record2_btn);
        View b = findViewById(R.id.record2_btn);
        b.setVisibility(View.GONE);

        toolbar = findViewById(R.id.audioToolbar);
        setSupportActionBar(toolbar);

        time = findViewById(R.id.record_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            /*case R.id.save:
                viewModel.saveNote( this, noteTitle, noteContent, progressBarSave ).observe( this, new Observer<Map<String, Object>>() {
                    @Override
                    public void onChanged(Map<String, Object> stringObjectMap) {
                        onBackPressed();
                    }
                } );
                break;*/

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


    /*public void showPopup(View anchorView) {

        View popupView = getLayoutInflater().inflate(R.layout.audio_card_layout, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 800, 600);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        // Initialize objects from layout
        TextInputLayout saveDescr = popupView.findViewById(R.id.save);
        Button saveButton = popupView.findViewById(R.id.save);
        saveButton.setOnClickListener((v) -> {
            String text = saveDescr.getEditText().getText().toString();

            popupWindow.dismiss();
        });
    }*/
}