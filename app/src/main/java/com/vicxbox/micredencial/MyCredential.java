package com.vicxbox.micredencial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vicxbox.micredencial.config.Env;
import com.vicxbox.micredencial.fragments.DialogSolicitudtFragment;
import com.vicxbox.micredencial.utils.ViewAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyCredential extends AppCompatActivity {

    Context mcontext;

    public static final int DIALOG_QUEST_CODE = 300;

    // permission Constants
    private static final int REQUEST_CODE_PERMISSION = 1122;
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    //camera constants
    private static final int CAMERA_REQUEST = 1101;
    String url;
    String currentPhotoPath;
    String photoName;
    boolean hvePhoto = false;

    //llayouts variables
    LinearLayout lyt_progress;
    LinearLayout ly_alert_no_internet;
    LinearLayout lyCondicionEspecial;
    NestedScrollView credentialContainer;
    private View parent_view;


    /*views of credentials*/
    ImageView imageLogo, imagePreview;;
    TextView emplid, grado, carrera, temporalidad, campus,fecha_inicio, txt_enlace, nombre_completo_alumno,
    txtAdeudadoTitle, txtAdeudadoMsg;

    Button btnAdeudadoCondspecial;
    FloatingActionButton fbtnEditPhoto, fabEditarSolicitud;

    //Creating a shared preference
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_credential);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //inflate views
        parent_view = findViewById(android.R.id.content);
        mcontext = getApplicationContext();

        lyt_progress = findViewById(R.id.lyt_progress);
        ly_alert_no_internet = findViewById(R.id.ly_alert_no_internet);
        lyCondicionEspecial = findViewById(R.id.ly_condicion_especial);
        credentialContainer = findViewById(R.id.nested_content);

        /*credential view components biding*/
        imagePreview = findViewById(R.id.image_preview);
        imageLogo = findViewById(R.id.image_logo);
        emplid = findViewById(R.id.txt_emplid);
        grado = findViewById(R.id.txt_licenciatura);
        carrera = findViewById(R.id.txt_carrera);
        temporalidad = findViewById(R.id.txt_temporalidad);
        campus = findViewById(R.id.txt_campus);
        fecha_inicio = findViewById(R.id.txt_fechas);
        nombre_completo_alumno = findViewById(R.id.txt_nombre_completo);
        txt_enlace = findViewById(R.id.txt_enlace);

        txtAdeudadoTitle =  findViewById(R.id.txt_adeudado_title);
        txtAdeudadoMsg = findViewById(R.id.txt_adeudado_msg);
        btnAdeudadoCondspecial = findViewById(R.id.btn_adeudado_condspe);
        fbtnEditPhoto = findViewById(R.id.fbtn_edit_photo);
        fabEditarSolicitud = findViewById(R.id.fbtn_solicitud);

        url = Env.BASEURLPATH;

        //Creating a shared preference
        sharedPreferences = getSharedPreferences(Env.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        /*
        * Lo primero es comprobar el estado de la conexion a internet con esta pequeña función:: haveNetwork()
        * Si el dispositivo esta conectado a internet al momento en el que la actividad es creada, se llaman a las
        * 2 funciones iniiciales para obtener los datos del usuario.
        * Sino esta conectado a internet, se valida si tiene DaTa en Caché y la vigencia de la misma.
         */
        if (haveNetwork()){
            // Obtener la información de la credencial del estudiante haciendo un request POST
            try {
                getDataFromWebService();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ly_alert_no_internet.setVisibility(View.GONE);

        } else if (!haveNetwork()) {
            //desactivar los unicos dos componentes con interacción de esta actividad
            fbtnEditPhoto.setEnabled(false);
            fabEditarSolicitud.setEnabled(false);

            //muestra una especie de alerta al top de la pantalla
            ly_alert_no_internet.setVisibility(View.VISIBLE);

            Snackbar.make(parent_view, "No tienes conexión a internet", Snackbar.LENGTH_LONG).show();
            //obtenemos la confirmación si evidentemente existe data localmente
            boolean islLocalDataExist = sharedPreferences.getBoolean(Env.LOCAL_STORED_DATA, false);

            //validamos que si existe información en local
            if(islLocalDataExist) {
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                String dataCreatedAt = sharedPreferences.getString(Env.SHARED_PREF_CREATEDAT, "");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                //obtener la diferencia de dias entre la fecha actual y la fecha en la que fue creada la caché de la Data
                int dateDifference = Integer.parseInt(getDateDiff(dateFormat, dataCreatedAt, currentDate));
                if(dateDifference > 7){
                    sharedPreferences.edit().clear().commit();
                    new AlertDialog.Builder(this)
                            .setTitle("¡Atención!")
                            .setMessage("No se pudo obtener la inforamción requerida. Revisa tu conexión a internet.")
                            .setPositiveButton("Salir", (dialog, which) -> returToMainActivity())
                            .create()
                            .show();
                }

                //obtener el objeto de la Data guadada en local storage parse to JSONObject
                String localJsonData = sharedPreferences.getString(Env.LOCAL_jsonresponse_DATA, "");
                try {
                    JSONObject jsonLocal = new JSONObject(localJsonData);
                    validateSaldo(jsonLocal);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {//si obtenemos false, no existe data local.
                new AlertDialog.Builder(this)
                        .setTitle("¡Atención!")
                        .setMessage("No se pudo obtener la inforamción requerida. Revisa tu conexión a internet.")
                        .setPositiveButton("Salir", (dialog, which) -> returToMainActivity())
                        .create()
                        .show();
            }

        }

        //Inicializamos algunas funciones extras para otorgar funcionalidades requeridas a la acttividad
        initFuncionalityListener();
        initToolbar();

        Log.e("Difference: ", "minutes: " + getTimeDiff("2020-05-13 23:30:00"));

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mi Credencial");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*funcionalidades extras como algunos listeners de ciertos componentes para agregar funcionalidad a la actividad*/
    private void initFuncionalityListener() {

        /*listener para floating button de editar la foto en la credencial*/
        fbtnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtener la data local
                String localJsonData = sharedPreferences.getString(Env.LOCAL_jsonresponse_DATA, "");

                try {
                    JSONObject jsonLocal = new JSONObject(localJsonData);
                    if(jsonLocal.getBoolean("opcional")) {
                        showDialogImageFull(jsonLocal.getString("foto"));
                    } else {
                        Snackbar.make(parent_view, "No tienes permisos para editar tu foto", Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        fabEditarSolicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSolicitud();
            }
        });

    }

    /*
    * obtener la diferencia entre 2 fechas
    * recibe como parametros un formato de fecha en dd-MM-yyy, la fecha de creacion de los SharedPreference
    * y la fecha actual. 7 días es el tiempo límite para alcenar la data en caché
    */
    public String getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        String dayDifference = "";
        try {
            String CurrentDate= oldDate;
            String FinalDate= newDate;
            Date date1;
            Date date2;
            date1 = format.parse(CurrentDate);
            date2 = format.parse(FinalDate);
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            dayDifference = Long.toString(differenceDates);
           return  dayDifference;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  dayDifference;
    }

    public int getTimeDiff(String oldDate) {

        int minutos = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {

            Date oldTime = dateFormat.parse(oldDate);
            Date currentDate = new Date();

            long diff = currentDate.getTime() - oldTime.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (oldTime.before(currentDate)) {

                Log.e("Difference: ", "minutes: " + minutes);
                minutos = (int) minutes;
            }

        } catch (ParseException e) {
                e.printStackTrace();
            }

        return minutos;
    }

    /**
     * Este metodo obtiene la información desde el web service haciendo una peticion POST
     * Y pasa la Data obtenida a las SharedPreferences para llenar la vista posteriormente con esa
     * información llamando a la función getDataFromLocalStorage()
     * */
    private void getDataFromWebService() throws IOException {

        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        RequestBody formBody = new FormBody.Builder()
                .add("data1", "Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y%3D&")
                .add("data4", "pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5=")
                .add("data8", "EECsUI5zwp6iJOIDh9vu2g%3D%3D&")
                .add("data16", "Tz1qTGx3G4T5kYfywoCe5w%3D%3D")
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(Env.BASEURLPATH)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE1::", "" + e);
                call.cancel();
                showAlertDialog();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                Log.d("RESPONSE1::", "" + myResponse);
                MyCredential.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);

                            if(json.getBoolean("estatus")){
                                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                                // setDataToLocalStorage(json);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(Env.LOCAL_jsonresponse_DATA, json.toString());
                                editor.putString(Env.SHARED_PREF_CREATEDAT, currentDate);
                                editor.putBoolean(Env.LOCAL_STORED_DATA, true);
                                editor.apply();

                                validateSaldo(json);

                            } else {
                                ViewAnimation.hideScale(lyt_progress);
                                showAlertDialog();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }

    /*en esta función validamos el saldo adeudado del estudiante*/
    private void validateSaldo(JSONObject json) {
        boolean specialCond = false;

        try {
            //validar el saldo del estudiante
            if(json.getInt("saldo") > 0 ) {

                if(json.getBoolean("condicionespecial")){
                    specialCond = true;
                }
                showDialogAdeudoInfo(specialCond, json.getString("alerttitle"), json.getString("alertmessage") );
            }

            if(json.getInt("saldo") == 0) {
                showCredential();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*Muestra la credencial digital con su data cargada desde un JSONObject guardodo en caché el
    * cual es obtenido el en request de getDataFromWebService()*/
    private void showCredential() {
        ViewAnimation.fadeIn(lyt_progress);


        try {
            //obtener el objeto de la Data guadada en local storage parse to JSONObject
            String localJsonData = sharedPreferences.getString(Env.LOCAL_jsonresponse_DATA, "");
            JSONObject json = new JSONObject(localJsonData);

            if(!json.getBoolean("opcional")) {
                fbtnEditPhoto.setEnabled(false);
            }

            //Get Marca logo by name
            String logoName = "ic_logo_" + json.getString("marca").toLowerCase();
            Resources resources = mcontext.getResources();
            final int resourceId = resources.getIdentifier(logoName, "drawable",
                    mcontext.getPackageName());

            //set logo to ImageView using Glade
            Glide.with(MyCredential.this)
                    .load(resourceId)
                    .placeholder(R.drawable.ic_generic_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageLogo);

            String pathFoto;
            Bitmap bitmap = null;
            if (haveNetwork()) {
                pathFoto = json.getString("foto");
            } else {
                pathFoto = sharedPreferences.getString(Env.LOCAL_FOTO_PATH, "");
                Log.d("JUUUUM", " " + pathFoto);
            }
            ////set photo preview to foto ImageView using Glade
            Glide.with(MyCredential.this)
                    .load(pathFoto)
                    .placeholder(R.drawable.ic_generic_avatar)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imagePreview);

            //complete to binding views with local data JsonObject
            nombre_completo_alumno.setText(json.getString("nombre_completo_alumno"));
            grado.setText(json.getString("grado"));
            carrera.setText(json.getString("carrera"));
            temporalidad.setText(json.getString("temporalidad"));
            emplid.setText(json.getString("emplid"));

            campus.setText(json.getString("descripcion_campus"));
            fecha_inicio.setText("Vigencia: "
                    + json.getString("fecha_inicio")
                    + " - "
                    + json.getString("fecha_fin")
            );

            txt_enlace.setText(json.getString("urisimple").toUpperCase());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimation.fadeOut(lyt_progress);
                ViewAnimation.fadeIn(credentialContainer);
                fabEditarSolicitud.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    /*Funcion para abrir la camara del dispositivo mediante Intent*/
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

    /*FUNCIONES PARA LOS DIALOGOS*/

    /*En este dialogo mostramos un mensaje de error cuando el request falla*/
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.somthingwrongText);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.retryText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    getDataFromWebService();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Snackbar.make(parent_view, R.string.retryChargeCred, Snackbar.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton(R.string.closeText, (dialog, which) -> {
            returToMainActivity();
        });
        builder.show();
    }

    private void showDialogSolicitud() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogSolicitudtFragment newFragment = new DialogSolicitudtFragment();
        newFragment.setRequestCode(DIALOG_QUEST_CODE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        newFragment.setOnCallbackResult(new DialogSolicitudtFragment.CallbackResult() {
            @Override
            public void sendResult(int requestCode) {
                //Toast.makeText(getApplicationContext(), "Menu clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
    * Este diologo muestra una alerta cuando el valor de Adeudo es > 0
    * se maneja también la condicionEspeial para mostrar o no el boton de Ver mi credencial
    */
    public void showDialogAdeudoInfo(boolean specialCond, String alerttitle, String alertmessage){
        final Dialog dialog = new Dialog(this);
        TextView dgAdeudoTitle;
        TextView dgAdeudoSubtitle;
        Button btnDgClose, btnDgViewCredential;

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_adeudo);
        dialog.setCancelable(false);

        btnDgViewCredential = dialog.findViewById(R.id.btn_adeudado_condspe);
        btnDgClose = dialog.findViewById(R.id.dg_adeudo_close);
        dgAdeudoTitle = dialog.findViewById(R.id.txt_adeudado_title);
        dgAdeudoSubtitle = dialog.findViewById(R.id.txt_adeudado_msg);

        if(!specialCond) {
            btnDgViewCredential.setVisibility(View.GONE);
        }

        dgAdeudoTitle.setText(alerttitle);
        dgAdeudoSubtitle.setText(alertmessage);

        btnDgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                returToMainActivity();
            }
        });

        btnDgViewCredential.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCredential();
                Snackbar.make(parent_view, "Generando Credencial", Snackbar.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setAttributes(lp);


        dialog.show();
    }

    /*Dialogo para mostrar la foto y editarla*/
    private void showDialogImageFull(String foto) {
        final Dialog dialogImg = new Dialog(this);

        dialogImg.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialogImg.setContentView(R.layout.dialog_image);
        dialogImg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogImg.setCancelable(true);

        Button btnEditPhoto = dialogImg.findViewById(R.id.btn_edit_photo);
        ImageView dgImgEdit = dialogImg.findViewById(R.id.dg_photo_edit);
        View closeEditphoto = dialogImg.findViewById(R.id.ly_close_editphoto);
        Glide.with(MyCredential.this)
                .load(foto)
                .placeholder(R.drawable.ic_logo_aliatu)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(dgImgEdit);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogImg.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogImg.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogImg.getWindow().setAttributes(lp);

        closeEditphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImg.dismiss();
            }
        });

        /*Listener para llamar a la funcion necesaria para tomar la foto.*/
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImg.dismiss();
                takePicture();
            }
        });

        dialogImg.show();
    }

    /********* OTRAS FUNCIONES ***** */

    /*This function create a Intent to return to our Main Activity */
    private void returToMainActivity() {
        Intent mainactivity = new Intent(MyCredential.this, MainActivity.class);
        startActivity(mainactivity);
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
            //obtenemos el path de la foto recien tomada
            String filePath = ImagePicker.Companion.getFilePath(data);
            File f = new File(filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath); //gereramo un bitmap a partir del path

            //nuevo bitmap en mirror para selfies
            Bitmap newImage = null;
            Matrix mtx = new Matrix();
            mtx.preScale(-1.0f, 1.0f);
            mtx.postRotate(0.f);
            newImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);

            //crearemos un nuevo archivo para guardolo como jpg en /MyCredential/
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + Env.credential_photo_path);

            /*TODO cambiar String idStuden = "000000228377"; por el ID del estudiante previamente logueado en el App*/
            String idStuden = "000000228377";

            String fname = idStuden + Env.credential_photo_jpg;
            File file = new File (myDir, fname);

            //guardamos el nuevo path y nombre de la nueva imagen localmente en las prefenrencias (cache)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Env.LOCAL_FOTO_PATH, file.getAbsolutePath());
            editor.putString(Env.LOCAL_FOTO_NAME, fname);
            editor.apply();

            //si el archivo ya existe, lo eliminamos y volvemos a crear (con el proposito que solo exista una foto con ese nombre
            if (file.exists ()) file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                newImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                //llamamos a la función para subir la nueva foto al servidor padando el absolute path de la nueva foto
                sendPictureToServer(newImage, fname);

                Glide.with(MyCredential.this)
                        .load(newImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imagePreview);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //función para enviar la nueva foto al servidor... Utilizamo OkHttp
    private void sendPictureToServer(Bitmap bitmap, String namePhoto) {

        ViewAnimation.fadeIn(lyt_progress);

        //*convert bitmap to base64*/
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        //con el URL encode escapamos caracteres especiales
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        try {
            encoded = URLEncoder.encode(encoded, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //iniciamos un nuevo cliente OkHttp y extendemos los tiempos de timeouts
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        //creamos un Request Body donde pasamos los diferentes DATAS....
        /*TODO estos data deben ser reemplazados por los correspondientes al usuario previamente logueado*/
        RequestBody formBody = new FormBody.Builder()
                .add("data1", "Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y==")
                //.add("data4", "pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5=")
                .add("data8", "EECsUI5zwp6iJOIDh9vu2g==")
                .add("data13", namePhoto)
                .add("data14", encoded)
                .add("data15", String.valueOf(byteArrayOutputStream.size()))
                .add("data16", "0XIQi1Jvwc/1Er/39nyn1Q==")
                .build();
        //creamos el request
        Request request = new Request.Builder()
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url(Env.BASEURLPATH)
                .build();

        //con enqueue creamos un callbback para saber el estado del request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE_tienefoto", "" + e);
                Snackbar.make(parent_view, "Hubo un error al subir la foto", Snackbar.LENGTH_SHORT).show();
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String myResponse = response.body().string();
                Log.e("RESPONSE_foto", "" + myResponse);
                try {
                    JSONObject json = new JSONObject(myResponse);
                    if(json.getBoolean("estatus")){
                        Snackbar.make(parent_view, "La foto se actualizó correctamente.", Snackbar.LENGTH_SHORT).show();
                        ViewAnimation.fadeOut(lyt_progress);
                    } else {
                        Snackbar.make(parent_view, "Hubo un error al subir la foto", Snackbar.LENGTH_SHORT).show();
                        ViewAnimation.fadeOut(lyt_progress);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
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
            Intent mainactivity = new Intent(MyCredential.this, MainActivity.class);
            startActivity(mainactivity);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean haveNetwork(){
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }

}
