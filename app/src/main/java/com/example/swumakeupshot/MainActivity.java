package com.example.swumakeupshot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton camerabtn;
    private ListView listview = null;
    private ListViewAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        camerabtn=(FloatingActionButton)findViewById(R.id.camera_btn);

        listview = (ListView) findViewById(R.id.main_listview);
        adapter = new ListViewAdapter();

        //Adapter 안에 아이템의 정보 담기
        adapter.addItem(new ListItem("1", "파랑이", R.drawable.ocean));
        adapter.addItem(new ListItem("2", "민트트", R.drawable.ocean));
        adapter.addItem(new ListItem("3", "하늘이", R.drawable.ocean));
        adapter.addItem(new ListItem("4", "하양이", R.drawable.ocean));
        adapter.addItem(new ListItem("5", "분홍이", R.drawable.ocean));
        adapter.addItem(new ListItem("6", "노랑이", R.drawable.ocean));
        adapter.addItem(new ListItem("7", "보라라", R.drawable.ocean));
        adapter.addItem(new ListItem("8", "믹스스", R.drawable.ocean));

        //리스트뷰에 Adapter 설정
        listview.setAdapter(adapter);

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    /* 리스트뷰 어댑터 */
    public class ListViewAdapter extends BaseAdapter {
        ArrayList<ListItem> items = new ArrayList<ListItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(ListItem item) {
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
            final ListItem bearItem = items.get(position);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.main_list_item, viewGroup, false);

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
                    Intent intent = new Intent(getApplicationContext(), SubActivity.class);
                    startActivity(intent);
                    //Toast.makeText(context, bearItem.getNum() + " 번 - " + bearItem.getName() + " 입니당! ", Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;  //뷰 객체 반환
        }
    }

}