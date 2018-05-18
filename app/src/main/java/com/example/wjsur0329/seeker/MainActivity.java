package com.example.wjsur0329.seeker;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.location.Location;
import android.location.LocationListener;

import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wjsur0329.seeker.Helper.InventoryHelper;
import com.example.wjsur0329.seeker.data.DataBean;
import com.example.wjsur0329.seeker.data.DataInterface;
import com.example.wjsur0329.seeker.data.DataManager;
import com.example.wjsur0329.seeker.data.InventoryBean;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity implements DataInterface, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {

    FloatingActionButton fab1, fab2, fab3;

    private MediaPlayer mp;

    private static final String LOG_TAG = "MainActivity";
    private final int LOCATION_REQUEST = 1111;

    private boolean locationof = false;
    private MapView mMapView;
    Intent lodings;

    public static String ServerIP="10.80.192.89";

    //나의 위도 경도 고도
    double mLatitude=0;  //위도
    double mLongitude=0; //경도
    LocationManager locationManager;

    DataBean[] items; //아이템 저장 공간
    double meiter=0;  //아이템과의 거리

    public final int INMEITER=30;       //아이템 찾은 반경
    public final int GPS_MILISECOND=300; //GPS업데이트 시간 1000=1초
    public final int GPS_MEITER=1;       //GPS업데이트 미터 1미터


    private static SharedPreferences sharedPref;

    private boolean endloading = true;
    SplashActivity loadings;

    public boolean sema = true;
    public Queue<Intent> queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab1 = (FloatingActionButton)findViewById(R.id.buttonInventory);
        fab2 = (FloatingActionButton)findViewById(R.id.buttonSetting);
        fab3 = (FloatingActionButton)findViewById(R.id.fab);

        fab3.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
            @Override
            public void onClick(View v){
                ToggleFab();
            }
        });

        startActivity(new Intent(this, SplashActivity.class));

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this); //설정 불러오기

        ServerIP=getIP();

        mp=MediaPlayer.create(this,R.raw.bgm);
        mp.setLooping(true);

        queue = new LinkedList<Intent>();

        //위치정보
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 정보 권한 있음.", Toast.LENGTH_LONG).show();

            initMapView();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }

        //팝업창 띄우기
        // startActivity(new Intent(this, ItemPushActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    private void ToggleFab() {
        // 버튼들이 보여지고있는 상태인 경우 숨겨줌.
        if(fab1.getVisibility() == View.VISIBLE) {
            fab1.hide();
            fab2.hide();
            fab1.animate().translationY(0);
            fab2.animate().translationY(0);
        }
        // 버튼들이 숨겨져있는 상태인 경우 위로 올라오면서 보여줌.
        else {
            // 중심이 되는 버튼의 높이 + 마진 만큼 거리를 계산.
            int dy = fab3.getHeight() + 20;
            fab1.show();
            fab2.show();
            // 계산된 거리만큼 이동하는 애니메이션을 입력.
            fab1.animate().translationY(-dy*2);
            fab2.animate().translationY(-dy);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // mapView 초기화

                initMapView();

            } else {
                Toast.makeText(this, "권한 획득에 실패했습니다. 앱을 종료합니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMapView!=null) {
            if (getSpinMap())
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); //위치, 방향에 따라 지도가 바뀜
            else
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); //위치에 따라만 지도가 바뀜

            if (getMapList() == 1)
                mMapView.setMapType(MapView.MapType.Standard);  //스탠다드 지도
            else if (getMapList() == 2)
                mMapView.setMapType(MapView.MapType.Hybrid);    //인공위성 지도
            else if (getMapList() == 3)
                mMapView.setMapType(MapView.MapType.Satellite); //인공위성 지도에서 글자 없이

            if(getBackgroundSound()){
                mp.start();
            }
            else{
                mp.pause();
            }




            ServerIP=getIP();
            Toast.makeText(this,ServerIP, Toast.LENGTH_SHORT).show();
        }
        semSignalB();

    }

    private void initMapView() {
        //다음 API 연결 구문
        mMapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);
        mMapView.setDaumMapApiKey("5a4ef9a203e32b883fc585a218948b38"); //api 연결 키
        mMapView.setCurrentLocationEventListener(this);



        if(getSpinMap())
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading); //위치, 방향에 따라 지도가 바뀜
        else
            mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading); //위치에 따라만 지도가 바뀜

        if(getMapList() == 1)
            mMapView.setMapType(MapView.MapType.Standard);  //스탠다드 지도
        else if(getMapList() == 2)
            mMapView.setMapType(MapView.MapType.Hybrid);    //인공위성 지도
        else if(getMapList() == 3)
            mMapView.setMapType(MapView.MapType.Satellite); //인공위성 지도에서 글자 없이


        DataManager.getInstance(this).getItems(); //아이템 불러오기



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); //위치 매니저

        //위치가 안켜져 있을 때 실행
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //GPS 설정화면으로 이동
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            startActivity(intent);
        }


    }

    public void onCatalog(View view) {

    }

    public void onLocation(View view){
        Toast.makeText(this, mLatitude+", "+mLongitude, Toast.LENGTH_SHORT).show();
        if(mLatitude == 0 || mLongitude ==0)
            return ;

        mMapView.moveCamera(CameraUpdateFactory.newMapPoint(MapPoint.mapPointWithGeoCoord(mLatitude,mLongitude)));
    }

    public void onInventory(View view) {
        startActivity(new Intent(this, InventoryActivity.class));
    }

    public void onSetting(View view) {
        startActivity(new Intent(this, SettingActivity.class));
    }

    @Override
    public void onUpdateResult(int result) {

    }

    @Override
    public void onDataReceived(DataBean[] items) {
        this.items = items;

        if (items == null) {
            Toast.makeText(this, "서버 ip재설정후 재시작 해보세요!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SettingActivity.class));
            return;
        } else {
            Toast.makeText(this, "데이터받아와짐"+items[0].getItemName(), Toast.LENGTH_SHORT).show();
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        InventoryHelper helper = new InventoryHelper(this, "inventory", null, 1);
        ArrayList<InventoryBean> inventoryitems = helper.getAll();

        for(int i=0;i<items.length;i++){
            for(int j=0;j<inventoryitems.size();j++){
                if(items[i].getItemId() == inventoryitems.get(j).getItemId()){
                    items[i].setHidden(true);
                    break;
                }
            }
        }

        try{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_MILISECOND, GPS_MEITER, mLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, GPS_MILISECOND, GPS_MEITER, mLocationListener);


        }catch(SecurityException ex){
        }

    }

    private final LocationListener mLocationListener = new LocationListener() { //위치가 바뀔 때 마다 불리는 리스너
        public void onLocationChanged(Location location) {
            if(endloading) {
                loadings = (SplashActivity) SplashActivity.loading;
                loadings.finish();
                endloading=true;
            }
            Log.d("mylocation",mLatitude+", "+mLongitude);
            mLatitude = location.getLatitude();   //위도
            mLongitude = location.getLongitude(); //경도

            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.


            double min=-999;
            //데이터 마다 길이 측정
            for(int i=0;i<items.length;i++){
                if(!items[i].getHidden()) {
                    meiter = calcDistance(mLatitude, mLongitude, items[i].getLat(), items[i].getLng());
                    if (meiter <= INMEITER)
                        setItemPush(i);

                    if(min == -999 || min > meiter)
                        min = meiter;

                    inputhint(min);
                }
            }


       }
        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }


    };

    public void inputhint(double m){
        TextView hint = (TextView) findViewById(R.id.hint);
        hint.setText("가장 가까운 보물까지의 거리는 "+(int)m+"m 입니다.");
    }


    public double calcDistance(double lat1, double lon1, double lat2, double lon2){ //길이측정
        double EARTH_R, Rad, radLat1, radLat2, radDist;
        double distance, ret;

        EARTH_R = 6371000.0;
        Rad = Math.PI/180;
        radLat1 = Rad * lat1;
        radLat2 = Rad * lat2;
        radDist = Rad * (lon1 - lon2);

        distance = Math.sin(radLat1) * Math.sin(radLat2);
        distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
        ret = EARTH_R * Math.acos(distance);



        return ret;
    }

    public void setItemPush(int i){

        items[i].setHidden(true);
        Intent intent = new Intent(this, ItemPushActivity.class);
        intent.putExtra("itemName", items[i].getItemName());
        intent.putExtra("itemDesc", items[i].getItemDesc());
        intent.putExtra("imageUrl", items[i].getImageUrl());
        intent.putExtra("itemId", items[i].getItemId());
        intent.putExtra("lat", items[i].getLat());
        intent.putExtra("lng", items[i].getLng());

        //semWaitB
        if(sema) {
            startActivity(intent);
            sema=false;
        }
        else{
            queue.offer(intent);
        }

    }

    public void semSignalB(){
        if(queue.isEmpty()){
            sema=true;
        }
        else{
            startActivity(queue.poll());
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        locationManager.removeUpdates(mLocationListener);
        mp.stop();
        finish();
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        mapReverseGeoCoder.toString();
        onFinishReverseGeoCoding(s);
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
        onFinishReverseGeoCoding("Fail");
    }

    private void onFinishReverseGeoCoding(String result) {
        Toast.makeText(MainActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }

    public String getIP()
    {
        String Ip = sharedPref.getString("serverIP", "10.80.162.89");
        return "http://"+Ip+":8080";
    }

    public int getMapList()
    {
        return Integer.parseInt(sharedPref.getString("mapList", "1"));
    }

    public boolean getSpinMap()
    {
        return sharedPref.getBoolean("spinMap", true);

    }

    public boolean getBackgroundSound() {return sharedPref.getBoolean("backgroundSound", true);}

    public static boolean getEffectSound() {return sharedPref.getBoolean("effectSound", true);}
}