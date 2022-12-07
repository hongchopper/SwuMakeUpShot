package com.example.swumakeupshot;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SubActivity extends AppCompatActivity {
    List<String> ingredient=new ArrayList<>();
    List<String> anal_allergy=new ArrayList<>();
    List<String> anal_caution=new ArrayList<>();
    List<String> anal_good=new ArrayList<>();
    StringBuilder middle=new StringBuilder();

    ImageView imageView;
    EditText cos_name;
    Button save_btn,back_btn;
    TextView text_info,caution_text,good_text,allergy_text,all,good,caution,allergy;
    TextRecognizer recognizer;
    int all_count,caution_count,allergy_count,good_count;
    Uri uri;
    Bitmap bitmap;
    InputImage image;
    int _id;
    public List<caution_ingredients> ciList ;
    public static Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub);

        imageView = (ImageView)findViewById(R.id.makeup_someimg);
        cos_name=findViewById(R.id.makeup_editname);
        //text_info = (TextView) findViewById(R.id.text_info);
        //all=(TextView)findViewById(R.id.all);
        caution_text=(TextView)findViewById(R.id.caution_text);
        caution=(TextView)findViewById(R.id.caution);
        allergy_text=(TextView)findViewById(R.id.allergy_text);
        allergy=(TextView)findViewById(R.id.allergy);
        good_text=(TextView)findViewById(R.id.good_text);
        good=(TextView)findViewById(R.id.good);
        back_btn=findViewById(R.id.onBackPress);
        save_btn=findViewById(R.id.save);

        recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

        Intent intent = getIntent();
        uri = intent.getExtras().getParcelable("Uri");
        setImage(uri);
        Log.e("인텐트로 전달된 URI",uri.toString());

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i = 0; i < anal_allergy.size(); i++){
                    insertDB("anal_allergy","allergy_ingredient", anal_allergy.get(i));
                }
                for(int i = 0; i < anal_caution.size(); i++){
                    insertDB("anal_cautious","cautious_ingredient", anal_caution.get(i));
                }
                for(int i = 0; i < anal_good.size(); i++){
                    insertDB("anal_good","good_ingredient", anal_good.get(i));
                }
                insertTotalDB("anal_total","sum_allergy",String.valueOf(allergy_count),"sum_caution",String.valueOf(caution_count),"sum_good",String.valueOf(good_count),"_id",_id,
                        "cos_img",uri.toString());
                //((MainActivity)MainActivity.context).displayRecycleList();
                Toast.makeText(SubActivity.this,"저장되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),textDetector.class);
                startActivity(intent);
            }
        });

        TextRecognition(recognizer);
    }

    private void setImage(Uri uri) {
        try{
            InputStream in = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(in);
            imageView.setImageBitmap(bitmap);

            image = InputImage.fromBitmap(bitmap, 0);
            Log.e("setImage", "이미지 to 비트맵");
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    private void initCautionDB() {

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        ciList = mDbHelper.getTableData("caution_ingredients_table");
        Iterator<caution_ingredients> iterator = ciList.iterator();

        while (iterator.hasNext()) {
            caution_ingredients element = iterator.next();
            for(int i = 0; i < ingredient.size(); i++){
                if (ingredient.get(i).trim().equals(element.getName())){
                    caution_count+=1;
                    anal_caution.add(element.name);
                    caution_text.append("• ");
                    caution_text.append(ingredient.get(i));
                    caution_text.append(": ");
                    caution_text.append(element.getComment());
                    caution_text.append("\n");
                    break;
                }
                else if(ingredient.get(i).trim().equals(element.getEng_name())){
                    caution_count+=1;
                    anal_caution.add(element.name);
                    caution_text.append("• ");
                    caution_text.append(ingredient.get(i));
                    caution_text.append("(= ");
                    caution_text.append(element.name);
                    caution_text.append("): ");
                    //caution_text.append(": ");
                    caution_text.append(element.getComment());
                    caution_text.append("\n");
                    break;
                }
            }
        }
        Log.e("주의성분 결과", String.valueOf(anal_caution));
        caution.setText("/ "+caution_count+" 개");
        // db 닫기
        mDbHelper.close();
    }
    private void initAllergyDB() {

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        ciList = mDbHelper.getTableData("allergy_ingredients_table");
        Iterator<caution_ingredients> iterator = ciList.iterator();

        while (iterator.hasNext()) {
            caution_ingredients element = iterator.next();
//            Log.e("전체 결과", element.getName());
            for(int i = 0; i < ingredient.size(); i++){
                if (ingredient.get(i).trim().equals(element.getName())) {
                    allergy_count += 1;
                    anal_allergy.add(element.name);
                    allergy_text.append("• ");
                    allergy_text.append(ingredient.get(i));
                    allergy_text.append(": ");
                    allergy_text.append(element.getComment());
                    allergy_text.append("\n");
                    break;
                }
                else if(ingredient.get(i).trim().equals(element.getEng_name())){
                    allergy_count+=1;
                    anal_allergy.add(element.name);
                    allergy_text.append("• ");
                    allergy_text.append(ingredient.get(i));
                    allergy_text.append("(= ");
                    allergy_text.append(element.name);
                    allergy_text.append("): ");
                    allergy_text.append(element.getComment());
                    allergy_text.append("\n");
                    break;
                }
            }
        }
        Log.e("알러지 결과", String.valueOf(anal_allergy));
        allergy.setText("/ "+allergy_count+" 개");
        // db 닫기
        mDbHelper.close();
    }

    private void initGoodDB() {

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        ciList = mDbHelper.getTableData("good_ingredients_table");
        Iterator<caution_ingredients> iterator = ciList.iterator();

        while (iterator.hasNext()) {
            caution_ingredients element = iterator.next();
            for(int i = 0; i < ingredient.size(); i++){
                if (ingredient.get(i).trim().equals(element.getName())){
                    good_count+=1;
                    anal_good.add(element.name);
                    good_text.append("• ");
                    good_text.append(ingredient.get(i));
                    good_text.append(": ");
                    good_text.append(element.getComment());
                    good_text.append("\n");
                    break;
                }
            }
        }
        Log.e("좋은성분 결과", String.valueOf(anal_good));
        good.setText("/ "+good_count+" 개");
        // db 닫기
        mDbHelper.close();
    }


    private void insertDB(String table_name,String col_name,String name){
        DataBaseHelper2 dbHelper=new DataBaseHelper2(this);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("cos_name",cos_name.getText().toString());
        cv.put(col_name,name);

        db.insert(table_name,col_name,cv);
        Log.e("삽입 결과",name);
        db.close();
        dbHelper.close();
    }
    private void insertTotalDB(String table_name,String col_name1,String name1,String col_name2,String name2,String col_name3,String name3,String col_name4,int name4,String col_name5,String name5){
        DataBaseHelper2 dbHelper=new DataBaseHelper2(this);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("cos_name",cos_name.getText().toString());
        cv.put(col_name1,name1);
        cv.put(col_name2,name2);
        cv.put(col_name3,name3);
        cv.put(col_name4,name4);
        cv.put(col_name5,name5);
        _id+=1;

        db.insert(table_name,null,cv);
        //Log.e("삽입 결과",name);
        db.close();
        dbHelper.close();
    }

    public void TextRecognition(TextRecognizer recognizer){
        Task<Text> result = recognizer.process(image)
                // 이미지 인식에 성공하면 실행되는 리스너
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
//                        // Task completed successfully
                        String resultText = visionText.getText();
                        Log.e("텍스트 인식",resultText );

                        char[] result=resultText.toCharArray();

                        for(int i=0;i<result.length;i++){
                            if (resultText.charAt(i)==',' ||resultText.charAt(i)=='.'){
                                ingredient.add(middle.toString());
                                //text_info.append(middle);
                                middle.delete(0,i);
                                continue;
                            }
                            else if(resultText.charAt(i)=='\n'){
                                continue;
                            }
                            else{
                                middle.append(resultText.charAt(i));
                            }
                        }
                        //text_info.append(middle);
                        // ingredient 출력해보기
                        ingredient.add(middle.toString());
                        for(int i = 0; i < ingredient.size(); i++) {
                            all_count+=1;
                            if(i == ingredient.size()-1){
                                //text_info.append(ingredient.get(i));
                            }else if(ingredient.get(i).contains("전성분") || ingredient.get(i).contains("성분")|| ingredient.get(i).contains("성분명")){
                                Log.e("전성분 포함 글자",ingredient.get(i));
                                String result2=ingredient.get(i).substring(4);
                                Log.e("전성분 뺀 글자",result2);
                                //text_info.append(result2);
                                //text_info.append(", ");
                                all_count-=1;
                            }
                            else{
                                //text_info.append(ingredient.get(i));
                                //text_info.append(", ");
                            }
                            Log.e("전체 결과", ingredient.get(i));
                        }
                        initCautionDB();
                        initAllergyDB();
                        initGoodDB();
                        //all.setText("/ "+all_count+" 개");
                    }
                })
                // 이미지 인식에 실패하면 실행되는 리스너
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("텍스트 인식", "실패: " + e.getMessage());
                                Toast.makeText(getApplicationContext(),"텍스트 인식에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
    }
}
