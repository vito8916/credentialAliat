package com.vicxbox.micredencial.utils;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.vicxbox.micredencial.config.Env;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Utils {

    private String responseUpload;

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        Log.d("SIXE()", "" +  byteArrayOutputStream.size());


        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }



}
