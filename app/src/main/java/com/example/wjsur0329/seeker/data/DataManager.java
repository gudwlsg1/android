package com.example.wjsur0329.seeker.data;

import android.content.Context;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.util.Log;

import com.example.wjsur0329.seeker.Helper.InventoryHelper;
import com.example.wjsur0329.seeker.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by wjsur0329 on 2017-12-16.
 */

public class DataManager {
    private static DataManager instance = new DataManager();
    private String serverAddress;
    private DataInterface handler;

    private DataManager() { }

    public static DataManager getInstance(DataInterface handler) {
        instance.handler = handler;
        return instance;
    }

    public void setServerIP(String address) {
        this.serverAddress = address;
    }

    public void getItems(){
        Log.i("DM", "Request to server");
        // server에 요청 보내고 응답온 JSON 을 DataBean Array로 생성해서 return
        try {
            URL url = new URL(MainActivity.ServerIP+"/project/select");
            new AsyncTask<URL, Void, String>() {
                @Override
                protected String doInBackground(URL... params) {
                    String result = new String();
                    if(params == null || params.length < 1) {
                        Log.i("DM", "null parameter");
                        return null;
                    }

                    try {
                        HttpURLConnection connection = (HttpURLConnection)params[0].openConnection();
                        Log.i("http", "connected");
                        connection.setRequestMethod("GET"); // get
                        connection.setDoInput(true); // 읽기모드
                        connection.setUseCaches(false);
                        connection.setDefaultUseCaches(false);

                        InputStream is = connection.getInputStream();
                        StringBuilder builder = new StringBuilder(); //문자열을 담기 위한 객체
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8")); //문자열 셋 세팅
                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line+ "\n");
                        }
                        result = builder.toString();
                        Log.i("http", "result=" + result);
                    } catch(ConnectException es){
                        es.printStackTrace();
                        result = null;
                    } catch(IOException me){
                        me.printStackTrace();
                        result = null;
                    }
                    catch(RuntimeException e){
                        e.printStackTrace();
                        result = null;
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    // DataBean 배열을 반환
                    DataBean[] result = null;
                    if(s!=null) {
                        try {
                            JSONObject reader = new JSONObject(s);
                            int length = reader.getInt("length");
                            JSONArray item = reader.getJSONArray("item");
                            result = new DataBean[length];

                            for (int i = 0; i < length; i++) {
                                result[i] = new DataBean();
                                JSONObject obj = item.getJSONObject(i);
                                result[i].setItemId(obj.getInt("itemId"));
                                result[i].setItemName(obj.getString("itemName"));
                                result[i].setItemCnt(obj.getInt("itemCnt"));
                                result[i].setItemDesc(obj.getString("itemDesc"));
                                result[i].setImageUrl(obj.getString("imageUrl"));
                                result[i].setShadowUrl(obj.getString("shadowUrl"));
                                result[i].setLat(obj.getDouble("lat"));
                                result[i].setLng(obj.getDouble("lng"));
                                if(result[i].getItemCnt()>0)
                                    result[i].setHidden(false);
                                else
                                    result[i].setHidden(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    handler.onDataReceived(result);
                }
            }.execute(url);
        }catch (MalformedURLException e) {
            handler.onDataReceived(null);
        }
    }

    public void updateItem(final DataBean item){
        //서버에 찾은 보물 갯수 줄이라는 요청을 보냄

        try {
            URL url = new URL(MainActivity.ServerIP+"/project/update");
            new AsyncTask<URL, Void, Integer>(){

                @Override
                protected Integer doInBackground(URL... params) {
                    Integer result = 0;
                    if(params == null || params.length < 1) {
                        Log.i("DM", "null parameter");
                        return null;
                    }
                    try {
                        HttpURLConnection connection = (HttpURLConnection) params[0].openConnection();
                        Log.i("http", "connected");
                        connection.setRequestMethod("POST"); // post
                        connection.setDoOutput(true); // 쓰기 모드
                        connection.setDoInput(true); // 읽기모드
                        connection.setUseCaches(false);
                        connection.setDefaultUseCaches(false);

                        OutputStream os = connection.getOutputStream();
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                        writer.write("itemId="+item.getItemId());
                        writer.flush();

                        InputStream is = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        String message = reader.readLine();

                        result = Integer.parseInt(message);
                    } catch(ConnectException es){
                    es.printStackTrace();
                        result = 0;
                    } catch(IOException me){
                        me.printStackTrace();
                        result = 0;
                    }
                        catch(RuntimeException e){
                        e.printStackTrace();
                            result = 0;
                    }
                    return result;
                }

                @Override
                protected void onPostExecute(Integer s) {
                    handler.onUpdateResult(s.intValue());
                }
            }.execute(url);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
