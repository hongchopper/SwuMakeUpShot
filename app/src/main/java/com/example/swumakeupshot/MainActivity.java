package com.example.swumakeupshot;

import static com.example.swumakeupshot.DataAdapter.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton camerabtn;
    Bitmap bitmap;
    InputImage image;
    ImageView imageView;
    //private ListViewAdapter adapter = null;
    public static Context context;
    ArrayList<ListItem> dataModels = new ArrayList<>();

    private Adapter adapter;

    //DBHelper helper;
    SQLiteDatabase db;
    //DataBaseHelper helper=new DataBaseHelper(this);
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

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), textDetector.class);
                startActivity(intent);
            }
        });
        displayRecycleList();
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String name=dataModels.get(position).getCos_name();
                String uri=dataModels.get(position).getUri();
                Intent intent=new Intent(getApplicationContext(),DetailActivity.class);
                intent.putExtra("화장품 이름",name);
                intent.putExtra("화장품 uri",uri);
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new Adapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                getMenuInflater().inflate(R.menu.main_list_menu, popup.getMenu());
                Toast.makeText(MainActivity.this, "꾹 누르기", Toast.LENGTH_SHORT).show();
                // Popup menu click event 처리
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem a_item) {

                        String name = dataModels.get(position).getCos_name();
                        switch (a_item.getItemId()) {
                            case R.id.action_delete:
                                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
    });
    }

    public void displayRecycleList(){
        DataBaseHelper2 helper2 = new DataBaseHelper2(this);
        SQLiteDatabase database = helper2.getReadableDatabase();

        adapter = new Adapter(getApplicationContext(),dataModels);

        RecyclerView recyclerView = findViewById(R.id.main_listview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        Cursor cursor = database.rawQuery("select * from anal_total",null);
        int recordCount = cursor.getCount();

        Log.d(TAG,"recordCount: " + recordCount);
        for (int i = 0; i < recordCount; i++){

            cursor.moveToNext();
            String name = cursor.getString(0);
            String caution = cursor.getString(1);
            String allergy = cursor.getString(2);
            String good = cursor.getString(3);
            String uri = cursor.getString(4);

            Log.d(TAG,"name: " + name);
            dataModels.add(new ListItem(name,caution,allergy,good,uri,getDrawable(R.drawable.ic_baseline_delete_24)));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
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