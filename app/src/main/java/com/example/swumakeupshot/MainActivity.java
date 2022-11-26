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
import android.widget.AdapterView;
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
    private ListView lvList = null;
    private ListViewAdapter adapter = null;
    public static Context context;

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
        context=this;
        camerabtn=(FloatingActionButton)findViewById(R.id.camera_btn);

        //권한요청
        ActivityCompat.requestPermissions(MainActivity.this, permissions,  1);

        lvList = (ListView) findViewById(R.id.main_listview);



        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), textDetector.class);
                startActivity(intent);
            }
        });
        displayList();

    }

    public void displayList(){
        //Dbhelper의 읽기모드 객체를 가져와 SQLiteDatabase에 담아 사용준비
        DataBaseHelper2 helper = new DataBaseHelper2(this);
        SQLiteDatabase database = helper.getReadableDatabase();

        //Cursor라는 그릇에 목록을 담아주기
        Cursor cursor = database.rawQuery("SELECT * FROM anal_total",null);

        //리스트뷰에 목록 채워주는 도구인 adapter준비
        ListViewAdapter adapter = new ListViewAdapter();

        //목록의 개수만큼 순회하여 adapter에 있는 list배열에 add
        while(cursor.moveToNext()){
            //num 행은 가장 첫번째에 있으니 0번이 되고, name은 1번
            adapter.addItemToList(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
        }

        //리스트뷰의 어댑터 대상을 여태 설계한 adapter로 설정
        lvList.setAdapter(adapter);

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