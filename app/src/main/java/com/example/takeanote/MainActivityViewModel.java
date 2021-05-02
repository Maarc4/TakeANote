package com.example.takeanote;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.model.PaintInfo;
import com.example.takeanote.utils.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    FirebaseFirestore db;
    FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private MutableLiveData<List<NoteListItem>> notesData;
    final long ONE_MEGABYTE = 1024 * 1024;
    private final String STORAGE_URL = "gs://takeanote-a0e9a.appspot.com/";


    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        notesData = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
    }

    /*case "paintNote":
        Log.d("MAVM", q.getString("url"));

        storageReference.child(q.getString("url"))
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Toast.makeText(getApplication().getApplicationContext(), "ONSUCCESS", Toast.LENGTH_SHORT).show();
                        PaintInfo pi = q.toObject(PaintInfo.class);
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        pi.setBmp(bmp);
                        pi.setTitle(q.getString("title"));
                        NoteListItem noteListItem = new NoteListItem(pi);
                        notes.add(noteListItem);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("MAVM ->ERROR", "PRINGAT " + e.getMessage());

            }
        });*/
                                        /*storageReference.child(q.getString("url")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Toast.makeText(getApplication().getApplicationContext(), "ONSUCCESS", Toast.LENGTH_SHORT).show();

                                                PaintInfo pi = q.toObject(PaintInfo.class);
                                                pi.setUri(uri);
                                                pi.setTitle(q.getString("title"));

                                               // PaintView pv = q.toObject(PaintView.class);
                                                NoteListItem noteListItem = new NoteListItem(pi);
                                                notes.add(noteListItem);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("MAVM ->ERROR","PRINGAT"+e.getMessage());
                                            }
                                        });*/
    public LiveData<List<NoteListItem>> init() {

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser finalUser = user;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl(STORAGE_URL);
        List<NoteListItem> notes = new ArrayList<>();
        boolean succes = false;

        //PAINT NOTES
        db.collection("notes").document(user.getUid()).collection("paintNotes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d("MAVM", "List Empty");
                            return;
                        } else {
                            Log.d("Entra", "Entra");
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                storageReference.child(q.getString("url"))
                                        .getDownloadUrl().
                                        addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                PaintInfo pi = q.toObject(PaintInfo.class);
                                                pi.setUri(uri);
                                                pi.setId(q.getId());
                                                //pi.setTitle(q.getString("title")); //No hi ha re guardat al title

                                                // PaintView pv = q.toObject(PaintView.class);
                                                NoteListItem noteListItem = new NoteListItem(pi);
                                                notes.add(noteListItem);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("MAVM ->ERROR", "PRINGAT" + e.getMessage());
                                    }
                                });
                            }

                        }

                        Log.d("MAVM", "notes sol paint " + notes.size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication().getApplicationContext(), "Error loading PAINT notes!", Toast.LENGTH_SHORT).show();
            }
        });
        //TEXT NOTES
        db.collection("notes").document(user.getUid()).collection("myNotes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d("MAVM", "List Empty");
                            return;
                        } else {
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                NoteUI note = q.toObject(NoteUI.class);
                                String id = q.getId();
                                note.setId(id);
                                note.setUser(finalUser.getUid());
                                NoteListItem noteListItem = new NoteListItem(note);
                                notes.add(noteListItem);
                            }
                            //notesData.postValue(notes);
                        }
                        notesData.postValue(notes);
                        Log.d("MAVM", "notes amb text? " + notes.size());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication().getApplicationContext(), "Error loading TEXT notes!", Toast.LENGTH_SHORT).show();
            }
        });


        return notesData;
    }

    public void checkUserNav(NavigationView nav) {
        if (!user.isAnonymous()) {
            nav.getMenu().clear();
            nav.inflateMenu(R.menu.nav_menu_loged_user);
        } else {
            nav.getMenu().clear();
            nav.inflateMenu(R.menu.nav_menu);
        }
    }

    /*public List<NoteUI> getData() {
        return notesData.getValue();
    }*/

    public void deleteNote(NoteListItem noteListItem, int viewType) {
        //Log.d("docId ->", docId);
        List<NoteListItem> oldData = notesData.getValue();
        List<NoteListItem> newData = new ArrayList<NoteListItem>();
        for (NoteListItem note : oldData) {
            //Borrar nota text
            if (note.getViewType() == viewType) {
                if (note.getViewType() == Constant.ITEM_TEXT_NOTE_VIEWTYPE) {
                    NoteUI noteUI = note.getTextNoteItem();
                    NoteUI noteToDelete = noteListItem.getTextNoteItem();
                    if (!noteUI.getId().equals(noteToDelete.getId())) {
                        newData.add(note);
                    }
                }
                //Borrar nota paint
                else if (note.getViewType() == Constant.ITEM_PAINT_NOTE_VIEWTYPE) {
                    PaintInfo paintNote = note.getPaintInfo();
                    PaintInfo paintToDelete = note.getPaintInfo();
                    if (!paintNote.getUri().toString().equals(paintToDelete.getUri().toString())) {
                        newData.add(note);
                    }
                }
                /*//Borrar nota Image
                else if(note.getViewType() == Constant.ITEM_IMAGE_NOTE_VIEWTYPE){

                }
                //Borrar nota Audio
                else {

                }*/
            } else {
                newData.add(note);
            }
        }

        switch (viewType) {
            case Constant.ITEM_TEXT_NOTE_VIEWTYPE:
                NoteUI textNote = noteListItem.getTextNoteItem();
                DocumentReference docRef = db.collection("notes").document(user.getUid()).collection("myNotes").document(textNote.getId());
                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplication().getApplicationContext(), "NoteUI deleted.", Toast.LENGTH_SHORT).show();
                        notesData.setValue(newData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplication().getApplicationContext(), "FAILED to delete the note.", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case Constant.ITEM_PAINT_NOTE_VIEWTYPE:
                PaintInfo pinfo = noteListItem.getPaintInfo();

                db.collection("notes").document(user.getUid()).collection("paintNotes").document(pinfo.getId())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                storageReference.child(documentSnapshot.getString("url")).delete();
                                notesData.setValue(newData);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplication().getApplicationContext(), "Error Deleting PAINT note!", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case Constant.ITEM_IMAGE_NOTE_VIEWTYPE:
                break;
            case Constant.ITEM_AUDIO_NOTE_VIEWTYPE:
                break;
            default:
                throw new

                        IllegalArgumentException();
        }

    }

    public boolean sync() {
        if (user.isAnonymous()) {
            return true;
        } else {
            Toast.makeText(getApplication().getApplicationContext(), "You are connected", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public boolean userAnonymous() {
        // if user is real or not
        if (user.isAnonymous()) {
            return true;
        } else {
            FirebaseAuth.getInstance().signOut();
            return false;
        }
    }


    public void menuConf(TextView email, TextView username) {
        if (user.isAnonymous()) {
            email.setVisibility(View.INVISIBLE);
            username.setText("Temporal account");
        } else {
            email.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }
    }


    public FirebaseUser getUser() {
        return user;
    }

}
