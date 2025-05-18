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
        holder.textTitle.setText(model.getTitle());
        holder.textAddress.setText(model.getAddress());
        holder.textPrice.setText(String.valueOf(model.getPrice()));
        holder.textType.setText(model.getType());

        holder.itemView.setOnClickListener(v -> {
            DocumentSnapshot document = getSnapshots().getSnapshot(position);
            Property property = document.toObject(Property.class);

            Intent intent = new Intent(v.getContext(), PropertyDetailActivity.class);
            intent.putExtra("PROPERTY", property);
            v.getContext().startActivity(intent);
        });
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

        public PropertyHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textViewTitle);
            textAddress = itemView.findViewById(R.id.textViewAddress);
            textPrice = itemView.findViewById(R.id.textViewPrice);
            textType = itemView.findViewById(R.id.textViewType);
        }
    }
}
