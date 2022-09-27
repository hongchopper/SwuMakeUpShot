package com.example.swumakeupshot;

import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SubActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();

    private ListView listview = null;
    private MainActivity.ListViewAdapter adapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        listview = (ListView) findViewById(R.id.listview);
        //adapter = new SubActivity().ListViewAdapter();
    }
}
