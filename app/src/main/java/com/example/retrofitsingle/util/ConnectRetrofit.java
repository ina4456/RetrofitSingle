package com.example.retrofitsingle.util;

import android.util.Log;


import com.example.retrofitsingle.ResponseResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//https://chuumong.github.io/android/2017/01/13/Get-Started-With-Retrofit-2-HTTP-Client 싱글톤

public class ConnectRetrofit {

    static OkHttpClient okHttpClient;
    public Retrofit mRetrofit;
    public ResponseResult Item;

    public String result;
    public String callID, callDT, version, carNum, driverName, carModel, carColor, callStatus="";

    private static final ConnectRetrofit connectRetrofit = new ConnectRetrofit();

    public static ConnectRetrofit getInstance(){
        return connectRetrofit;
    }

    private ConnectRetrofit() {
        Log.d("샘플", "싱글톤성공");
        init();
    }

    public void init(){
        okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://58.180.28.220:8000")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

    }


    public ResponseResult call(Call<ResponseResult> call){
        call.enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                Item = response.body();
                try {
                    if(Item.msg.equals("")){
                        if(Item.callID !=null) callID=AES256Util.decode(Item.callID);
                        if(Item.callDT !=null) callDT=AES256Util.decode(Item.callDT);
                        if(Item.version !=null) version=AES256Util.decode(Item.version);
                        if(Item.carNum !=null) carNum=AES256Util.decode(Item.carNum);
                        if(Item.driverName !=null) driverName=AES256Util.decode(Item.driverName);
                        if(Item.carModel !=null) carModel=AES256Util.decode(Item.carModel);
                        if(Item.carColor !=null) carColor=AES256Util.decode(Item.carColor);
                        if(Item.callStatus !=null) callStatus=AES256Util.decode(Item.callStatus);

                        //Log.d("2.통신성공 :"," CallID는"+ callID + "입니다 : "+Item.isSuccessful);

                    }else{
                        result=AES256Util.decode(Item.msg);
                        Log.d("MsgERROR", result+ "입니다 : "+Item.isSuccessful);
                    }

                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                        e) {
                    Log.d("ResponseERROR", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });

        return Item;
    }




}
