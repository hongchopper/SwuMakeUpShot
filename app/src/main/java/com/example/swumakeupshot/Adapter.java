package com.example.swumakeupshot;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    String TAG = "RecyclerViewAdapter";

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<ListItem> dataModels;
    Context context;

    //생성자를 통하여 데이터 리스트 context를 받음
    public Adapter(Context context, ArrayList<ListItem> dataModels){
        this.dataModels = dataModels;
        this.context = context;
    }
    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    interface OnItemLongClickListener{
        void onItemLongClick(View v,int position);
    }
    private OnItemClickListener mListener=null;
    private OnItemLongClickListener mItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener) { mItemLongClickListener = listener;}
    private int mCheckedPosition = -1;

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return dataModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,caution,allergy,good;
        ImageView image,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Click event
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(mListener!=null){
                            mListener.onItemClick(view,position);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(mListener!=null){
                            mItemLongClickListener.onItemLongClick(v,position);
                        }
                    }
                    return true;
                }
            });

            this.name = (TextView)itemView.findViewById(R.id.makeup_name);
            this.caution = (TextView)itemView.findViewById(R.id.caution);
            this.allergy = (TextView)itemView.findViewById(R.id.allergy);
            this.good = (TextView)itemView.findViewById(R.id.good);
            this.image=itemView.findViewById(R.id.makeup_img);
            this.delete=itemView.findViewById(R.id.delete);

        }
}


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate (R.layout.main_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");
        ListItem item = dataModels.get(position);

        final int color;
        if (holder.getAdapterPosition() == mCheckedPosition) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_200);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent);

        }
        holder.itemView.setBackgroundColor(color);

        holder.name.setText(item.getCos_name());
        holder.caution.setText(item.getCaution_count());
        holder.allergy.setText(item.getAllergy_count());
        holder.good.setText(item.getGood_count());
        holder.image.setImageURI(Uri.parse(item.getUri()));
        holder.delete.setImageResource(R.drawable.ic_baseline_delete_24);
    }

}
