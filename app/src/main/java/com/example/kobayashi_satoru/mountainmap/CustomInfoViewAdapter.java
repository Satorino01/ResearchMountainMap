package com.example.kobayashi_satoru.mountainmap;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import static android.app.PendingIntent.getActivity;
import static android.content.Intent.getIntent;


/**
 * Created by kobayashi_satoru on 2018/01/01.
 */

public class CustomInfoViewAdapter implements GoogleMap.InfoWindowAdapter {
    private final LayoutInflater mInflater;

    public CustomInfoViewAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
    }

    @Override public View getInfoWindow(Marker marker) {
        final View popup = mInflater.inflate(R.layout.info_window_layout, null);

        ((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

        return popup;
        //return null;
    }

    @Override public View getInfoContents(Marker marker) {
        final View popup = mInflater.inflate(R.layout.info_window_layout, null);

        ((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

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
