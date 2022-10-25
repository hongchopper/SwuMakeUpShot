package com.example.swumakeupshot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    //DBHelper helper;
    SQLiteDatabase db;

    String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        camerabtn=(FloatingActionButton)findViewById(R.id.camera_btn);
        //권한요청
        ActivityCompat.requestPermissions(MainActivity.this, permissions,  1);

        listview = (ListView) findViewById(R.id.main_listview);
        adapter = new ListViewAdapter();

        /*helper = new DBHelper(MainActivity.this, "newdb.db", null, 1);
        db = helper.getWritableDatabase();
        helper.onCreate(db);*//*

        //쿼리
        String sql = "select * from mytable;";
        Cursor c = db.rawQuery(sql, null);
        String[] strs = new String[]{"txt", "uri"};
        int[] ints = new int[] {R.id.makeup_img, R.id.makeup_text};

        MyCursorAdapter adapter = null;
        adapter = new MyCursorAdapter(listview.getContext(), R.layout.main_list_item, c, strs, ints);
        //SimpleCursorAdapter adapter = null;
        //adapter = new SimpleCursorAdapter(listview.getContext(), R.layout.main_list_item, c, strs, ints,0);

        listview.setAdapter(adapter);*/

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), textDetector.class);
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

    //타이틀바 맨 우측 버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.options_menu, menu);
        //길이 조정
        SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        //검색 시 힌트 추가
        searchView.setQueryHint("화장품명을 검색합니다.");
        MenuItem item_like = menu.add(0,0,0,"삭제하기");
        item_like.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //삭제코드구현
                return true;
            }
        });
        return true;
    }

    //타이틀바 우측 두번째, 검색 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //검색버튼 눌렀을때, 이벤트 제어
        if(id==R.id.search){
            //검색했을때 쿼리 구현
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}