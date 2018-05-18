package com.example.wjsur0329.seeker.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wjsur0329.seeker.MainActivity;
import com.example.wjsur0329.seeker.R;
import com.example.wjsur0329.seeker.data.DataBean;
import com.example.wjsur0329.seeker.data.InventoryBean;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wjsur0329 on 2017-12-17.
 */

public class InventoryAdapter extends RecyclerView.Adapter<ItemViewHolder> {
    private ArrayList<InventoryBean> data;
    public void setData(ArrayList<InventoryBean> data){
        this.data = data;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        InventoryBean bean = data.get(position);
        holder.itemName .setText(bean.getItemName());
        Date d = new Date(bean.getItemfndtime());
        holder.itemFndTime.setText(d.toString());
        ImageView iv = holder.imageView;
        String imageurl = bean.getImageUrl();
        Log.d("test",imageurl);
        imageurl = MainActivity.ServerIP + imageurl;
        try{
            // 걍 외우는게 좋다 -_-;

            URL url = new URL(imageurl);
            InputStream is = url.openStream();
            final Bitmap bm = BitmapFactory.decodeStream(is);

            iv.setImageBitmap(bm); //비트맵 객체로 보여주기
            is.close();
        } catch(Exception e){
            e.printStackTrace();
        }



    }


    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        else
            return data.size();
    }
}
