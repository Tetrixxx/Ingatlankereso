package com.example.ingatlan;

import android.os.Bundle;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private FirebaseUser user;
    private static final String LOG_TAG = ListActivity.class.getName();
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;  // Ez egy FirestoreRecyclerAdapter-et kiterjesztő adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);

        // Ablak-insetek beállítása a recyclerView-hoz
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Felhasználó ellenőrzése a Firebase Authentication-nel

        // RecyclerView beállítása
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        uploadSampleData();
        // Firestore lekérdezés létrehozása: pl. a "properties" gyűjteményből Budapest ingatlanjai, ár szerint növekvő sorrendben
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("properties")
                .orderBy("price", Query.Direction.ASCENDING);

        // FirestoreRecyclerOptions felépítése a modell osztályoddal (például Property)
        FirestoreRecyclerOptions<Property> options = new FirestoreRecyclerOptions.Builder<Property>()
                .setQuery(query, Property.class)
                .build();

        // Adapter inicializálása és a RecyclerView-hoz rendelése
        adapter = new PropertyAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();  // Elindítja a snapshot figyelést
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null){
            adapter.stopListening();   // Megállítja a snapshot figyelést, így erőforrásokat takarít meg
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

        // 1. Ellenőrizzük, van-e már dokumentum a "properties" kollekcióban
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
