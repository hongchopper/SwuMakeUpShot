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

public class Adapter extends RecyclerView.Adapter {
    /*
      어댑터의 동작원리 및 순서
      1.(getItemCount) 데이터 개수를 세어 어댑터가 만들어야 할 총 아이템 개수를 얻는다.
      2.(getItemViewType)[생략가능] 현재 itemview의 viewtype을 판단한다
      3.(onCreateViewHolder)viewtype에 맞는 뷰 홀더를 생성하여 onBindViewHolder에 전달한다.
      4.(onBindViewHolder)뷰홀더와 position을 받아 postion에 맞는 데이터를 뷰홀더의 뷰들에 바인딩한다.
      */
    String TAG = "RecyclerViewAdapter";

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<ListItem> dataModels=new ArrayList<>();
    Context context;

    //생성자를 통하여 데이터 리스트 context를 받음
    public Adapter(Context context, ArrayList<ListItem> dataModels){
        this.dataModels = dataModels;
        this.context = context;
    }
    public interface OnItemClickEventListener {
        void onItemClick(int a_position);
    }

    private List<ListItem> mItemList;

    private OnItemClickEventListener mItemClickListener = new OnItemClickEventListener() {
        @Override
        public void onItemClick(int a_position) {
            notifyItemChanged(mCheckedPosition, null);
            mCheckedPosition = a_position;
            notifyItemChanged(a_position, null);
        }
    };

    private int mCheckedPosition = -1;

    public Adapter(List<ListItem> a_list) {
        mItemList = a_list;
    }

    @Override
    public int getItemCount() {
        //데이터 리스트의 크기를 전달해주어야 함
        return dataModels.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG,"onCreateViewHolder");

        //자신이 만든 itemview를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_list_item,parent,false);
        //MyViewHolder viewHolder = new MyViewHolder(view);


        //생선된 뷰홀더를 리턴하여 onBindViewHolder에 전달한다.
        return new ViewHolder(view,mItemClickListener);
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG,"onBindViewHolder");
        ListItem item = dataModels.get(position);
        Log.e("선택한 화장품",dataModels.get(position).getCos_name());
        final int color;
        if (holder.getAdapterPosition() == mCheckedPosition) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.purple_200);
        } else {
            color = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent);

        }
        holder.itemView.setBackgroundColor(color);
        ViewHolder myViewHolder = (ViewHolder)holder;

        myViewHolder.name.setText(item.getCos_name());
        myViewHolder.caution.setText(item.getCaution_count());
        myViewHolder.allergy.setText(item.getAllergy_count());
        myViewHolder.good.setText(item.getGood_count());
        myViewHolder.image.setImageURI(Uri.parse(item.getUri()));
    }



    /*public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,caution,allergy,good;
        ImageView image;
        public MyViewHolder(View itemView,final OnItemClickEventListener a_itemClickListener) {
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
    }*/

}
