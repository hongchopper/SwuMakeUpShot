package com.example.swumakeupshot;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.common.InputImage;



public class anal_cos extends AppCompatActivity {
    Bitmap bitmap;
    ImageView imageView;
    InputImage image;
    public String cos_name;
    public String data;

    public String getCos_name() {
        return cos_name;
    }

    public void setCos_name(String cos_name) {
        this.cos_name = cos_name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
