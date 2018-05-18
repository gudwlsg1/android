package com.example.wjsur0329.seeker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wjsur0329.seeker.Helper.InventoryHelper;
import com.example.wjsur0329.seeker.data.DataBean;
import com.example.wjsur0329.seeker.data.DataInterface;
import com.example.wjsur0329.seeker.data.DataManager;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;



public class ItemPushActivity extends AppCompatActivity implements DataInterface{
    String imageurl=null;
    private DataBean item;
    Vibrator vibrator;
    Handler handlers = new Handler();
    public void onClick(View view){
        DataBean b = new DataBean();
        b.setItemId(item.getItemId());
        DataManager.getInstance(this).updateItem(b);
    }
    private MediaPlayer effect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_push);



        Intent intent = getIntent();
        String itemName = intent.getStringExtra("itemName");
        String itemDesc = intent.getStringExtra("itemDesc");
        imageurl = intent.getStringExtra("imageUrl");
        imageurl = MainActivity.ServerIP+imageurl;
        int itemId = intent.getIntExtra("itemId", 0);
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);

        item = new DataBean();
        item.setItemId(itemId);
        item.setItemName(itemName);
        item.setImageUrl(imageurl);
        item.setItemDesc(itemDesc);
        item.setLat(lat);
        item.setLng(lng);

        effect=MediaPlayer.create(this,R.raw.effect);
        if(MainActivity.getEffectSound()){
            effect.start();
        }

        TextView itemTitles = (TextView) findViewById(R.id.itemTitle);
        TextView itemDescs = (TextView) findViewById(R.id.itemName);
//http://10.80.162.89:8080/project/upload/class7.PNG
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE); //진동 매니저
        vibrator.vibrate(800);
        itemTitles.setText(itemName);
        itemDescs.setText(itemDesc);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {    // 오래 거릴 작업을 구현한다
                // TODO Auto-generated method stub
                try{
                    // 걍 외우는게 좋다 -_-;
                    final ImageView iv = (ImageView) findViewById(R.id.itemImage);
                    URL url = new URL(imageurl);
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    handlers.post(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            iv.setImageBitmap(bm);
                        }
                    });

                    iv.setImageBitmap(bm); //비트맵 객체로 보여주기
                    is.close();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }
        });


        Button btn_finish = (Button)findViewById(R.id.outButton);
        btn_finish.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                effect.stop();
                finish();
            }
        });
        t.start();
    }


    @Override
    public void onDataReceived(DataBean[] items) {

    }

    @Override
    public void onUpdateResult(int result) {
        if(result == 1){
            Toast.makeText(this, "획득", Toast.LENGTH_SHORT).show();
            InventoryHelper helper = new InventoryHelper(this, "inventory", null, 1);
            helper.insert(item);
        } else {
            Toast.makeText(this, "앗! 획득실패. 한 발 늦었음", Toast.LENGTH_SHORT).show();
        }
        effect.stop();
        finish();
    }


}
