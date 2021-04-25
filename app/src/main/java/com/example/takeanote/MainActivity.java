package com.example.takeanote;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeanote.auth.Register;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.model.NoteUIAdapter;
import com.example.takeanote.notes.AddNote;
import com.example.takeanote.notes.NoteDetails;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

//TODO: canviar SAMPLE DESCRIPTION: treure o posar algo (a concertar)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView listOfNotes;

    private MainActivityViewModel viewModel;
    private NoteUIAdapter adapter;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.init(this).observe(this, noteUIS -> {
            setUpAdapter(noteUIS);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.content_main_toolbar);
        setSupportActionBar(toolbar);

        listOfNotes = findViewById(R.id.listOfNotes);
        listOfNotes.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        setUpViewModel();

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);
        viewModel.checkUserNav( nav_view );

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        View headerView = nav_view.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.userDisplayName);
        TextView email = headerView.findViewById(R.id.userDisplayEmail);

        viewModel.menuConf(email, username);

        //Escollir si nota dibuix o nota text
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_main);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_text:
                        startActivity(new Intent(getApplicationContext(), AddNote.class));
                        break;
                    case R.id.action_paint:
                        startActivity(new Intent(getApplicationContext(), PaintActivity.class));
                        break;
                    case R.id.action_audio:
                        startActivity(new Intent(getApplicationContext(), AddAudio.class));
                        break;
                    /*case R.id.action_image:
                        startActivity(new Intent(getApplicationContext(), AddImage.class));
                        break;*/
                    default:
                        Toast.makeText(MainActivity.this, "Coming soon.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    private void setUpAdapter(List<NoteUI> noteUIS) {
        if (noteUIS == null) {
            noteUIS = new ArrayList<>();
        }
        adapter = new NoteUIAdapter(noteUIS, new NoteUIAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(NoteUI noteUI) {
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), NoteDetails.class);
                intent.putExtra("title", noteUI.getTitle());
                intent.putExtra("content", noteUI.getContent());
                //intent.putExtra("code",code);
                String docId = noteUI.getId();
                intent.putExtra("noteId", docId);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.getApplicationContext().startActivity(intent);
            }

            @Override
            public void onNoteMenuClick(NoteUI noteUI, View view) {
                PopupMenu menu = new PopupMenu(MainActivity.this.getApplicationContext(), view);
                menu.getMenu().add("Delete").setOnMenuItemClickListener(item -> {
                    viewModel.deleteNote(noteUI);
                    return false;
                });
                //TODO: potser afegir share dsp de noteDetails i cambiar a material

                menu.show();
            }
        });
        listOfNotes.setAdapter(adapter);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()) {
            case R.id.add_text_note:
                startActivity(new Intent(this, AddNote.class));
                break;

            case R.id.add_drawing_note:
                startActivity(new Intent(this, PaintActivity.class));
                break;

            case R.id.add_audio_note:
                startActivity(new Intent(this, AddAudio.class));
                break;

            /*case R.id.add_image_note:
                startActivity(new Intent(this, AddImage.class));
                break;*/

            case R.id.sync:
                viewModel.sync();
                break;

            case R.id.create_account:
                startActivity(new Intent(getApplicationContext(), Register.class));
                break;

            case R.id.logout:
                //Mirem si l'usuari logejat és anonim o no i fem signout
                viewModel.checkUser();
                break;

            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //TODO: fer per poder canviar idioma espanyol/català/àngles (MARC) i altres ajuestes a concertar
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