package com.example.swumakeupshot;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView name,caution,allergy,good;
    ImageView image;
    public ViewHolder(View itemView, final Adapter.OnItemClickEventListener a_itemClickListener) {
        super(itemView);

        // Click event
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a_view) {
                final int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    a_itemClickListener.onItemClick(position);
                }
            }
        });

        name = (TextView)itemView.findViewById(R.id.makeup_name);
        caution = (TextView)itemView.findViewById(R.id.caution);
        allergy = (TextView)itemView.findViewById(R.id.allergy);
        good = (TextView)itemView.findViewById(R.id.good);
        image=itemView.findViewById(R.id.makeup_img);
    }
}
