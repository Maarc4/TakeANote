package com.example.takeanote;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Intent data;
    private GoogleMap mMap;
    private Address address;
    private List<Address> addressList;
    private LatLng latLng;
    private EditText t;
    private androidx.appcompat.widget.SearchView searchView;
    private MapsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_note);

        MaterialToolbar toolbar = findViewById(R.id.addMap_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();
        t = findViewById(R.id.addMapTitle);

        viewModel = new ViewModelProvider(this).get(MapsViewModel.class);

        searchView = findViewById(R.id.idSearchView);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                addressList = null;
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                if (location != null || location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    try {
                        addressList = geocoder.getFromLocationName(location, 5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    PopupMenu popupMenu = new PopupMenu(MapsActivity.this, MapsActivity.this.findViewById(R.id.idSearchView), Gravity.START);
                    popupMenu.getMenuInflater().inflate(R.menu.maps_menu, popupMenu.getMenu());
                    if (addressList != null && !addressList.isEmpty()) {
                        viewModel.confItems(popupMenu, addressList);
                        popupMenu.setOnMenuItemClickListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.Opt1:
                                    address = addressList.get(0);
                                    break;
                                case R.id.Opt2:
                                    address = addressList.get(1);
                                    break;
                                case R.id.Opt3:
                                    address = addressList.get(2);
                                    break;
                                case R.id.Opt4:
                                    address = addressList.get(3);
                                    break;
                                case R.id.Opt5:
                                    address = addressList.get(4);
                                    break;
                                default:
                                    break;
                            }
                            latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(latLng).title(t.getText().toString()));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            return true;
                        });
                        popupMenu.show();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (data.getExtras() != null) {
            Bundle bun = data.getExtras();
            t.setText(bun.get("title").toString());
            latLng = (LatLng) bun.get("latlng");
            mMap.addMarker(new MarkerOptions().position(latLng).title(bun.get("title").toString()));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                address = addressList.get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            address = null;
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng( -34, 151 );
        mMap.addMarker( new MarkerOptions().position( sydney ).title( "Marker in Sydney" ) );
        mMap.moveCamera( CameraUpdateFactory.newLatLng( sydney ) );*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                String title = t.getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(getApplication().getApplicationContext(), R.string.toast_empty_field, Toast.LENGTH_SHORT).show();
                } else if (address == null) {
                    Toast.makeText( getApplication().getApplicationContext(), R.string.toast_empty_field, Toast.LENGTH_SHORT ).show();
                } else if (data.getExtras() != null && title.equals( data.getExtras().get( "title" ) ) &&
                        address.getAddressLine(address.getMaxAddressLineIndex()).equals( data.getExtras().getString( "address" ) )) {
                    Toast.makeText( getApplication().getApplicationContext(), R.string.toast_nothing_changed, Toast.LENGTH_SHORT ).show();
                } else {
                    if (data.getExtras() != null) {
                        viewModel.updateMaps(t.getText().toString(), latLng, address.getAddressLine(address.getMaxAddressLineIndex()), data.getExtras().get("id").toString())
                                .observe(this, stringObjectMap -> {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                });
                    } else {
                        viewModel.saveMaps(title, latLng, address.getAddressLine(address.getMaxAddressLineIndex())).observe(this, stringObjectMap -> {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        });
                    }
                }
                break;

            case android.R.id.home:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;

            default:
                Toast.makeText(this, getApplication().getResources().getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_top_bar, menu);
        return true;
    }

}