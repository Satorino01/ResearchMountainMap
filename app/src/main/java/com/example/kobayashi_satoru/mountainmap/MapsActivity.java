package com.example.kobayashi_satoru.mountainmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

//FragmentActivityでFragmentクラスを継承します
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,TextWatcher {

    GoogleMap mMap;
    LocationManager locationManager;
    static int Makercount = 0;
    static LatLng TargetLatLng = new LatLng(0,0);
    static String TargetMountainName = "null";
    static String TargetMountainNamePhotoID = "null";


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
        //test0();
        //test1();
        //ネットにつなっているかの初回確認
        TextView netStatusText = findViewById(R.id.netStatusView);
        if (netWorkCheck(this.getApplicationContext())){
            netStatusText.setText("NET：ON");
        } else {
            netStatusText.setText("NET：OFF");
        }
        //editTextのリアルタイム監視に必要なメソッド
        EditText edittext = findViewById(R.id.editText);
        edittext.addTextChangedListener(this);
    }

    // 緯度経度を入れて経路を検索
    private void test1() {
        String src_lat = "35.681382";
        String src_ltg = "139.7660842";
        String des_lat = "35.684752";
        String des_ltg = "139.707937";

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + src_lat + "," + src_ltg + "&daddr=" + des_lat + "," + des_ltg));
        startActivity(intent);
    }


    //mMapにgooglemapを代入
    private ClusterManager<StringClusterItem> mClusterManager;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        //現在位置の表示
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        //      mMap.addMarker(new MarkerOptions().position(new LatLng(35.6140332,139.4945413)).title("小林慧の").snippet("自分の部屋"));
        /*mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "マーカータップ", Toast.LENGTH_LONG).show();
                return false;
            }*/
        //クラスターマーカ用情報ウィンドウに必要なやつ
        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);

        //クラスターマーカ改造実行反映
        final CustomClusterRenderer renderer = new CustomClusterRenderer(this, mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        //クラスターをクリックした時の動作
        mClusterManager.setOnClusterClickListener(
                new ClusterManager.OnClusterClickListener<StringClusterItem>() {
                    @Override public boolean onClusterClick(Cluster<StringClusterItem> cluster) {

                        //Toast.makeText(MapsActivity.this, "Cluster click", Toast.LENGTH_SHORT).show();

                        // if true, do not move camera

                        return false;
                    }
                });
        //改造マーカーをクリックした時の動作
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<StringClusterItem>() {
                    @Override public boolean onClusterItemClick(StringClusterItem clusterItem) {

                        //Toast.makeText(MapsActivity.this, "Cluster item click", Toast.LENGTH_SHORT).show();

                        // if true, click handling stops here and do not show info view, do not move camera
                        // you can avoid this by calling:
                        // renderer.getMarker(clusterItem).showInfoWindow();

                        return false;
                    }
                });
        mMap.setOnMarkerClickListener(mClusterManager);
        //情報ウィンドウクリック用セット
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new CustomInfoViewAdapter(LayoutInflater.from(this)));
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        //情報ウィンドウクリック用
        final Intent intent = new Intent(this, InfoWindowResultActivity.class);
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<StringClusterItem>() {
            @Override public void onClusterItemInfoWindowClick(StringClusterItem stringClusterItem) {
                //Toast.makeText(MapsActivity.this, "Clicked info window: " + stringClusterItem.title, Toast.LENGTH_LONG).show();
                intent.putExtra("画像データ",stringClusterItem.title);
                startActivity(intent);

            }
        });
    }

    static class StringClusterItem implements ClusterItem {
        final String title;
        final LatLng latLng;
        public StringClusterItem(String title, LatLng latLng) {
            this.title = title;
            this.latLng = latLng;
        }
        @Override public LatLng getPosition() {
            return latLng;
        }
    }
    //クラスタリング改造クラス
    public class CustomClusterRenderer extends DefaultClusterRenderer<StringClusterItem> {
        private final Context mContext;
        public CustomClusterRenderer(Context context, GoogleMap map,ClusterManager<MapsActivity.StringClusterItem> clusterManager) {
            super(context, map, clusterManager);

            mContext = context;
        }
        //クラスタリングマーカー改造メソッド↓従来のマーカーをレンダリングする前に呼び出されます。
        @Override protected void onBeforeClusterItemRendered(MapsActivity.StringClusterItem item, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.photomarker)).snippet(item.title);
        }
        //クラスタリングの背景指定。クラスタをレンダリングする前に呼び出されます
        @Override protected void onBeforeClusterRendered(Cluster<StringClusterItem> cluster, MarkerOptions markerOptions) {
            final IconGenerator mClusterIconGenerator;
            // in constructor
            mClusterIconGenerator = new IconGenerator(mContext.getApplicationContext());

            mClusterIconGenerator.setBackground(
                    ContextCompat.getDrawable(mContext, R.drawable.background_circle));
            mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
            final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

    }

    //↓コピペで意味不明,onCreateで呼ばれてる
    private void locationStart() {
        Log.d("debug", "locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        final long minTime = 60000;/* 位置情報の通知するための最小時間間隔（ミリ秒） */
        final long minDistance = 10;/* 位置情報を通知するための最小距離間隔（メートル）*/
        // ↓利用可能なロケーションプロバイダによる位置情報の取得の開始FIXME 本来であれば、リスナが複数回登録されないようにチェックする必要がある
        assert locationManager != null;
        locationManager.requestLocationUpdates(locationProvider, minTime, minDistance, this);
        targetlocation = locationManager.getLastKnownLocation(locationProvider);// 最新の位置情報
        double x = targetlocation.getLatitude();//緯度の代入
        double y = targetlocation.getLongitude();// 経度の代入
        long nowTimeUTC = targetlocation.getTime();//UNIX時刻
        String japTime = UnixTimeTrans(nowTimeUTC);//UNIX時刻を文字列に変換
        float targetSpeed = targetlocation.getSpeed();//速度	確認用hasSpeed()	単位m毎秒
        float targetDirection = targetlocation.getBearing();//方位 確認用hasBearing()	北が０で時計回りに増加します。
        LatLng myLocation = new LatLng(x, y);

        CountView();
        if(Makercount==1) {
            cameraZoom(myLocation);
            TextView gpsStatusText = findViewById(R.id.gpsStatusView);
            gpsStatusText.setText("GPS：ON");
        }
        //MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.photomarker))
        if(Makercount==1||Makercount%10==0) {
            mMap.addMarker(new MarkerOptions().position(myLocation).anchor((float) 0.5, (float) 0.5).rotation(targetDirection).title(japTime).icon(BitmapDescriptorFactory.fromResource(R.drawable.tracker)));//
            //リバースジオコーディングの呼び出し
            String string = "null";
            ReverseGeocode rg = new ReverseGeocode();
            try {
                string = rg.point2address(this,targetlocation.getLatitude(), targetlocation.getLongitude());
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextView locationText = findViewById(R.id.locationView);
            locationText.setText("現在地："+ string);
        }
    }
    private void cameraZoom( LatLng location ) {
        float zoom = 14.0f; //ズームレベル
        float tilt = 0.0f; // 0.0 - 90.0  //チルトアングル
        float bearing = 0.0f; //向き
        CameraPosition pos = new CameraPosition(location, zoom, tilt, bearing); //CameraUpdate
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(pos);
        mMap.moveCamera(camera);
    }
    public void CountView(){
        Makercount+=1;
        String kazumo = String.valueOf(Makercount);
        TextView textViewHensuu = findViewById(R.id.countView);
        textViewHensuu.setText("位置情報取得回数："+kazumo);
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
    //GPSプロバイダの状態が変化するとコールバックされるメソッド
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
        //ネットにつなっているかの確認
        TextView netStatusText = findViewById(R.id.netStatusView);
        if (netWorkCheck(this.getApplicationContext())){
            netStatusText.setText("NET：ON");
        } else {
            netStatusText.setText("NET：OFF");
        }
    }

    //LocationListenerで自動生成された必要な関数
    //GPSプロバイダが利用可能になるとコールバックされるメソッド
    @Override
    public void onProviderEnabled(String s) {
        //プロバイダが利用可能になったら呼ばれる
        TextView gpsStatusText = findViewById(R.id.gpsStatusView);
        gpsStatusText.setText("GPS：ON");
    }

    //LocationListenerで自動生成された必要な関数
    //GPSプロバイダが利用不可能になるとコールバックされるメソッド
    @Override
    public void onProviderDisabled(String s) {
        //ロケーションプロバイダーが使われなくなったらリムーブする必要がある
        TextView gpsStatusText = findViewById(R.id.gpsStatusView);
        gpsStatusText.setText("GPS：OFF");
    }
    //ネットワークステータスのチェックに必要な関数
    public static boolean netWorkCheck(Context context){
        ConnectivityManager cm =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if( info != null ){
            return info.isConnected();
        } else {
            return false;
        }
    }

    // 終了ボタン用
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//finishAndRemoveTaskの自動生成
    public void onClickFinish(View view){
        finishAndRemoveTask();//タスク一覧から消す&アプリの終了。APIレベル21以降から使える
        finish();//アプリの終了。タスク一覧に空の画面が出っ放し。↑が実行されなかったとき用
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        //操作前のEtidTextの状態を取得する
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
        //操作中のEtidTextの状態を取得する
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //操作後のEtidTextの状態を取得する
        if(editable.toString().equals("高尾山")){
            mMap.clear();
            mClusterManager.clearItems();

            TargetLatLng = new LatLng(35.6251319,139.2435817);
            TargetMountainName = editable.toString();
            TargetMountainNamePhotoID = "takaozann";

            Toast toast = Toast.makeText(this,
                    TargetMountainName+"を目標地点に設定", Toast.LENGTH_LONG);
            toast.show();

            cameraZoom(TargetLatLng);
            mMap.addMarker(new MarkerOptions().position(TargetLatLng).title(TargetMountainName+"頂上").icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));


            LatLng[] photoLatlong = new LatLng[20];
            photoLatlong[0] = new LatLng(35.6309329,139.2557798);//takaozann1
            photoLatlong[1] = new LatLng(35.6305362,139.2554631);//takaozann2
            photoLatlong[2] = new LatLng(35.6302891,139.2554089);
            photoLatlong[3] = new LatLng(35.6301904,139.2547552);
            photoLatlong[4] = new LatLng(35.6299598,139.2534169);
            photoLatlong[5] = new LatLng(35.629133,139.2519582);
            photoLatlong[6] = new LatLng(35.6276235,139.2503081);
            photoLatlong[7] = new LatLng(35.6261898,139.2508381);
            photoLatlong[8] = new LatLng(35.6256475,139.2506619);
            photoLatlong[9] = new LatLng(35.6255583,139.2503437);
            photoLatlong[10] = new LatLng(35.6261133,139.249805);
            photoLatlong[11] = new LatLng(35.6265051,139.249813);
            photoLatlong[12] = new LatLng(35.6263014,139.2490923);
            photoLatlong[13] = new LatLng( 35.626357,139.2463414 );
            photoLatlong[14] = new LatLng(35.6257824,139.2449585);
            photoLatlong[15] = new LatLng(35.6254283,139.2441482);
            photoLatlong[16] = new LatLng(35.6248742,139.2434163);
            photoLatlong[17] = new LatLng(35.6247153,139.2432579);
            photoLatlong[18] = new LatLng(35.6250185,139.243602 );
            photoLatlong[19] = new LatLng(35.6246956,139.2432038);
            //マーカセット用
            mMap.setOnInfoWindowClickListener(mClusterManager);
            for (int i = 0; i < photoLatlong.length; i++) {
                int viewId = getResources().getIdentifier(TargetMountainNamePhotoID + (i + 1), "drawable", getPackageName());
                mClusterManager.addItem(new StringClusterItem("" + viewId , photoLatlong[i]));
            }
            mClusterManager.cluster();
        }else{
            TargetLatLng = new LatLng(0,0);
            TargetMountainName = "null";
            TargetMountainNamePhotoID = "null";
            mMap.clear();
            mClusterManager.clearItems();
        }
    }
    public void onClickGoogleWalk(View view) {
        if(TargetMountainName.equals("null")) {
            Toast toast = Toast.makeText(this,
                    "登る山の名前を入力してください", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            String start = "現在地";
            String destination = TargetMountainName;

            // 電車:r
            //String dir = "r";
            // 車:d
            //String dir = "d";
            // 歩き:w
            String dir = "w";

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + start + "&daddr=" + destination + "&dirflg=" + dir));
            startActivity(intent);
        }
    }
    public void onClickGoogleTrain(View view) {
        if(TargetMountainName.equals("null")) {
            Toast toast = Toast.makeText(this,
                    "登る山の名前を入力してください", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            String start = "現在地";
            String destination = TargetMountainName;

            // 電車:r
            String dir = "r";
            // 車:d
            //String dir = "d";
            // 歩き:w
            //String dir = "w";

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            intent.setData(Uri.parse("http://maps.google.com/maps?saddr=" + start + "&daddr=" + destination + "&dirflg=" + dir));
            startActivity(intent);
        }
    }
    public void onClickTargetMountain(View view) {
        if(TargetMountainName.equals("null")) {
            Toast toast = Toast.makeText(this,
                    "登る山の名前を入力してください", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            cameraZoom(TargetLatLng);
        }
    }
}