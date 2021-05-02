package com.example.takeanote;

public class AddAudioViewModel { //extends AndroidViewModel

    /*FirebaseStorage storage;
    StorageReference storageReference;
    String userid;
    private boolean isrecording = false;

    public AddAudioViewModel(@NonNull Application application) {
        super( application );
        this.storage = FirebaseStorage.getInstance();
        this.userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = storage.getReference();
    }

    private void startRecording() {
        time.setBase( SystemClock.elapsedRealtime() );
        Log.d( "startRecording", "startRecording" );
        time.start();
        mrecorder = new MediaRecorder();
        mrecorder.reset();
        mrecorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        mrecorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        mrecorder.setOutputFile( fileName );
        mrecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AMR_NB );

        try {
            mrecorder.prepare();
            mrecorder.start();
        } catch (IOException e) {
            Log.d( "startRecording", "prepare() failed" );
        }
    }

    private void stopRecording() {
        time.stop();
        mrecorder.stop();
        mrecorder.release();
        mrecorder = null;

    }

    public void uploadAudio() {
        StorageReference filepath = storageReference.child( "audio/" + userid + "/" + UUID.randomUUID().toString() + ".3gp" );
        Uri uri = Uri.fromFile( new File( fileName ) );
        Log.d( "STATE", "FILEPATHHHHHHHHHHHH: " + filepath );
        filepath.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        } );
    }

    public boolean isRecording() {
        return isrecording;
    }
     */

}
