package com.vicxbox.micredencial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.vicxbox.micredencial.config.Env;
import com.vicxbox.micredencial.utils.DialogNetworkChecker;
import com.vicxbox.micredencial.utils.NetworkStateReceiver;
import com.vicxbox.micredencial.utils.Utils;
import com.vicxbox.micredencial.utils.ViewAnimation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VerifyPhotoActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    Context context;
    private NetworkStateReceiver networkStateReceiver;      // Receiver that detects network state changes

    // permission Constants
    private static final int REQUEST_CODE_PERMISSION = 1122;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int CAMERA_REQUEST = 1101;

    ImageView imgVerify;
    Button btnOpenCamera, btnOtherPhoto, btnSendPhoto;
    LinearLayout lyt_progress;
    LinearLayout no_internet_ly;
    View lytNotPhotoContainer;
    View lytPhotoContainer;
    private View parent_view;
    String currentPhotoPath;

    //Creating a shared preference
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_photo);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        parent_view = findViewById(android.R.id.content);
        startNetworkBroadcastReceiver(this);

        imgVerify = findViewById(R.id.img_verify);
        lyt_progress = findViewById(R.id.lyt_progress);
        no_internet_ly = findViewById(R.id.no_internet_ly);
        lytNotPhotoContainer = findViewById(R.id.ly_not_photo_container);
        lytPhotoContainer = findViewById(R.id.ly_photo_container);

        btnOpenCamera = findViewById(R.id.open_camera);
        btnOtherPhoto = findViewById(R.id.btn_another_photo);
        btnSendPhoto = findViewById(R.id.btn_send_photo);

        //Creating a shared preference
        sharedPreferences = getSharedPreferences(Env.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        try {
            verifyIfPhotoExist();
        } catch (IOException e) {
            e.printStackTrace();
        }


        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btnOtherPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btnSendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(parent_view, R.string.sendPhotoToServerMsg, Snackbar.LENGTH_SHORT).show();
                Utils utils = new Utils();

                String absolutePath = sharedPreferences.getString(Env.LOCAL_FOTO_PATH, "Not Available");
                String namePhoto = sharedPreferences.getString(Env.LOCAL_FOTO_NAME, "Not Available");

                Log.d("fotio", "" + absolutePath);
                Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);

                //imgVerify.setImageBitmap(bitmap);

                ViewAnimation.fadeOut(lytPhotoContainer);
                ViewAnimation.fadeIn(lyt_progress);

                try {
                    sendPhotoToServer(bitmap, namePhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

    }

    private void verifyIfPhotoExist() throws IOException {
        String data1 = "data1=Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y%3D&",
                data4 = "data4=pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5=&",
                data8 = "data8=EECsUI5zwp6iJOIDh9vu2g%3D%3D&",
                data16_3 = "data16=XZtWgRMiLMOKjlTSQOiAig%3D%3D";

        ViewAnimation.fadeIn( lyt_progress);

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(
                mediaType,
                data1 + data4 + data8 + data16_3);

        Request request = new Request.Builder()
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(Env.BASEURLPATH)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE_tienefoto", "" + e);
                ViewAnimation.hideScale(lyt_progress);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                Log.d("RESPONSE_tienefoto", "" + myResponse);
                VerifyPhotoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);
                           if(!json.getBoolean("estatus")){
                               int actualYear = Calendar.getInstance().get(Calendar.YEAR);
                               int vigenciadefotovigente = Integer.parseInt(json.getString("vigenciadefotovigente"));

                               /*validar el año de vigencia de la foto*/
                               if(vigenciadefotovigente < actualYear){
                                   ViewAnimation.fadeIn(lytNotPhotoContainer);
                                   ViewAnimation.hideScale(lyt_progress);
                               } else {
                                   /*Llamar a esta funcion para ir a la actividad de MiCredenciañ*/
                                    goToCredentialActivity();
                               }

                           } else {
                               /*Muestra los views necesarios para tomar una foto*/
                               ViewAnimation.fadeIn(lytNotPhotoContainer);
                               ViewAnimation.hideScale(lyt_progress);
                           }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

    private void takePicture() {
        if (ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {

            ImagePicker.Companion.with(this)
                    .crop(3f, 4f)	//Crop image with 16:9 aspect ratio
                    .cameraOnly()
                    .saveDir(new File(Environment.getExternalStorageDirectory(), Env.credential_photo_path))
                    .compress(256)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(720, 720)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start(CAMERA_REQUEST);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERMISSION, READ_EXTERNAL_STORAGE,
                    WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        }
    }

    public String savebitmap(String filePath) throws IOException {

        File f = new File(filePath);
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        //Bitmap originalImage = drawable;
        Bitmap newImage = null;
        Matrix mtx = new Matrix();
        //this will prevent mirror effect
        mtx.preScale(-1.0f, 1.0f);
        // Setting post rotate to 90 because image will be possibly in landscape
        mtx.postRotate(0.f);
        // Rotating Bitmap , create real image that we want
        newImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + Env.credential_photo_path);

        /*COMPONER NOMBRE DE LA FOTO CON EL ID DEL ESTUDIANTE*/
        String idStuden = "000000228377";

        String fname = idStuden + Env.credential_photo_jpg;
        File file = new File (myDir, fname);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Env.LOCAL_FOTO_PATH, file.getAbsolutePath());
        editor.putString(Env.LOCAL_FOTO_NAME, fname);
        editor.apply();

        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            newImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getCanonicalPath();
    }

    public void sendPhotoToServer(Bitmap bitmap, String namePhoto) throws Exception{

        //*convert bitmap to base64*/
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        try {
            encoded = URLEncoder.encode(encoded, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d("SIXE()", "" +  byteArrayOutputStream.size());
        Log.d("Base64()", "" +  new String(encoded));

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("data1", "Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y==")
                //.add("data4", "pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5=")
                .add("data8", "EECsUI5zwp6iJOIDh9vu2g==")
                .add("data13", namePhoto)
                .add("data14", encoded)
                .add("data15", String.valueOf(byteArrayOutputStream.size()))
                .add("data16", "0XIQi1Jvwc/1Er/39nyn1Q==")
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(Env.BASEURLPATH)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE_tienefoto", "" + e);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.e("RESPONSE_foto", "" + myResponse);
                try {
                    JSONObject json = new JSONObject(myResponse);
                    if(json.getBoolean("estatus")){
                        goToCredentialActivity();
                    } else {
                        Snackbar.make(parent_view, "Hubo un error al subir la foto", Snackbar.LENGTH_SHORT).show();
                        ViewAnimation.fadeIn(lytPhotoContainer);
                        ViewAnimation.fadeOut(lyt_progress);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void goToCredentialActivity() {
        Intent goToMyCredential = new Intent(VerifyPhotoActivity.this, MyCredential.class);
        startActivity(goToMyCredential);
        finish();
    }


    /*
     * se obtiene el resultado de la toma de la fotografia
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.d("fotio", "" + data);
            String filePath = ImagePicker.Companion.getFilePath(data);


            imgVerify.setVisibility(View.VISIBLE);
            try {

                String foto = savebitmap(ImagePicker.Companion.getFilePath(data));
                Glide.with(VerifyPhotoActivity.this)
                        .load(foto)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imgVerify);

                lytNotPhotoContainer.setVisibility(View.GONE);
                ViewAnimation.fadeIn(lytPhotoContainer);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_permission_required)
                        .setMessage(R.string.error_permission_not_granted)
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> this.finish())
                        .setPositiveButton(R.string.grant_permission, (dialog, which) -> takePicture())
                        .create()
                        .show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent mainactivity = new Intent(VerifyPhotoActivity.this, MainActivity.class);
            startActivity(mainactivity);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterNetworkBroadcastReceiver(this);
        super.onDestroy();
    }

    @Override
    public void networkAvailable() {
        no_internet_ly.setVisibility(View.GONE);
        lyt_progress.setVisibility(View.VISIBLE);
        try {
            verifyIfPhotoExist();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkUnavailable() {
        no_internet_ly.setVisibility(View.VISIBLE);
        lytNotPhotoContainer.setVisibility(View.GONE);
        lytPhotoContainer.setVisibility(View.GONE);
        lyt_progress.setVisibility(View.GONE);
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }

    /**
     * Register the NetworkStateReceiver with your activity
     * @param currentContext
     */
    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     Unregister the NetworkStateReceiver with your activity
     * @param currentContext
     */
    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

}
