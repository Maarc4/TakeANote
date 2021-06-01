package com.example.takeanote;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaintActivityViewModel extends AndroidViewModel {

    private FirebaseStorage storage;
    private final FirebaseFirestore db;
    private StorageReference storageReference;
    private final String userUID;
    public static String filePath;
    private final String STORAGE_URL = getApplication().getResources().getString(R.string.storage_url);
    public static Uri ImageUri;


    private final MutableLiveData<String> observableFilePath;

    public PaintActivityViewModel(@NonNull Application application) {
        super(application);
        observableFilePath = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void uploadImage(ProgressDialog progressDialog, String paintTitle) {

        if (filePath != null) {
            progressDialog.setTitle(getApplication().getResources().getString(R.string.uploading));
            progressDialog.show();
            String saveUrl = getApplication().getResources().getString(R.string.image_type) + userUID + "/" + UUID.randomUUID().toString() + getApplication().getResources().getString(R.string.suffix_jpg);

            // Guardem la nota a firebase per
            DocumentReference docref = db.collection("notes").document(userUID).collection("paintNotes").document();
            Map<String, Object> newNote = new HashMap<>();
            newNote.put("title", paintTitle);
            newNote.put("url", saveUrl);
            newNote.put("filepath", filePath);

            docref.set(newNote).addOnSuccessListener(aVoid -> Log.d("PAVM", "ONSUCCESS docref.setNote"))
                    .addOnFailureListener(e -> Log.d("PAVM", "FAILURE docref.setNote"));

            ImageUri = Uri.parse(filePath);

            StorageReference ref = storageReference.child(saveUrl);


            ref.putFile(Uri.parse(filePath))
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_uploaded), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage(getApplication().getResources().getString(R.string.uploading) + (int) progress + "%");
                    });

        }
    }

    public LiveData<String> saveView(PaintView paintView) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl(STORAGE_URL);


        //AIXI VA PERO DEPRACATED
        paintView.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(
                getApplication().getContentResolver(), paintView.getDrawingCache(),
                UUID.randomUUID().toString() + ".jpg", "drawing");


        if (imgSaved != null) {
            filePath = imgSaved;
            observableFilePath.setValue(imgSaved);
        } else {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_not_saved), Toast.LENGTH_SHORT).show();
        }
        paintView.destroyDrawingCache();
        return observableFilePath;
    }

}
