package com.example.kobayashi_satoru.mountainmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

public class InfoWindowResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_window_result);
        //追加ゲット
        Intent intent = getIntent();
        String PhotoID = intent.getStringExtra("画像データ");
        if(PhotoID.equals("null0")){
        }else {
            /*Toast toast = Toast.makeText(this,
                    PhotoID, Toast.LENGTH_SHORT);
            toast.show();
            */
            ImageView imageView = findViewById(R.id.imageViewScenery);
            int viewId = getResources().getIdentifier(PhotoID, "drawable", getPackageName());
            imageView.setImageResource(viewId);
            imageView.invalidate();
        }
    }
}
