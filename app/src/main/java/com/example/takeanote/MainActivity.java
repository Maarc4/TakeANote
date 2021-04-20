package com.example.takeanote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.takeanote.auth.Register;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.model.NoteUIAdapter;
import com.example.takeanote.notes.AddNote;
import com.example.takeanote.notes.NoteDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView listOfNotes;
    //FirebaseFirestore db;
    //FirestoreRecyclerAdapter<NoteUI, MainActivity.NoteViewHolder> noteAdapter;
    //FirebaseUser user;
    //FirebaseAuth auth;

    private MainActivityViewModel viewModel;
    private NoteUIAdapter adapter;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.init(this).observe( this, new androidx.lifecycle.Observer<List<NoteUI>>() {
            @Override
            public void onChanged(List<NoteUI> noteUIS) {
                setUpAdapter( noteUIS );
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.content_main_toolbar);
        setSupportActionBar(toolbar);

        listOfNotes = findViewById(R.id.listOfNotes);
        listOfNotes.setLayoutManager( new LinearLayoutManager(  this, LinearLayoutManager.VERTICAL, false) );

        setUpViewModel();

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView email = headerView.findViewById(R.id.userDisplayEmail);

        /*
        if (user.isAnonymous()) {
            email.setVisibility(View.INVISIBLE);
            username.setText("Temporal account");
        } else {
            email.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }*/

        //Escollir si nota dibuix o nota text
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_main);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_text:
                        Toast.makeText(MainActivity.this, "Add TEXT NOTE clicked.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AddNote.class));
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

    private void setUpAdapter(List<NoteUI> noteUIS) {
        if (noteUIS == null) {
            noteUIS = new ArrayList<>();
        }
        adapter = new NoteUIAdapter( noteUIS, new NoteUIAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(NoteUI noteUI) {
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), NoteDetails.class);
                intent.putExtra("title", noteUI.getTitle());
                intent.putExtra("content", noteUI.getContent());
                //intent.putExtra("code",code);
                String docId = noteUI.getId();
                intent.putExtra("noteId", docId);
                intent.addFlags( intent.FLAG_ACTIVITY_NEW_TASK );
                MainActivity.this.getApplicationContext().startActivity(intent);
            }

            @Override
            public void onNoteMenuClick(NoteUI noteUI, View view) {
                PopupMenu menu = new PopupMenu(MainActivity.this.getApplicationContext(), view);
                menu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        viewModel.deleteNote( noteUI );
                        return false;
                    }
                });
                //TODO: potser afegir share dsp de noteDetails i cambiar a material

                menu.show();
            }
        } );
        listOfNotes.setAdapter( adapter );
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
                viewModel.sync();
                break;

            case R.id.logout:
                //Mirem si l'usuari logejat és anonim o no i fem signout
                viewModel.checkUser();
                break;

            default:
                Toast.makeText(this, "Comming soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
/*
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


    //TODO posible canvi a viewmodel
    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary Account. Loggin out will Delete All the notes.")
                .setPositiveButton("Sync NoteUI", new DialogInterface.OnClickListener() {
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
*/
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

}