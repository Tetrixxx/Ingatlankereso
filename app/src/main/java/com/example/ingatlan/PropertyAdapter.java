package com.example.ingatlan;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class PropertyAdapter extends FirestoreRecyclerAdapter<Property, PropertyAdapter.PropertyHolder> {

    public PropertyAdapter(@NonNull FirestoreRecyclerOptions<Property> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PropertyHolder holder, int position, @NonNull Property model) {
        // Alapértelmezett értékek beállítása
        holder.textTitle.setText(model.getTitle());
        holder.textAddress.setText(model.getAddress());
        holder.textPrice.setText(String.valueOf(model.getPrice()));
        holder.textType.setText(model.getType());
        holder.textCity.setText(model.getCity());

        // Accessibility tartalom leírások
        holder.textTitle.setContentDescription("Ingatlan neve: " + model.getTitle());
        holder.textPrice.setContentDescription("Ár: " + model.getPrice() + " Ft");
        holder.textCity.setContentDescription("Város: " + model.getCity());
        holder.textType.setContentDescription("Típus: " + model.getType());
    }

    @NonNull
    @Override
    public PropertyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // a list_item.xml layout inflatálása
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new PropertyHolder(view);


    }

    // A ViewHolder osztály, amely a list_item.xml-ben definiált nézeteket fogja összekötni
    class PropertyHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textAddress;
        TextView textPrice;
        TextView textType;

        TextView textCity;

        public PropertyHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textViewTitle);
            textAddress = itemView.findViewById(R.id.textViewAddress);
            textPrice = itemView.findViewById(R.id.textViewPrice);
            textType = itemView.findViewById(R.id.textViewType);
            textCity = itemView.findViewById(R.id.textViewCity);
        }
    }

}
