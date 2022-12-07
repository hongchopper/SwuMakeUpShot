package com.example.swumakeupshot;

import static com.example.swumakeupshot.DataAdapter.TAG;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailActivity extends AppCompatActivity {
    TextView cos_name,allergy_text,caution_text,good_text;
    ImageView cos_image;
    String allergy,caution,good,allergyComment,cautionComment,goodComment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        String name = intent.getStringExtra("화장품 이름");
        String uri = intent.getStringExtra("화장품 uri");

        cos_name = findViewById(R.id.makeup_name);
        cos_image = findViewById(R.id.makeup_someimg);
        allergy_text = findViewById(R.id.allergy_text);
        caution_text=findViewById(R.id.caution_text);
        good_text=findViewById(R.id.good_text);

        cos_name.setText(name);
        cos_image.setImageURI(Uri.parse(uri));

        getAllergyIngredient("anal_allergy",name);
        getCautionIngredient("anal_cautious",name);
        getGoodIngredient("anal_good",name);
    }

    public void getAllergyDetail(String tablename,String igname){
        DataBaseHelper helper = new DataBaseHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+tablename+" where name = "+"\""+igname+"\"", null);

        if (cursor != null) {
            // 칼럼의 마지막까지
            while (cursor.moveToNext()) {

                allergyComment=cursor.getString(1);
                allergy_text.append(allergyComment);
                allergy_text.append("\n");
            }
        }
        database.close();
    }
    public void getCautionDetail(String tablename,String igname){
        DataBaseHelper helper = new DataBaseHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+tablename+" where name = "+"\""+igname+"\"", null);

        if (cursor != null) {
            // 칼럼의 마지막까지
            while (cursor.moveToNext()) {

                cautionComment=cursor.getString(1);
                caution_text.append(cautionComment);
                caution_text.append("\n");
            }
        }
        database.close();
    }
    public void getGoodDetail(String tablename,String igname){
        DataBaseHelper helper = new DataBaseHelper(this);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+tablename+" where name = "+"\""+igname+"\"", null);

        if (cursor != null) {
            // 칼럼의 마지막까지
            while (cursor.moveToNext()) {

                goodComment=cursor.getString(1);
                good_text.append(goodComment);
                good_text.append("\n");
            }
        }
        database.close();
    }

    public void getAllergyIngredient(String tablename,String name){
        DataBaseHelper2 helper2 = new DataBaseHelper2(this);
        SQLiteDatabase database = helper2.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+ tablename+" where cos_name = "+"\""+name+"\"", null);

        if (cursor != null) {
            // 칼럼의 마지막까지
            while (cursor.moveToNext()) {
                allergy=cursor.getString(1);
                allergy_text.append("• ");
                allergy_text.append(allergy);
                allergy_text.append(": ");
                getAllergyDetail("allergy_ingredients_table",allergy);
            }
        }
        database.close();
    }
    public void getCautionIngredient(String tablename,String name){
        DataBaseHelper2 helper2 = new DataBaseHelper2(this);
        SQLiteDatabase database = helper2.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+ tablename+" where cos_name = "+"\""+name+"\"", null);

        if (cursor != null) {
            // 칼럼의 마지막까지
            while (cursor.moveToNext()) {
                caution=cursor.getString(1);
                caution_text.append("• ");
                caution_text.append(caution);
                caution_text.append(": ");
                getCautionDetail("caution_ingredients_table",caution);
            }
        }
        database.close();
    }
    public void getGoodIngredient(String tablename,String name){
        DataBaseHelper2 helper2 = new DataBaseHelper2(this);
        SQLiteDatabase database = helper2.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from "+ tablename+" where cos_name = "+"\""+name+"\"", null);

        if (cursor != null) {
            // 칼럼의 마지막까지
            while (cursor.moveToNext()) {
                good=cursor.getString(1);
                good_text.append("• ");
                good_text.append(good);
                good_text.append(": ");
                getGoodDetail("good_ingredients_table",good);
            }
        }
        database.close();
    }
}
