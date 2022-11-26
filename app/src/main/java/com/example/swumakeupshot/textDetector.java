package com.example.swumakeupshot;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class textDetector extends AppCompatActivity {
    private static final int REQUEST_CODE = 101;

    private PermissionSupport permission;
    //static final int REQUEST_CODE = 2;
    public static final int REQUEST_TAKE_PHOTO = 10;
    public static final int REQUEST_PERMISSION = 11;
    private Executor executor = Executors.newSingleThreadExecutor();
    private int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    List<String> ingredient=new ArrayList<>();
    List<String> anal_allergy=new ArrayList<>();
    List<String> anal_caution=new ArrayList<>();
    List<String> anal_good=new ArrayList<>();
    StringBuilder middle=new StringBuilder();

    ImageView imageView;    // 갤러리에서 가져온 이미지를 보여줄 뷰
    Uri uri, xUri;                // 갤러리에서 가져온 이미지에 대한 Uri
    Bitmap bitmap;          // 갤러리에서 가져온 이미지를 담을 비트맵
    InputImage image;       // ML 모델이 인식할 인풋 이미지
    TextView text_info,caution_text,good_text,allergy_text,all,good,caution,allergy;     // ML 모델이 인식한 텍스트를 보여줄 뷰
    EditText cos_name;
    int all_count,caution_count,allergy_count,good_count;
    Button btn_get_image, btn_detection_image,btnCamera,btn_save;  // 이미지 가져오기 버튼, 이미지 인식 버튼
    TextRecognizer recognizer;    //텍스트 인식에 사용될 모델
    private String mCurrentPhotoPath;
    public List<caution_ingredients> ciList ;
    private String[] permissions={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.READ_EXTERNAL_STORAGE};

    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context=this;
        cos_name=(EditText)findViewById(R.id.makeup_editname);
        imageView = (ImageView)findViewById(R.id.quick_start_cropped_image);
        text_info = (TextView) findViewById(R.id.text_info);
        all=(TextView)findViewById(R.id.all);
        caution_text=(TextView)findViewById(R.id.caution_text);
        caution=(TextView)findViewById(R.id.caution);
        allergy_text=(TextView)findViewById(R.id.allergy_text);
        allergy=(TextView)findViewById(R.id.allergy);
        good_text=(TextView)findViewById(R.id.good_text);
        good=(TextView)findViewById(R.id.good);

        recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());    //텍스트 인식에 사용될 모델

        //btn_save=findViewById(R.id.save);
        btn_save.setOnClickListener(new View.OnClickListener() {
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
                insertTotalDB("anal_total","sum_allergy",String.valueOf(allergy_count),"sum_caution",String.valueOf(caution_count),"sum_good",String.valueOf(good_count));
                ((MainActivity)MainActivity.context).displayList();
            }


        });

        // CAMERA CLICK 버튼
        btnCamera = findViewById(R.id.onCameraClick);
        //btnCamera.setOnClickListener(v -> captureCamera());
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("버튼","카메라 버튼눌럿음");
                permissionCheck();
                Intent intent = new Intent(getApplicationContext(), cameraX.class);
                startActivityForResult(intent,0);
            }
        });


        // GET IMAGE 버튼
        btn_get_image = findViewById(R.id.getPic);
        btn_get_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("버튼","앨범 버튼눌럿음");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // IMAGE DETECTION 버튼
        btn_detection_image = findViewById(R.id.onSelectImageClick);
        btn_detection_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("버튼","분석 버튼눌럿음");
                TextRecognition(recognizer);
            }
        });
    }
    private void initCautionDB() {

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        ciList = mDbHelper.getTableData();
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
            }
        }
        Log.e("주의성분 결과", String.valueOf(anal_caution));
        caution.setText("/ "+caution_count+" 개");
        // db 닫기
        mDbHelper.close();
    }
    private void initAllergyDB() {

        DataAdapter2 mDbHelper = new DataAdapter2(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        ciList = mDbHelper.getTableData();
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
            }
        }
        Log.e("알러지 결과", String.valueOf(anal_allergy));
        allergy.setText("/ "+allergy_count+" 개");
        // db 닫기
        mDbHelper.close();
    }

    private void initGoodDB() {

        DataAdapter3 mDbHelper = new DataAdapter3(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        ciList = mDbHelper.getTableData();
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
    private void insertTotalDB(String table_name,String col_name1,String name1,String col_name2,String name2,String col_name3,String name3){
        DataBaseHelper2 dbHelper=new DataBaseHelper2(this);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        ContentValues cv=new ContentValues();
        cv.put("cos_name",cos_name.getText().toString());
        cv.put(col_name1,name1);
        cv.put(col_name2,name2);
        cv.put(col_name3,name3);

        db.insert(table_name,null,cv);
        //Log.e("삽입 결과",name);
        db.close();
        dbHelper.close();
    }

    // uri를 비트맵으로 변환시킨후 이미지뷰에 띄워주고 InputImage를 생성하는 메서드
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

    private void TextRecognition(TextRecognizer recognizer){
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
                                text_info.append(ingredient.get(i));
                            }else if(ingredient.get(i).contains("전성분") || ingredient.get(i).contains("성분")|| ingredient.get(i).contains("성분명")){
                                Log.e("전성분 포함 글자",ingredient.get(i));
                                String result2=ingredient.get(i).substring(4);
                                Log.e("전성분 뺀 글자",result2);
                                text_info.append(result2);
                                text_info.append(", ");
                                all_count-=1;
                            }
                            else{
                                text_info.append(ingredient.get(i));
                                text_info.append(", ");
                            }
                        }
                        initCautionDB();
                        initAllergyDB();
                        initGoodDB();
                        all.setText("/ "+all_count+" 개");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            // 갤러리에서 선택한 사진에 대한 uri를 가져온다.
            uri = intent.getData();
            setImage(uri);
        }
        else if (requestCode == 0){
            xUri=intent.getParcelableExtra("xUri");
            try {

                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), xUri);
                Matrix matrix = new Matrix();

                matrix.postRotate(60);

                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, width, height, true);

                Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                imageView.setImageBitmap(rotatedBitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            setImage(xUri);
            Log.e("URI 받기 성공",xUri.toString());
        }
        /*try {
            //after capture
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {

                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            //사진해상도가 너무 높으면 비트맵으로 로딩
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 8; //8분의 1크기로 비트맵 객체 생성
                            Bitmap bitmap2 = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                            Bitmap rotatedBitmap = null;
                            switch (orientation) {

                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotatedBitmap = rotateImage(bitmap, 90);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotatedBitmap = rotateImage(bitmap, 180);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotatedBitmap = rotateImage(bitmap, 270);
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotatedBitmap = bitmap;
                            }

                            //Rotate한 bitmap을 ImageView에 저장
                            imageView.setImageBitmap(rotatedBitmap);
                            image = InputImage.fromBitmap(bitmap, 0);

                        }
                    }
                    break;
                }
            }

        } catch (Exception e) {
            Log.w(TAG, "onActivityResult Error !", e);
        }*/
    }
    //카메라에 맞게 이미지 로테이션
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void permissionCheck(){
        // sdk 23버전 이하 버전에서는 permission이 필요하지 않음
        if(Build.VERSION.SDK_INT >= 23){

            // 클래스 객체 생성
            permission =  new PermissionSupport(this, this);

            // 권한 체크한 후에 리턴이 false일 경우 권한 요청을 해준다.
            if(!permission.checkPermission()){
                permission.requestPermission();
            }
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // 권한이 취소되면 result 배열은 비어있다.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 확인", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "권한 없음", Toast.LENGTH_SHORT).show();
                    finish(); //권한이 없으면 앱 종료
                }
                break;
            }
        }
    }*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /*// 리턴이 false일 경우 다시 권한 요청
        if (!permission.permissionResult(requestCode, permissions, grantResults)){
            permission.requestPermission();
        }*/
    }
}


