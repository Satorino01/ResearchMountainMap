package com.example.kobayashi_satoru.mountainmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;

//FragmentActivityでFragmentクラスを継承します
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap mMap;
    LocationManager locationManager;
    static int Makercount=0;

    // Fragmentで表示するViewを作成するメソッド
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(2000);// ここで2秒間スリープし、スプラッシュを表示させたままにする。
        } catch (InterruptedException e) {
        }
        setTheme(R.style.AppTheme);// スプラッシュの表示動作指定。themeを通常themeに変更する

        setContentView(R.layout.activity_maps);//表示するレイアウトxmlの指定(res/layout/.xml)

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);//地図が使われる準備ができているとき、SupportMapFragmentを得て、通知されてください。
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(35.6140332,139.4945413)).title("小林慧の").snippet("自分の部屋"));
        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "マーカータップ", Toast.LENGTH_LONG).show();
                return false;
            }*/
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
        /*ここ消す　0.5秒間隔でチェックし直す設定*/
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //    /*TODO: Consider calling
        //    なくなった許可、そして、圧倒的市民を要請するために、ここのActivityCompat#requestPermissionsは、
        //    ユーザーが許可を与えるケースを取り扱うために、onRequestPermissionsResult（int requestCode、String［］許可、int［］grantResults）
        //    を無効にします。詳細はActivityCompat#requestPermissionsについてはドキュメンテーションを見てください。*/
         /*ここ消す*/
        final long minTime = 500;/* 位置情報の通知するための最小時間間隔（ミリ秒） */
        final long minDistance = 1;/* 位置情報を通知するための最小距離間隔（メートル）*/
        // ↓利用可能なロケーションプロバイダによる位置情報の取得の開始FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        targetlocation = locationManager.getLastKnownLocation(locationProvider);// 最新の位置情報
        double x = targetlocation.getLatitude();//緯度の代入
        double y = targetlocation.getLongitude();// 経度の代入
        long nowTimeUTC = targetlocation.getTime();//UNIX時刻
        String japTime = UnixTimeTrans(nowTimeUTC);//UNIX時刻を文字列に変換
        float targetSpeed = targetlocation.getSpeed();//速度	確認用hasSpeed()	単位m毎秒
        float targetDirection = targetlocation.getBearing();//方位 確認用hasBearing()	北が０で時計回りに増加します。
        LatLng myLocation = new LatLng(x, y);
        mMap.addMarker(new MarkerOptions().position(myLocation).title(japTime));//.icon(BitmapDescriptorFactory.fromResource(R.drawable.markerpin)).snippet(japTime)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        CountView();

        //CameraPosition cameraPos = new CameraPosition.Builder().target(new LatLng(targetlocation.getLatitude(), targetlocation.getLongitude())).zoom(21).bearing(0).build();
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos));
    }
    public void CountView(){
        Makercount+=1;
        String kazumo = String.valueOf(Makercount);
        TextView textViewHensuu = findViewById(R.id.countView);
        textViewHensuu.setText("マーカー数："+kazumo);
    }

    //long型UNIX時間をStringを日本の日付に変換するメソッド
    public String UnixTimeTrans(long TimeUNIX){
        Calendar currCal = Calendar.getInstance();
        currCal.setTimeInMillis(TimeUNIX);
        Date currCalDate = new Date(TimeUNIX);
        String japTime = String.valueOf(currCalDate);//Mon Dec 18 16:07:51 GMT+09:00 2017
        String[] timeList = japTime.split(" ");
        String[] timeMinuteSecond = timeList[3].split(":");//16:07:51
        String month="";
        if(timeList[1].equals("Jan")){
            month="1";
        }else if(timeList[1].equals("Feb")){
            month="2";
        }else if(timeList[1].equals("Mar")){
            month="3";
        }else if(timeList[1].equals("Apr")){
            month="4";
        }else if(timeList[1].equals("May")){
            month="5";
        }else if(timeList[1].equals("Jun")){
            month="6";
        }else if(timeList[1].equals("Jul")){
            month="7";
        }else if(timeList[1].equals("Aug")){
            month="8";
        }else if(timeList[1].equals("Sep")){
            month="9";
        }else if(timeList[1].equals("Oct")){
            month="10";
        }else if(timeList[1].equals("Nov")){
            month="11";
        }else if(timeList[1].equals("Dec")){
            month="12";
        }
        String useTime = (timeList[5]+"年"+ month +"月"+ timeList[2] +"日"+ timeMinuteSecond[0] +"時"+ timeMinuteSecond[1] +"分"+ timeMinuteSecond[2] +"秒");
        return useTime;
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

    // 終了ボタン用
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//finishAndRemoveTaskの自動生成
    public void onClickFinish(View view){
        finishAndRemoveTask();//タスク一覧から消す&アプリの終了。APIレベル21以降から使える
        finish();//アプリの終了。タスク一覧に空の画面が出っ放し。↑が実行されなかったとき用
    }

}