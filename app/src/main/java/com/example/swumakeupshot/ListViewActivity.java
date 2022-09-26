package com.example.swumakeupshot;

import android.app.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListViewActivity extends AppCompatActivity {
    private String TAG = ListViewActivity.class.getSimpleName();

    private ListView listview = null;
    private ListViewAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new ListViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new BearItem("1", "파랑이", R.drawable.ocean));
        adapter.addItem(new BearItem("2", "민트트", R.drawable.ocean));
        adapter.addItem(new BearItem("3", "하늘이", R.drawable.ocean));
        adapter.addItem(new BearItem("4", "하양이", R.drawable.ocean));
        adapter.addItem(new BearItem("5", "분홍이", R.drawable.ocean));
        adapter.addItem(new BearItem("6", "노랑이", R.drawable.ocean));
        adapter.addItem(new BearItem("7", "보라라", R.drawable.ocean));
        adapter.addItem(new BearItem("8", "믹스스", R.drawable.ocean));

        //리스트뷰에 Adapter 설정
        listview.setAdapter(adapter);
    }


    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<BearItem> items = new ArrayList<BearItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(BearItem item) {
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final Context context = viewGroup.getContext();
            final BearItem bearItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_list_item, viewGroup, false);

            } else {
                View view = new View(context);
                view = (View) convertView;
            }
/*
            TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);

            tv_num.setText(bearItem.getNum());
            tv_name.setText(bearItem.getName());
            iv_icon.setImageResource(bearItem.getResId());
            Log.d(TAG, "getView() - [ " + position + " ] " + bearItem.getName());
*/
            //각 아이템 선택 event
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, bearItem.getNum() + " 번 - " + bearItem.getName() + " 입니당! ", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

}