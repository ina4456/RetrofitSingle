package com.example.retrofitsingle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.retrofitsingle.util.AES256Util;
import com.example.retrofitsingle.util.ConnectRetrofit;
import com.example.retrofitsingle.util.RetrofitAPI;
import com.example.retrofitsingle.util.UnsafeOkHttpClient;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RetrofitAPI mRetrofitAPI;

    TextView tv;
    Button btn, btnCancel, btnCarInfo, btnDriverInfo;
    ImageView img;
    public String isSuccessful, msg, callID, callDT, version, carNum, driverName, carModel, carColor, callStatus;
    String currentDate;
    String code="";

    private Call<ResponseResult> responseResultCall;
    HashMap<String, String> input;
    ConnectRetrofit cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.InitializeView();
        this.SetListener();

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
        currentDate = dateFormat.format(mDate);

        try {
            cr = ConnectRetrofit.getInstance();
            mRetrofitAPI = cr.mRetrofit.create(RetrofitAPI.class);
            requestBoardingMethod();
        } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e ) {
            e.printStackTrace();
        }

    }


    public void InitializeView()
    {
        tv=findViewById(R.id.tv);
        btn=findViewById(R.id.btn);
        btnCancel=findViewById(R.id.btnCancel);
        btnCarInfo=findViewById(R.id.btnCarInfo);
        btnDriverInfo=findViewById(R.id.btnDriverInfo);
        img=findViewById(R.id.img);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                tv.setText("Test성공 : CallID는"+cr.callID + "입니다");
                Log.d("3.버튼클릭 성공 :"," CallID는"+ cr.callID+ "입니다");
                break;
            case R.id.btnCancel:
                try {
                    Log.d("5.탑승취소 :"," CallID : "+ cr.callID+", CallDT:"+cr.callDT);
                    cancelBoardingMethod(cr.callID, cr.callDT);
                } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                    Log.d("button2ERROR", e.getMessage());
                }
                break;
            case R.id.btnCarInfo:

                break;
            case R.id.btnDriverInfo:
                try {
                    Log.d("6.차정보조회 :"," CallID : "+ cr.callID+", CallDT:"+cr.callDT);
                    driverInfoMethod(cr.callID, cr.callDT);
                    tv.setText("차색상:"+carColor+" , 차 모델:"+carModel);
                } catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                    Log.d("button2ERROR", e.getMessage());
                }
                break;
        }
    }

    public void SetListener()
    {
        btn.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCarInfo.setOnClickListener(this);
        btnDriverInfo.setOnClickListener(this);
    }

   /* private void authHttp() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        code = AES256Util.encode(currentDate);

        input = new HashMap<>();
        input.put("currentDT", currentDate);
        input.put("authCode", code);
        input.put("mobile", AES256Util.encode("01044561472"));
        input.put("data", AES256Util.encode("28777"));

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://58.180.28.220:8000")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mRetrofitAPI = mRetrofit.create(RetrofitAPI.class);

        Log.d("보낼 데이터 : ", currentDate+" : "+code);
        authSet = mRetrofitAPI.authSetHTTP(input);
        authSet.enqueue(new Callback<auth_set>(){

            @Override
            public void onResponse(Call<auth_set> call, Response<auth_set> response) {

                auth_set authItem = response.body();
                try {
                    if(authItem.getMsg().equals("")){
                        Log.d("Test", currentDate + "입니다 : "+authItem.getIsSuccessful());
                        tv.setText("연결성공");
                    }else{
                        result=AES256Util.decode(authItem.getMsg());
                        Log.d("Test", currentDate + "입니다 : "+result+" : "+authItem.getIsSuccessful());
                        tv.setText(result);
                    }

                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                        e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<auth_set> call, Throwable t) {
                Log.d("ERROR", t.getMessage());
            }
        });



    }*/


    private void requestBoardingMethod() throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        code = AES256Util.encode(currentDate);

        input = new HashMap<>();
        input.put("currentDT", currentDate);
        input.put("authCode", code);
        input.put("mobile", AES256Util.encode("01044561472"));
        input.put("posName", AES256Util.encode("인솔라인"));           //현재 고객 위치명
        input.put("posLon", AES256Util.encode("127.110580"));            //현재 고객 위도
        input.put("posLat", AES256Util.encode("37.402278"));            //현재 고객 경도
        input.put("posNameDetail", AES256Util.encode("판교예요"));     //현재 고객 위치 상세설명
        input.put("destLon", AES256Util.encode("127.112459"));           //목적지 위도
        input.put("destLat", AES256Util.encode("37.307630"));           //목적지 경도
        input.put("destination", AES256Util.encode("우리집"));       //목적지 위치명

        responseResultCall = mRetrofitAPI.requestBoarding(input);
        cr.call(responseResultCall);


    }


    private void cancelBoardingMethod(String ID, String DT) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        code = AES256Util.encode(currentDate);

        input = new HashMap<>();
        input.put("currentDT", currentDate);
        input.put("authCode", code);
        input.put("mobile", AES256Util.encode("01044561472"));
        input.put("callID", AES256Util.encode(ID));
        input.put("callDT", AES256Util.encode(DT));

        Log.d("보낼 데이터", "ID:"+ID+", DT:"+DT);

        responseResultCall = mRetrofitAPI.cancelBoarding(input);
        cr.call(responseResultCall);
    }

    private void carInfoMethod(String ID, String DT) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        code = AES256Util.encode(currentDate);

        input = new HashMap<>();
        input.put("currentDT", currentDate);
        input.put("authCode", code);
        input.put("mobile", AES256Util.encode("01044561472"));
        input.put("callID", AES256Util.encode(ID));
        input.put("callDT", AES256Util.encode(DT));

        Log.d("보낼 데이터", "ID:"+ID+", DT:"+DT);

        responseResultCall = mRetrofitAPI.carInfo(input);
        cr.call(responseResultCall);
    }

    private void driverInfoMethod(String ID, String DT) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        code = AES256Util.encode(currentDate);

        input = new HashMap<>();
        input.put("currentDT", currentDate);
        input.put("authCode", code);
        input.put("mobile", AES256Util.encode("01044561472"));
        input.put("callID", AES256Util.encode(ID));
        input.put("callDT", AES256Util.encode(DT));

        Log.d("보낼 데이터", "ID:"+ID+", DT:"+DT);

        responseResultCall = mRetrofitAPI.driverInfo(input);
        cr.call(responseResultCall);

        carColor = cr.carColor;
        carModel = cr.carModel;
        Log.d("받아온것","차색상:"+carColor+" , 차 모델:"+carModel);
    }



}