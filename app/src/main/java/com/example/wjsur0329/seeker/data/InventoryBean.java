package com.example.wjsur0329.seeker.data;

import java.util.Date;

/**
 * Created by wjsur0329 on 2017-12-17.
 */

public class InventoryBean {

    private int itemId; // 보물아이템 번호
    private long itemfndtime; //찾은 시간
    private String itemName; // 보물아이템이름
    private String itemDesc; //아이템 설명
    private String imageUrl; // 이미지
    private double lat; //위도
    private double lng; //경도

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public long getItemfndtime() {
        return itemfndtime;
    }

    public void setItemfndtime(long itemfndtime) {
        this.itemfndtime = itemfndtime;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

}
