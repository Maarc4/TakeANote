package com.example.takeanote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeanote.adapter.NotesAdapter;
import com.example.takeanote.auth.Login;
import com.example.takeanote.auth.Register;
import com.example.takeanote.model.AudioInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.notes.AddNote;
import com.example.takeanote.notes.NoteDetails;
import com.example.takeanote.utils.Constant;
import com.example.takeanote.utils.OnNoteTypeClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;
    private RecyclerView listOfNotes;
    private MainActivityViewModel viewModel;
    private NotesAdapter adapter;
    private MediaPlayer mediaplayer;
    private ImageButton record;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider( this ).get( MainActivityViewModel.class );
        viewModel.init().observe( this, allTypeNotes -> {
            setUpAdapter( allTypeNotes );
            //adapter.notifyDataSetChanged();
        } );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        MaterialToolbar toolbar = findViewById( R.id.content_main_toolbar );
        setSupportActionBar( toolbar );

        listOfNotes = findViewById( R.id.listOfNotes );
        listOfNotes.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false ) );
        mediaplayer = new MediaPlayer();
        setUpViewModel();
        record = findViewById(R.id.audioPlayButton);
        drawerLayout = findViewById( R.id.drawer );
        nav_view = findViewById( R.id.nav_view );
        nav_view.setNavigationItemSelectedListener( this );
        viewModel.checkUserNav( nav_view );

        //mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //Log.d("Hector","caca" + fileName);


        toggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.open, R.string.close );
        drawerLayout.addDrawerListener( toggle );
        toggle.setDrawerIndicatorEnabled( true );
        toggle.syncState();

        View headerView = nav_view.getHeaderView( 0 );
        TextView username = headerView.findViewById( R.id.userDisplayName );
        TextView email = headerView.findViewById( R.id.userDisplayEmail );
        viewModel.menuConf( email, username );

        //Escollir si nota dibuix o nota text
        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById( R.id.fab_main );
        fabSpeedDial.setMenuListener( new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_text:
                        startActivity( new Intent( getApplicationContext(), AddNote.class ) );
                        break;
                    case R.id.action_paint:
                        startActivity( new Intent( getApplicationContext(), PaintActivity.class ) );
                        break;
                    case R.id.action_audio:
                        startActivity( new Intent( getApplicationContext(), AddAudio.class ) );
                        break;
                    case R.id.action_image:
                        startActivity( new Intent( getApplicationContext(), ImageActivity.class ) );
                        break;
                    default:
                        Toast.makeText( MainActivity.this, "Coming soon.", Toast.LENGTH_SHORT ).show();
                }
                return false;
            }
        } );

    }

    private void setUpAdapter(List<NoteListItem> allTypeNotes) {

        if (allTypeNotes == null) {
            allTypeNotes = new ArrayList<>();
        }
        adapter = new NotesAdapter( allTypeNotes, new OnNoteTypeClickListener() {
            @Override
            public void onNoteClick(NoteListItem noteItem) {
                int viewType = noteItem.getViewType();

                switch (viewType) {
                    case (Constant.ITEM_TEXT_NOTE_VIEWTYPE):
                        NoteUI textNote = noteItem.getTextNoteItem();
                        Intent textIntent = new Intent( MainActivity.this.getApplicationContext(), NoteDetails.class );
                        textIntent.putExtra( "title", textNote.getTitle() );
                        textIntent.putExtra( "content", textNote.getContent() );
                        textIntent.putExtra( "noteId", textNote.getId() );
                        textIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( textIntent );
                        break;
                    case (Constant.ITEM_AUDIO_NOTE_VIEWTYPE):
                        AudioInfo textAudio = noteItem.getAudioNoteItem();
                        Intent textIn = new Intent( MainActivity.this.getApplicationContext(), NoteDetails.class );
                        textIn.putExtra( "title", textAudio.getTitle() );
                        textIn.putExtra( "noteId", textAudio.getId() );
                        textIn.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( textIn );
                        break;
                    case (Constant.ITEM_PAINT_NOTE_VIEWTYPE):
                        /*PaintInfo paintInfo = noteItem.getPaintInfo();
                        Intent paintIntent = new Intent(MainActivity.this.getApplicationContext(), PaintActivity.class);
                        paintIntent.putExtra("title",paintInfo.getTitle());
                        Log.d("MAact",paintInfo.getBmp().toString());
                        paintIntent.putExtra("bitmap",paintInfo.getBmp());
                        startActivity(paintIntent);*/
                        Toast.makeText( MainActivity.this, "Edit PAINT note -> Coming in the next update.", Toast.LENGTH_SHORT ).show();
                        break;
                    /*case (Constant.ITEM_AUDIO_NOTE_VIEWTYPE):
                        break;
                    case (Constant.ITEM_IMAGE_NOTE_VIEWTYPE):
                       break;*/
                    default:
                        Toast.makeText( MainActivity.this, "Coming Soon. OnNoteClick", Toast.LENGTH_SHORT ).show();
                }
            }

            @Override
            public void onNoteMenuClick(NoteListItem noteItem, View view) {
                PopupMenu menu = new PopupMenu( MainActivity.this.getApplicationContext(), view );
                menu.getMenu().add( "Delete" ).setOnMenuItemClickListener( item -> {
                    viewModel.deleteNote( noteItem, noteItem.getViewType() );
                    viewModel.init().observe( MainActivity.this, allTypeNotes -> {
                        setUpAdapter( allTypeNotes );
                        //adapter.notifyDataSetChanged();
                    } );
                    return false;
                } );

                //TODO: potser afegir share dsp de noteDetails i cambiar a material

                menu.show();
            }

            @Override
            public void onPlayClick(NoteListItem audio, View view) {

                AudioInfo aud = audio.getAudioNoteItem();
                if(!mediaplayer.isPlaying()){

                    String fileName = aud.getUri().toString();
                    Log.d("minga",fileName);
                    try {
                        mediaplayer.setDataSource(fileName);
                        mediaplayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(!aud.isRepro()){
                    mediaplayer.start();
                }else{
                    mediaplayer.pause();
                }

            }
            /*record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaplayer.start();
                }
            });*/

        } );
        Handler handler = new Handler( Looper.getMainLooper() );
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                listOfNotes.setAdapter( adapter );
            }
        }, 1000 );

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        drawerLayout.closeDrawer( GravityCompat.START );

        switch (item.getItemId()) {
            case R.id.add_text_note:
                startActivity( new Intent( this, AddNote.class ) );
                break;

            case R.id.add_drawing_note:
                startActivity( new Intent( this, PaintActivity.class ) );
                break;

            case R.id.add_audio_note:
                startActivity( new Intent( this, AddAudio.class ) );
                break;

            case R.id.add_image_note:
                startActivity( new Intent( this, ImageActivity.class ) );
                break;

            case R.id.sync:
                if (viewModel.sync()) {
                    showWarning();
                }
                break;

            case R.id.create_account:
                startActivity( new Intent( getApplicationContext(), Register.class ) );
                break;

            case R.id.logout:
                //Mirem si l'usuari logejat és anonim o no i fem signout
                if (!viewModel.userAnonymous()) {
                    startActivity( new Intent( getApplicationContext(), LoadScreen.class ) );
                    finish();
                } else {
                    displayAlert();
                }
                break;

            default:
                Toast.makeText( this, "Coming soon.", Toast.LENGTH_SHORT ).show();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.settings_menu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //TODO: fer per poder canviar idioma manualment i altres ajuestes a concertar
        if (item.getItemId() == R.id.action_settings) {
            Toast.makeText( this, "Settings Menu is Clicked.", Toast.LENGTH_SHORT ).show();
        }
        return super.onOptionsItemSelected( item );
    }

    //TODO possible traspas a viewmodel?
    public void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder( this )
                .setTitle( "Are you sure?" )
                .setMessage( "You are logged in with Temporary Account. Loggin out will Delete All the notes." )
                .setPositiveButton( "Sync NoteUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( getApplicationContext(), Register.class ) );
                        finish();
                    }
                } ).setNegativeButton( "Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        viewModel.getUser().delete().addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                startActivity( new Intent( getApplicationContext(), LoadScreen.class ) );
                                finish();
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        } );
                    }
                } );
        warning.show();
    }

    //TODO possible traspas a viewmodel
    public void showWarning() {
        final AlertDialog.Builder warning = new AlertDialog.Builder( this )
                .setMessage( "Linking Existing Account Will delete all the temp notes. Create New Account To Save them." )
                .setPositiveButton( "Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( getApplicationContext(), Register.class ) );
                    }
                } ).setNegativeButton( "Its Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent( getApplicationContext(), Login.class ) );
                    }
                } );

        warning.show();
    }


}