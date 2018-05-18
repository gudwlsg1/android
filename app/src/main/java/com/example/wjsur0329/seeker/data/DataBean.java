package com.example.wjsur0329.seeker.data;

/**
 * Created by wjsur0329 on 2017-12-16.
 */

public class DataBean {
    private int itemId; // 보물아이템 번호
    private int itemCnt; // 보물아이템 개수
    private String itemName; // 보물아이템이름
    private String itemDesc; //아이템 설명
    private String imageUrl; // 이미지
    private String shadowUrl;// 그림자 이미지
    private double lat; //위도
    private double lng; //경도
    private boolean hidden; //숨길건지 보여줄건지
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemCnt() {
        return itemCnt;
    }

    public void setItemCnt(int itemCnt) {
        this.itemCnt = itemCnt;
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

    public String getShadowUrl() {
        return shadowUrl;
    }

    public void setShadowUrl(String shadowUrl) {
        this.shadowUrl = shadowUrl;
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

    public boolean getHidden(){ return hidden; }

    public void setHidden(boolean hidden){ this.hidden = hidden; }
}
