package com.vicxbox.micredencial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vicxbox.micredencial.config.Env;
import com.vicxbox.micredencial.utils.ViewAnimation;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CredentialActivity extends AppCompatActivity {

    // region Constants
    private static final int REQUEST_CODE_PERMISSION = 1122;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;


    private static final int CAMERA_REQUEST = 1101;
    // endregion Constants
    LinearLayout lyt_progress;
    String url;
    NestedScrollView credentialContainer;
    private ProgressBar progress_bar;
    private FloatingActionButton fab;
    private View parent_view;
    private CompoundButton chkRequestCredential;
    Dialog dialog;

    String currentPhotoPath;
    String photoName;

    /*views of credentials*/
    ImageView imageLogo, imagePreview;;
    TextView emplid, grado, carrera, temporalidad, campus,fecha_inicio, fecha_fin, nombre_completo_alumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credential);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lyt_progress = findViewById(R.id.lyt_progress);
        credentialContainer = findViewById(R.id.nested_content);
        imagePreview = findViewById(R.id.image_preview);
        imageLogo = findViewById(R.id.image_logo);

        parent_view = findViewById(android.R.id.content);

        chkRequestCredential = findViewById(R.id.chk_request_credencial);
        progress_bar = findViewById(R.id.progress_bar);
        fab = findViewById(R.id.fab_send_image);

        /*credential view components biding*/
        emplid = findViewById(R.id.txt_emplid);
        grado = findViewById(R.id.txt_licenciatura);
        carrera = findViewById(R.id.txt_carrera);
        temporalidad = findViewById(R.id.txt_temporalidad);
        campus = findViewById(R.id.txt_campus);
        fecha_inicio = findViewById(R.id.txt_fechas);
        nombre_completo_alumno = findViewById(R.id.txt_nombre_completo);

        /*inicializa los listeners para otorgar funcionalidad extra*/
        initFuncionalityListener();


        // Instantiate the RequestQueue.
        url = Env.BASEURLPATH;
        Log.d("URLREQUEST", url);

        try {
            runGetData();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initFuncionalityListener() {

        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap image =((BitmapDrawable)imagePreview.getDrawable()).getBitmap();
                //showDialogPhotoView(image);
            }
        });

        if(chkRequestCredential.isChecked()){
            fab.show();
        } else {
            fab.hide();
        }
        chkRequestCredential.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fab.show();
                    if (fab.getVisibility() == View.VISIBLE){

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // This method will be executed once the timer is over
                                credentialContainer.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        }, 15);
                        credentialContainer.fullScroll(View.FOCUS_DOWN);
                    }
                } else {
                    fab.hide();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                sendImageAction();
            }
        });
    }

    private void runGetData() throws IOException {

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "data1=Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y%3D&" +
                "data4=pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5=&" +
                "data8=EECsUI5zwp6iJOIDh9vu2g%3D%3D&" +
                "data16=Tz1qTGx3G4T5kYfywoCe5w%3D%3D");

        Request request = new Request.Builder()
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE1::", "" + e);
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                Log.d("RESPONSE1::", "" + myResponse);
                CredentialActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);

                            Glide.with(CredentialActivity.this)
                                    .load(getResources().getDrawable(R.drawable.ic_logo_unea))

                                    .into(imageLogo);

                            String photoB64 = json.getString("foto");
                            String pureBase64Encoded = photoB64.substring(photoB64.indexOf(",")  + 1);

                            Glide.with(CredentialActivity.this)
                                    .load(json.getString("foto"))
                                    .centerCrop()
                                    .into(imagePreview);

                            nombre_completo_alumno.setText(json.getString("nombre_completo_alumno"));
                            grado.setText(json.getString("grado"));
                            carrera.setText(json.getString("carrera"));
                            temporalidad.setText(json.getString("temporalidad"));
                            emplid.setText(json.getString("emplid"));
                            photoName = json.getString("emplid") + ".jpg";

                            campus.setText(json.getString("campus"));
                            fecha_inicio.setText("Vigencia: "
                                    + json.getString("fecha_inicio")
                                    + " - "
                                    + json.getString("fecha_fin")
                            );

                            ViewAnimation.hideScale(lyt_progress);
                            ViewAnimation.fadeIn(credentialContainer);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }

    private void sendImageAction() {
        progress_bar.setVisibility(View.VISIBLE);
        fab.setAlpha(0f);

        //TODO quitar el handle post delay y colocar en su lugar el request para enviar la imagen al servidor
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progress_bar.setVisibility(View.GONE);
                fab.setAlpha(1f);
                Snackbar.make(parent_view, "Image was send success", Snackbar.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private void getCredentialData() {

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .addHeader("Cache-Control", "no-cache")
                .url("https://pagos.aliat.edu.mx/autoservicio/webservicenew/webservicecredencial.php?data1=Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y%3D&data4=pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5= ")
                .method("GET", null)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("RESPONSE1::", "" + e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody responseBody = response.body();
                Gson gson = new Gson();
                JsonElement element = gson.fromJson (responseBody.string(), JsonElement.class);
                Log.d("RESPONSE1::", "" + element);
                Log.d("debug", "REQUEST BODY LENGTH: " + response.body().contentLength());
            }
        });

    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    private Bitmap convertBase64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
    private String convertBase64ToStringUrl(String b64) {
        byte[] imageAsBytes = Base64.decode(b64, Base64.DEFAULT);

        return new String(imageAsBytes);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    /*foto*/

    /*private void showDialogPhotoView(Bitmap bitmap) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_header_photo_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        ((ImageView) dialog.findViewById(R.id.img_dlg_preview)).setImageBitmap(bitmap);

        ((Button) dialog.findViewById(R.id.btn_dlg_open_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Open Camera", Toast.LENGTH_SHORT).show();
                setupPermission();
            }
        });

        ((Button) dialog.findViewById(R.id.btn_dlg_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }*/

    /*solicita permisos requeridos para tomar foto y guardar la misma en la memoria */
    void setupPermission() {
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

    /**
     *
     * crea un intent para abrir la aplicacion de la camara del dispositivo
     *
     **/
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(cameraIntent, CAMERA_REQUEST);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = savebitmap();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.vicxbox.micredencial.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
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
            Glide.with(CredentialActivity.this)
                    .load(filePath)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(imagePreview);

            //imagePreview.setImageURI(Uri.parse(String.valueOf(data)));
            /*Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imagePreview.setImageBitmap(imageBitmap);*/

            dialog.dismiss();
        }
    }

    /*
    *
    * crea un archivo que le dice el intent donde almacenar la foto
    *
    * */
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = photoName;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /*
    * guarda una foto a partir de un bitmap
    * */
    public File savebitmap() throws IOException {

        File f = new File( getExternalFilesDir(Environment.DIRECTORY_PICTURES)  + "/" + "000777.jpg");

        /*FileOutputStream out;
        out = new FileOutputStream(f);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.flush();
        out.close();*/
        //f.createNewFile();

        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());

        //Bitmap originalImage = drawable;
        Bitmap newImage = null;
        Matrix mtx = new Matrix();
        //this will prevent mirror effect
        mtx.preScale(-1.0f, 1.0f);
        // Setting post rotate to 90 because image will be possibly in landscape
        mtx.postRotate(0.f);
        // Rotating Bitmap , create real image that we want
        newImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

        FileOutputStream out;
        out = new FileOutputStream(f);
        newImage.compress(Bitmap.CompressFormat.JPEG, 30, out);
        out.flush();
        out.close();
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = f.getAbsolutePath();
        return f;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (ActivityCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
                setupPermission();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_permission_required)
                        .setMessage(R.string.error_permission_not_granted)
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> this.finish())
                        .setPositiveButton(R.string.grant_permission, (dialog, which) -> setupPermission())
                        .create()
                        .show();
            }
        }
    }

}
