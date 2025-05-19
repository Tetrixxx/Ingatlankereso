package com.example.ingatlan;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 1000;
    private static final String LOG_TAG = ListActivity.class.getName();

    private FirebaseUser user;
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;  // Ez egy FirestoreRecyclerAdapter-et kiterjesztő adapter

    private SearchView searchView;
    private Spinner spinnerFilter;
    private FirestoreRecyclerOptions<Property> currentOptions;

    // Helymeghatározáshoz szükséges változók
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    //Helymeghatározás engedély ellenőrzése és lekérése
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // UI elemek inicializálása
        searchView = findViewById(R.id.searchView);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setupSearchAndFilter();

        // Helymeghatározás inicializálása
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermissionAndFetch();

        // Ide jönnek az adatok frissítése és feltöltése Firebase-be
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("properties").get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Property property = doc.toObject(Property.class);
                doc.getReference().update("searchTerms", property.generateSearchTerms());
            }
        });

        uploadSampleData();

        // Firestore lekérdezés: pl. a properties kollekció, ár szerint növekvő sorrend
        Query query = db.collection("properties")
                .orderBy("price", Query.Direction.ASCENDING);

        // FirestoreRecyclerOptions felépítése a modellel (Property)
        FirestoreRecyclerOptions<Property> options = new FirestoreRecyclerOptions.Builder<Property>()
                .setQuery(query, Property.class)
                .build();

        // Adapter inicializálása és a RecyclerView-hoz rendelése
        adapter = new PropertyAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    // Ellenőrzi, hogy van-e location engedély, ha nem, akkor kéri
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        } else {
            fetchUserLocation();
        }
    }

    // Lekéri a felhasználó utolsó ismert helyét
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Mindig ellenőrizd újra a jogosultságot!
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = location;
                        Log.d(LOG_TAG, "User location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
                        // Itt később használhatod az aktuális helyet, pl. lekérdezés szűrésére
                    } else {
                        Log.d(LOG_TAG, "Nem sikerült lekérni a helyet.");
                    }
                });
    }

    // A location engedély eredményének kezelése
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            } else {
                Toast.makeText(this, "Helymeghatározás engedélye szükséges a közeli ingatlanok megjelenítéséhez.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupSearchAndFilter() {
        // Spinner érintés kezelése
        spinnerFilter.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
                Log.d("SPINNER", "Spinner meg lett koppintva.");
            }
            return false;
        });

        // Keresőmező eseménykezelője
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                updateQuery(newText);
                return true;
            }
        });

        // Szűrő beállításainak kezelése
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateQuery(searchView.getQuery().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateQuery(String searchText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("properties");

        // Ha a keresőmező nem üres, akkor szűrés a searchTerms mező alapján
        if (!searchText.isEmpty()) {
            query = query.whereArrayContains("searchTerms", searchText.toLowerCase());
        }

        // Rendezés a Spinner kiválasztása alapján
        switch (spinnerFilter.getSelectedItemPosition()) {
            case 0:  // Ár növekvő
                query = query.orderBy("price", Query.Direction.ASCENDING);
                break;
            case 1:  // Ár csökkenő
                query = query.orderBy("price", Query.Direction.DESCENDING);
                break;
            case 2:  // Város szerint
                query = query.orderBy("city");
                break;
        }

        // Új FirestoreRecyclerOptions beállítása az adapterhez
        currentOptions = new FirestoreRecyclerOptions.Builder<Property>()
                .setQuery(query, Property.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }
        adapter = new PropertyAdapter(currentOptions);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
            adapter = new PropertyAdapter(currentOptions);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    private List<Property> properties = Arrays.asList(
            new Property("Belvárosi lakás", "Váci út 5.", "lakás", 65000000, "Budapest"),
            new Property("Kertvárosi ház", "Alkotmány utca 12.", "ház", 120000000, "Budapest"),
            new Property("Panel lakás", "Kossuth tér 3.", "lakás", 35000000, "Debrecen"),
            new Property("Tetőtéri lakás", "Petőfi Sándor utca 7.", "lakás", 28000000, "Szeged"),
            new Property("Családi ház", "Dózsa György út 25.", "ház", 90000000, "Budapest"),
            new Property("Újépítésű lakás", "Béke tér 14.", "lakás", 55000000, "Pécs"),
            new Property("Nyugalom otthona", "Erdőszél utca 8.", "ház", 75000000, "Győr"),
            new Property("Belvárosi garzon", "Andrássy út 32.", "lakás", 42000000, "Budapest"),
            new Property("Kertkapcsolatos ház", "Rózsa utca 19.", "ház", 110000000, "Miskolc"),
            new Property("Téglaépítésű lakás", "Széchenyi tér 5.", "lakás", 48000000, "Debrecen"),
            new Property("Luxusvilla", "Hegyalja út 42.", "ház", 150000000, "Budapest"),
            new Property("Panoráma lakás", "Hősök tere 9.", "lakás", 68000000, "Budapest"),
            new Property("Kertes családi ház", "Virág utca 3.", "ház", 85000000, "Szeged"),
            new Property("Két szobás lakás", "Ady Endre út 17.", "lakás", 37000000, "Nyíregyháza"),
            new Property("Tanya felújítva", "Mező utca 22.", "ház", 60000000, "Kecskemét"),
            new Property("Penthouse", "Várkert rakpart 1.", "lakás", 130000000, "Budapest"),
            new Property("Kis ház a város szélén", "Erdősor út 11.", "ház", 70000000, "Székesfehérvár"),
            new Property("Társasházi lakás", "Bartók Béla út 27.", "lakás", 45000000, "Veszprém"),
            new Property("Felújított lakópark", "Szent István körút 8.", "lakás", 58000000, "Budapest"),
            new Property("Nyaraló a Balatonnál", "Füredi út 55.", "ház", 95000000, "Siófok")
    );

    private void uploadSampleData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Ellenőrizzük, van-e már dokumentum a "properties" kollekcióban
        db.collection("properties").limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        for (Property property : properties) {
                            db.collection("properties")
                                    .add(property)
                                    .addOnSuccessListener(documentReference ->
                                            Log.d("UPLOAD", "Feltöltve: " + documentReference.getId()))
                                    .addOnFailureListener(e ->
                                            Log.w("UPLOAD", "Hiba: " + e.getMessage()));
                        }
                        Log.d("UPLOAD", "Adatok feltöltve!");
                    } else {
                        Log.d("UPLOAD", "A kollekció már létezik, feltöltés kihagyva.");
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("UPLOAD", "Hiba a kollekció ellenőrzésekor: " + e.getMessage()));
    }
}
