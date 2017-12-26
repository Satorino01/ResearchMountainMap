package com.example.kobayashi_satoru.mountainmap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ReverseGeocode {
    //座標を住所のStringへ変換
    public String point2address(Context context,double latitude, double longitude) throws IOException{
        String string = new String();
        //geocoedrの実体化
        Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
        List<Address> list_address = geocoder.getFromLocation(latitude, longitude, 5);	//引数末尾は返す検索結果数
        //ジオコーディングに成功したらStringへ
        if (!list_address.isEmpty()){
            Address address = list_address.get(0);
            StringBuffer strbuf = new StringBuffer();
            //adressをStringへ
            String buf;
            for (int i = 0; (buf = address.getAddressLine(i)) != null; i++){
                strbuf.append(buf);
            }
            string = strbuf.toString();
        }
        //失敗（Listが空だったら）
        else {
            string = "現在地が特定できませんでした。";
        }
        return string;
    }
    /*
    public class ReverseGeocode {
        public String point2address(Context context, double latitude, double longitude) throws IOException {
            String string = new String();
            Geocoder geocoder = new Geocoder(context, Locale.JAPAN);
            List<Address> list_address = geocoder.getFromLocation(latitude, longitude, 5);
            if (!list_address.isEmpty()) {
                string = list_address.get(0).getAddressLine(0);
            } else {
                string = "現在地が特定できませんでした。";
            }
            return string;
        }
}
*/

}