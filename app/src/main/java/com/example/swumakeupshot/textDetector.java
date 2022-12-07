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

    ImageView imageView;    // 갤러리에서 가져온 이미지를 보여줄 뷰
    Uri uri=null, xUri=null;                // 갤러리에서 가져온 이미지에 대한 Uri
    Bitmap bitmap;          // 갤러리에서 가져온 이미지를 담을 비트맵
    InputImage image;       // ML 모델이 인식할 인풋 이미지
    TextView text_info,caution_text,good_text,allergy_text,all,good,caution,allergy;     // ML 모델이 인식한 텍스트를 보여줄 뷰
    EditText cos_name;
    Button btn_get_image, btn_detection_image,btnCamera,btn_save;  // 이미지 가져오기 버튼, 이미지 인식 버튼
    TextRecognizer recognizer;    //텍스트 인식에 사용될 모델

    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        context=this;
        cos_name=(EditText)findViewById(R.id.makeup_editname);
        imageView = (ImageView)findViewById(R.id.quick_start_cropped_image);
        //text_info = (TextView) findViewById(R.id.text_info);
        //all=(TextView)findViewById(R.id.all);
        caution_text=(TextView)findViewById(R.id.caution_text);
        caution=(TextView)findViewById(R.id.caution);
        allergy_text=(TextView)findViewById(R.id.allergy_text);
        allergy=(TextView)findViewById(R.id.allergy);
        good_text=(TextView)findViewById(R.id.good_text);
        good=(TextView)findViewById(R.id.good);

        recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());    //텍스트 인식에 사용될 모델

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
                //TextRecognition(recognizer);
                Intent callIntent=new Intent(getApplicationContext(), SubActivity.class);
                if (uri==null){
                    callIntent.putExtra("Uri",xUri);
                }else{
                    callIntent.putExtra("Uri",uri);
                }
                startActivity(callIntent);
            }
        });
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}


