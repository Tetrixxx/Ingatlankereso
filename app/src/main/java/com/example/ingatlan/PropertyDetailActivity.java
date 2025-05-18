package com.example.ingatlan;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PropertyDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        // Visszagomb beállítása
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Property objektum fogadása
        Property property = getIntent().getParcelableExtra("PROPERTY");

        if (property != null) {
            // View-ok inicializálása
            TextView titleText = findViewById(R.id.title_text);
            TextView priceText = findViewById(R.id.price_text);
            TextView addressText = findViewById(R.id.address_text);
            TextView typeText = findViewById(R.id.type_text);
            TextView cityText = findViewById(R.id.city_text);

            // Adatok beállítása
            titleText.setText(property.getTitle());
            priceText.setText(String.format("%,d Ft", property.getPrice())); // Formázott ár
            addressText.setText(property.getAddress());
            typeText.setText(property.getType());
            cityText.setText(property.getCity());

            // ActionBar cím beállítása
            setTitle(property.getTitle());
        }
    }

}