package com.example.takeanote;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
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
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class AddAudio extends AppCompatActivity {


    private String address;

    MutableLiveData<ArrayList<AddAudio>> mAudioCards;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private Context parentContext;
    private RecyclerView mRecyclerView;
    private ImageButton recorder,recorderlist;
    private TextView text ,Title;
    private Chronometer time = null;
    private MediaRecorder mrecorder;
    private String fileName = null;
    private static final String LOG_TAG = "Record_log";
    //private StorageReference mStorage;
    private ProgressDialog mProgress;
    private boolean isrecording =false;
    MaterialToolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_audio);
        //mStorage = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        recorder =  (ImageButton) findViewById(R.id.record_btn);
        text = (TextView) findViewById(R.id.record_filename);

        toolbar = findViewById(R.id.audioToolbar);
        setSupportActionBar(toolbar);

        time = findViewById(R.id.record_timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddAudio.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AddAudio.this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);

                } else {


                    if(!isrecording){

                        startRecording();
                        text.setText("Recording Started");
                        isrecording = true;
                    }else{
                        stopRecording();
                        text.setText("Recording Finshed");
                    }

                }

            }
        });

    }

    public AddAudio getAudioCard(int idx) {
        return mAudioCards.getValue().get(idx);
    }

    public String getAddress() {
        return this.address;
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




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted) finish();
    }


    public void showPopup(View anchorView) {

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
    }
}