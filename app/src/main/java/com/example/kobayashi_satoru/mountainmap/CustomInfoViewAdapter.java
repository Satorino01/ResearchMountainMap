package com.example.kobayashi_satoru.mountainmap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;


/**
 * Created by kobayashi_satoru on 2018/01/01.
 */

public class CustomInfoViewAdapter extends AppCompatActivity implements GoogleMap.InfoWindowAdapter {
    private final LayoutInflater mInflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public CustomInfoViewAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override public View getInfoWindow(Marker marker) {
        final View popup = mInflater.inflate(R.layout.info_window_layout, null);
        //int viewId = getResources().getIdentifier("takaozann"+marker.getSnippet(), "drawable", getPackageName());
        //Resources res = getResources();

        //Bitmap bitmap = BitmapFactory.decodeResource(res,R.drawable.takaozann1);
        // bitmapの画像を200×45で作成する
        //Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 200, 100, false);
        //((ImageView) popup.findViewById(R.id.title)).setImageBitmap(bitmap2);
        String PhotoID = marker.getSnippet();
        ((ImageView) popup.findViewById(R.id.title)).setImageResource(Integer.parseInt(PhotoID));
        //((ImageView) popup.findViewById(R.id.title)).setImageResource(R.drawable.takaozann1);
        return popup;
        //return null;
    }

    @Override public View getInfoContents(Marker marker) {
        final View popup = mInflater.inflate(R.layout.info_window_layout, null);

        //((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

        return popup;
    }
    /*
    @Override
    public String onNewIntent(Intent intent) {
        String PhotoID =intent.getStringExtra("画像データ");   // セットした値が取り出せる
        return PhotoID;
    }
    */
}
