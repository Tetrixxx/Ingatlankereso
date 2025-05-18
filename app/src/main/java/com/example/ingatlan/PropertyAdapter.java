package com.example.ingatlan;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PropertyAdapter extends FirestoreRecyclerAdapter<Property, PropertyAdapter.PropertyViewHolder> {

    public PropertyAdapter(@NonNull FirestoreRecyclerOptions<Property> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PropertyViewHolder holder, int position, @NonNull Property model) {
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
        holder.textAddress.setContentDescription("Cím: " + model.getAddress());

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PropertyDetailActivity.class);
            // Feltételezzük, hogy a Property osztályod implementálja a Parcelable interfészt!
            intent.putExtra("PROPERTY", model);
            context.startActivity(intent);
        });


    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup group, int i) {
        View view = LayoutInflater.from(group.getContext())
                .inflate(R.layout.list_item, group, false);
        return new PropertyViewHolder(view);
    }


    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textAddress;
        TextView textPrice;
        TextView textType;

        TextView textCity;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textViewTitle);
            textAddress = itemView.findViewById(R.id.textViewAddress);
            textPrice = itemView.findViewById(R.id.textViewPrice);
            textType = itemView.findViewById(R.id.textViewType);
            textCity = itemView.findViewById(R.id.textViewCity);
        }
    }
}
