package com.example.kobayashi_satoru.mountainmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

//FragmentActivityでFragmentクラスを継承します
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap mMap;
    LocationManager locationManager;

    // Fragmentで表示するViewを作成するメソッド
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);//表示するxmlの指定
        //地図が使われる準備ができているとき、SupportMapFragmentを得て、通知されてください。
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*かつて利用できる地図を操ります。地図が使われる準備ができているとき、このコールバックは引き起こされます。
        これは、我々が目印または線を加えることができるか、リスナーを加えることができるか、カメラを動かすことができるところです。
        このケースでは、我々はちょうどシドニー（オーストラリア）の近くで、目印を加えます。
        Playがサービスを提供するGoogleが装置上にインストールされないならば、ユーザーはSupportMapFragment内部にそれをインストールすることを促されます。
        一旦ユーザーがGoogle Playサービスを装置して、アプリに戻るならば、この方法は引き起こされるだけです。*/
        //↓コピペで意味不明
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    1000);
        } else {
            locationStart();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 50, this);
        }
    }
    //mMapにgooglemapを代入
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    //↓コピペで意味不明,onCreateで呼ばれてる
    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        } else {
            // GPSを設定するように促す
            Intent settingsIntent =
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            Log.d("debug", "checkSelfPermission false");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 50, this);
    }
    //↓コピペで意味不明
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug", "checkSelfPermission true");

                locationStart();

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
    //LocationListenerで自動生成された必要な関数.位置情報が変化すると、onLocationChangedがコールバックされる
    //getLatitude()とgetLongitude()を使って緯度経度を取得可能
    //位置情報が通知されるたびにコールバックされるメソッド
    @Override
    public void onLocationChanged(Location targetlocation) {
        /*ここ消す　0.5秒間隔でチェックし直す設定
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            /*TODO: Consider calling
            なくなった許可、そして、圧倒的市民を要請するために、ここのActivityCompat#requestPermissionsは、
            ユーザーが許可を与えるケースを取り扱うために、onRequestPermissionsResult（int requestCode、String［］許可、int［］grantResults）
            を無効にします。詳細はActivityCompat#requestPermissionsについてはドキュメンテーションを見てください。
            return;
        }
         ここ消す*/
        //final long minTime = 500;/* 位置情報の通知するための最小時間間隔（ミリ秒） */
        //final long minDistance = 1;/* 位置情報を通知するための最小距離間隔（メートル）*/
        // ↓利用可能なロケーションプロバイダによる位置情報の取得の開始FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        //locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        //targetlocation = locationManager.getLastKnownLocation(locationProvider);// 最新の位置情報
        double x = targetlocation.getLatitude();//緯度の代入
        double y = targetlocation.getLongitude();// 経度の代入
        LatLng myLocation = new LatLng(x, y);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("ここにいます。"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
    }
    //LocationListenerで自動生成された必要な関数
    //ロケーションステータスが変わるとコールバックされるメソッド
    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        // 利用可能なプロバイダの利用状態が変化したときに呼ばれる
        //↓コピペ,意味不明,
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }
    //LocationListenerで自動生成された必要な関数
    //ロケーションプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String s) {
        //プロバイダが利用可能になったら呼ばれる
    }
    //LocationListenerで自動生成された必要な関数
    //ロケーションプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String s) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
    }
}