package com.example.takeanote;

import android.content.Intent;
import android.os.Bundle;

import com.example.takeanote.model.Adapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView nav_view;
    RecyclerView listOfNotes;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MaterialToolbar toolbar = findViewById(R.id.content_main_toolbar);
        setSupportActionBar(toolbar);

        listOfNotes = findViewById(R.id.listOfNotes);

        drawerLayout = findViewById(R.id.drawer);
        nav_view = findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        // SAMPLE DATA
        List<String> titles = new ArrayList<>();
        List<String> content = new ArrayList<>();

        titles.add("First Note");
        content.add("First Note Content sample.");

        titles.add("Second Note");
        content.add("Second Note Content sample.");

        titles.add("Third Note");
        content.add("Third Note Content sample.");
        // FI SAMPLE DATA
        adapter = new Adapter(titles, content);
        // 2 per fila
        //listOfNotes.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //LLISTA 1 per fila
        listOfNotes.setLayoutManager(new LinearLayoutManager(this));
        listOfNotes.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addNoteFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AddNote.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_note:
                Intent intent = new Intent(this,AddNote.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this,"Comming soon.",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Toast.makeText(this,"Settings Menu is Clicked.",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}