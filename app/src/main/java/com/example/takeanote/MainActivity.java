package com.example.takeanote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.takeanote.auth.Register;
import com.example.takeanote.model.Adapter;
import com.example.takeanote.model.Note;
import com.example.takeanote.notes.AddNote;
import com.example.takeanote.notes.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView listOfNotes;
    FirebaseFirestore db;
    FirestoreRecyclerAdapter<Note, NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth auth;
    String docId;
//TODO: Crear card display per la nota de dibuix (potser poder afegir titol i eso)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.content_main_toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //de moment ordenat per titol a
        Query query = db.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title", Query.Direction.ASCENDING);
        // query notes > uuid > mynotes

        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i, @NonNull Note note) {
                noteViewHolder.noteTitle.setText(note.getTitle());
                noteViewHolder.noteContent.setText(note.getContent());
                //int code = getRandomColor();
                //noteViewHolder.mCardView.setCardBackgroundColor(noteViewHolder.view.getResources().getColor(code,null));
                docId = noteAdapter.getSnapshots().getSnapshot(i).getId();

                noteViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Al clicar una nota, es mou a una activity nova (details)
                        Intent intent = new Intent(v.getContext(), NoteDetails.class);
                        intent.putExtra("title", note.getTitle());
                        intent.putExtra("content", note.getContent());
                        //intent.putExtra("code",code);
                        intent.putExtra("noteId", docId);
                        v.getContext().startActivity(intent);
                    }
                });

                ImageView menuIcon = noteViewHolder.view.findViewById(R.id.menuIcon);
                menuIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu menu = new PopupMenu(v.getContext(), v);
                        menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Log.d("docId ->", docId);
                                DocumentReference docRef = db.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
                                docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "Note deleted.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "FAILED to delete the note.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        //TODO: potser afegir share dsp de noteDetails i cambiar a material

                        menu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_layout, parent, false);
                return new NoteViewHolder(view);
            }
        };

        listOfNotes = findViewById(R.id.listOfNotes);

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        // 2 per fila
        //listOfNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //LLISTA 1 per fila
        listOfNotes.setLayoutManager(new LinearLayoutManager(this));
        listOfNotes.setAdapter(noteAdapter);

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView email = headerView.findViewById(R.id.userDisplayEmail);

        if (user.isAnonymous()) {
            email.setVisibility(View.INVISIBLE);
            username.setText("Temporal account");
        } else {
            email.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }


        //Escollir si nota dibuix o nota text
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_main);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_text:
                        Toast.makeText(MainActivity.this, "Add TEXT NOTE clicked.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AddNote.class);
                        intent.putExtra("docId", docId);
                        startActivity(intent);
                        break;
                    case R.id.action_paint:
                        Toast.makeText(MainActivity.this, "Add DRAW NOTE clicked.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), PaintActivity.class));
                        break;
                    case R.id.action_audio:
                        //TODO: Hector, desde aqui vas a la teva clase audio
                        Toast.makeText(MainActivity.this, "Add AUDIO NOTE clicked.", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(), NOM_CLASE_AUDIO_HECTOR.class));
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()) {

            case R.id.add_note:
                Intent intent = new Intent(this, AddNote.class);
                startActivity(intent);
                break;

            case R.id.sync:
                if (user.isAnonymous()) {
                    startActivity(new Intent(this, Register.class));
                } else {
                    Toast.makeText(this, "You are connected", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.logout:
                //Mirem si l'usuari logejat és anonim o no i fem signout
                checkUser();
                break;

            default:
                Toast.makeText(this, "Comming soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        // if user is real or not
        if (user.isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoadScreen.class));
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary Account. Loggin out will Delete All the notes.")
                .setPositiveButton("Sync Note", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity(new Intent(getApplicationContext(), LoadScreen.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText(this, "Settings Menu is Clicked.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        //Per cambiar els colors random
        //MaterialCardView mCardView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
            //mCardView = itemView.findViewById(R.id.cardViewContent);
        }
    }

/*
    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random rColor = new Random();
        int n = rColor.nextInt(colorCode.size());
        return colorCode.get(n);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }
    }
}