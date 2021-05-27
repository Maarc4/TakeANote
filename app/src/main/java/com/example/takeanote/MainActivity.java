package com.example.takeanote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
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
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.takeanote.adapter.NotesAdapter;
import com.example.takeanote.auth.Login;
import com.example.takeanote.auth.Register;
import com.example.takeanote.model.AudioInfo;
import com.example.takeanote.model.ImageInfo;
import com.example.takeanote.model.MapsInfo;
import com.example.takeanote.model.AudioInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.model.PaintInfo;
import com.example.takeanote.notes.AddNote;
import com.example.takeanote.notes.NoteDetails;
import com.example.takeanote.utils.Constant;
import com.example.takeanote.utils.OnNoteTypeClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView nav_view;
    private RecyclerView listOfNotes;
    private MainActivityViewModel viewModel;
    private NotesAdapter adapter;
    private androidx.appcompat.widget.SearchView search;
    private List<NoteListItem> original;
    private int order;
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
        order = -1;
        listOfNotes = findViewById( R.id.listOfNotes );
        listOfNotes.setLayoutManager( new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false ) );
        mediaplayer = new MediaPlayer();
        setUpViewModel();
        record = findViewById(R.id.audioPlayButton);
        drawerLayout = findViewById( R.id.drawer );
        nav_view = findViewById( R.id.nav_view );
        nav_view.setNavigationItemSelectedListener( this );
        viewModel.checkUserNav( nav_view );

        toggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.open, R.string.close );
        drawerLayout.addDrawerListener( toggle );
        toggle.setDrawerIndicatorEnabled( true );
        toggle.syncState();

        search = findViewById( R.id.search );
        confSearch();

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
                        Intent paintIntent1 = new Intent( MainActivity.this.getApplicationContext(), PaintActivity.class );
                        //paintIntent1.putExtra("uriPath"," ");
                        startActivity( paintIntent1);
                        break;
                    case R.id.action_audio:
                        startActivity( new Intent( getApplicationContext(), AddAudio.class ) );
                        break;
                    case R.id.action_image:
                        startActivity( new Intent( getApplicationContext(), ImageActivity.class ) );
                        break;
                    case R.id.action_map:
                        startActivity( new Intent( getApplicationContext(), MapsActivity.class));
                        finish();
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
                   /* case (Constant.ITEM_PAINT_NOTE_VIEWTYPE):
                        PaintInfo paintInfo = noteItem.getPaintInfo();
                        Intent paintIntent = new Intent(MainActivity.this.getApplicationContext(), PaintActivity.class);
                        paintIntent.putExtra("title",paintInfo.getTitle());
                        Log.d("MAact",paintInfo.getBmp().toString());
                        paintIntent.putExtra("bitmap",paintInfo.getBmp());
                        startActivity(paintIntent);
                        //Toast.makeText( MainActivity.this, "Edit PAINT note -> Coming in the next update.", Toast.LENGTH_SHORT ).show();
                        break;
                   */

                    case (Constant.ITEM_IMAGE_NOTE_VIEWTYPE):
                        ImageInfo imageInfo = noteItem.getImageInfo();
                        Intent imageIntent = new Intent();
                        imageIntent.setAction(Intent.ACTION_VIEW);
                        Log.d("myTag", "This ssage");
                        imageIntent.setDataAndType(imageInfo.getUri(), "image/*");
                        startActivity(imageIntent);
                        break;



                    case (Constant.ITEM_PAINT_NOTE_VIEWTYPE):

                       /* PaintInfo paintInfo = noteItem.getPaintInfo();
                        Intent paintIntent = new Intent();
                        paintIntent.setAction(Intent.ACTION_VIEW);
                        Log.d("myTag", "This ssage");
                        paintIntent.setDataAndType(paintInfo.getUri(), "image/*");
                        startActivity(paintIntent);
*/

                        PaintInfo paintInfo = noteItem.getPaintInfo();
                        Intent paintIntent = new Intent( MainActivity.this.getApplicationContext(), PaintActivity.class );
                        paintIntent.putExtra( "title", paintInfo.getTitle() );
                        Uri a = paintInfo.getUri();
                        paintIntent.putExtra("uri",paintInfo.getUri());
                        paintIntent.putExtra("path",paintInfo.getUriPath());
                        startActivity( paintIntent );


                        /*
                        //Intent intent = new Intent();
                        //intent.setAction(Intent.ACTION_VIEW);
                        //intent.setDataAndType(PaintActivity.imageUri, "image/*");
                        //startActivity(intent);
                        PaintInfo paintInfo = noteItem.getPaintInfo();
                        //Intent paintIntent = new Intent();
                        //paintIntent.setAction();

                        Log.d("myTag", "This is my message");
                        //paintIntent.setDataAndType(paintInfo.getUri(), "image/*");
                        //startActivity(paintIntent);



                        Intent paintIntent1 = new Intent( MainActivity.this.getApplicationContext(), PaintActivity.class );
                        paintIntent1.putExtra( "title", paintInfo.getTitle() );
                        Uri a = paintInfo.getUri();
                        paintIntent1.putExtra("uriPath",paintInfo.getUri());
                        Bitmap bmp = paintInfo.getBmp();
                        paintIntent1.putExtra("bitmap",paintInfo.getBmp());



                        Log.d( "URISS", "URI FORA: " + a );
                        paintIntent1.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( paintIntent1 );

                        */
                        break;


                    /*case (Constant.ITEM_AUDIO_NOTE_VIEWTYPE):
                        break;
                    case (Constant.ITEM_IMAGE_NOTE_VIEWTYPE):
                       break;*/
                    case (Constant.ITEM_MAP_NOTE_VIEWTYPE):
                        MapsInfo maps = noteItem.getMaps();
                        Intent mapsIntetnt = new Intent( MainActivity.this.getApplicationContext(), MapsActivity.class );
                        mapsIntetnt.putExtra( "title", maps.getTitle() );
                        mapsIntetnt.putExtra( "latlng", maps.getLatLng() );
                        mapsIntetnt.putExtra( "id", maps.getId() );
                        mapsIntetnt.putExtra( "address", maps.getAddress() );
                        //mapsIntetnt.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity( mapsIntetnt );
                        finish();
                        break;
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
                } else {
                    Toast.makeText( MainActivity.this, "Reproduciendo", Toast.LENGTH_SHORT ).show();
                }
                /*
                if(!aud.isRepro()){
                    mediaplayer.start();
                }else{
                    mediaplayer.pause();
                }*/

            }
            /*record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaplayer.start();
                }
            });*/

        } );
        Handler handler = new Handler( Looper.getMainLooper() );
        List<NoteListItem> finalAllTypeNotes = allTypeNotes;
        handler.postDelayed( new Runnable() {

            @Override
            public void run() {
                listOfNotes.setAdapter( adapter );
                original = new ArrayList<>();
                original.addAll( finalAllTypeNotes );
                adapter.orderRecyclerView( order );
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
                Intent paintIntent1 = new Intent( MainActivity.this.getApplicationContext(), PaintActivity.class );
                paintIntent1.putExtra("uriPath"," ");
                startActivity( paintIntent1);

                break;

            case R.id.add_audio_note:
                startActivity( new Intent( this, AddAudio.class ) );
                break;

            case R.id.add_image_note:
                startActivity( new Intent( this, ImageActivity.class ) );
                break;
            case R.id.add_map_note:
                startActivity( new Intent( getApplicationContext(), MapsActivity.class));
                finish();
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
            Toast.makeText( MainActivity.this, "Settings Menu is Clicked.", Toast.LENGTH_SHORT ).show();
        } else if (item.getItemId() == R.id.action_order) {
            PopupMenu popupMenu = new PopupMenu( this,  this.findViewById( R.id.content_main_toolbar ), Gravity.RIGHT );
            popupMenu.getMenuInflater().inflate( R.menu.order_menu, popupMenu.getMenu() );
            popupMenu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch(item.getItemId()) {
                        case R.id.alfabet:
                            order = 0;
                            adapter.orderRecyclerView( order );
                            break;
                        case R.id.alfabet_reverse:
                            order = 1;
                            adapter.orderRecyclerView( order );
                            break;
                        case R.id.order_type:
                            order = 2;
                            adapter.orderRecyclerView( order );
                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });
            popupMenu.show();

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

    private void confSearch(){
        search.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.setOriginal( original );
                adapter.filter( query );
                adapter.orderRecyclerView( order );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setOriginal( original );
                adapter.filter( newText );
                adapter.orderRecyclerView( order );
                return false;
            }
        } );
    }

    @Override
    protected void onResume() {
        super.onResume();
        search.setEnabled( true );
        search.clearFocus();
        search.clearAnimation();
        if (adapter != null) {
            search.setQuery( "", false );
            adapter.orderRecyclerView( order );
            for(MapView mapView : adapter.getMapViewList()){
                if (mapView != null){
                    mapView.onResume();
                }
            }
        }

    }

}