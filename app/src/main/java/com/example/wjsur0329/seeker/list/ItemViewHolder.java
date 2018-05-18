package com.example.wjsur0329.seeker.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wjsur0329.seeker.R;

import org.w3c.dom.Text;

/**
 * Created by wjsur0329 on 2017-12-17.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView itemName;
    public TextView itemFndTime;

    public ItemViewHolder(View itemView) {
        super(itemView);

        imageView = (ImageView)itemView.findViewById(R.id.imageView);
        itemName = (TextView)itemView.findViewById(R.id.textViewItemName);
        itemFndTime = (TextView)itemView.findViewById(R.id.textViewFndTime);
    }
}
