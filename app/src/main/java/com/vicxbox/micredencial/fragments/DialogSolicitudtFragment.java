package com.vicxbox.micredencial.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.vicxbox.micredencial.MyCredential;
import com.vicxbox.micredencial.R;
import com.vicxbox.micredencial.config.Env;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.CONNECTIVITY_SERVICE;


public class DialogSolicitudtFragment extends DialogFragment {
    //Creating a shared preference
    SharedPreferences sharedPreferences;
    String statusFisicaCred;
    public CallbackResult callbackResult;
    int banderitaTimer = 0;

    Context context;

    ExtendedFloatingActionButton fabCrear, fabCancelar;
    CompoundButton chkSolicitar;
    View dglyt_progress;
    View ly_switcsolicitud;
    View dglyt_countdown;
    TextView txt_info_tocancel;
    View parentView;


    public void setOnCallbackResult(final CallbackResult callbackResult) {
        this.callbackResult = callbackResult;
    }

    private int request_code = 0;
    private View root_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root_view = inflater.inflate(R.layout.dialog_payment, container, false);
        context = root_view.getContext();
        parentView = getActivity().findViewById(android.R.id.content);

        fabCrear = root_view.findViewById(R.id.xfbtn_crear_solicitud);
        fabCancelar = root_view.findViewById(R.id.xfbtn_cancelar_solicitud);
        txt_info_tocancel = root_view.findViewById(R.id.txt_info_tocancel);
        chkSolicitar = root_view.findViewById(R.id.chk_request_credencial);
        dglyt_progress = root_view.findViewById(R.id.dglyt_progress);
        ly_switcsolicitud = root_view.findViewById(R.id.ly_switcsolicitud);
        dglyt_countdown = root_view.findViewById(R.id.dglyt_countdown);


        fabCrear.setEnabled(false);
        fabCancelar.setEnabled(false);

        //Creating a shared preference
        sharedPreferences = getActivity().getSharedPreferences(Env.SHARED_PREF_NAME,
                Context.MODE_PRIVATE);

        ((ImageButton) root_view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        chkSolicitar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    fabCrear.setEnabled(true);
                    fabCrear.setBackgroundColor(getResources().getColor(R.color.blue_500));
                } else {
                    fabCrear.setEnabled(false);
                    fabCrear.setBackgroundColor(getResources().getColor(R.color.grey_500));
                }
            }
        });


        fabCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dglyt_progress.setVisibility(View.VISIBLE);
                String title = "¡Aviso!";
                String msg = "Esta acción creará una solicitud para la creación fisica de una credential. ¿Deseas continuar?";
                sendOrCancelAlertDialog(title, msg, "send");
            }
        });
        fabCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dglyt_progress.setVisibility(View.VISIBLE);
                String title = "¡Cuidado!";
                String msg = "Estás a punto de cancelar la creación física de tu credencial. ¿Deseas continuar?";
                sendOrCancelAlertDialog(title, msg, "cancel");
            }
        });

        String fechaCreacionSolicitud = sharedPreferences.getString(Env.LOCAL_SOLICITUD_CREATED, "2020-05-13 23:30:00");

        verifyphisicalCredential();

        if (getTimeDiff(fechaCreacionSolicitud) < 5) {
            int placeholderTime = 5 - getTimeDiff(fechaCreacionSolicitud);
            showCountDownDialod(placeholderTime);
        }

        return root_view;
    }

    private void onPaymentClick() {
        if (callbackResult != null) {
            callbackResult.sendResult(request_code);
        }
        dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public void setRequestCode(int request_code) {
        this.request_code = request_code;
    }

    public interface CallbackResult {
        void sendResult(int requestCode);
    }


    /*En esta funcion hacemos un request para obtener la información acerca del estado de la solicitud
     * fisica de la credencial*/
    private void verifyphisicalCredential() {

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
                .add("data17", "3")
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://pagos.aliat.edu.mx/autoservicio/webservicenew/webservicecredencialsolicitud.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE1::", "" + e);
                call.cancel();
                //showAlertDialog();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                dglyt_progress.setAlpha(0f);

                final String myResponse = response.body().string();
                Log.d("RESPONSE1::", "" + myResponse);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);

                            switch (json.getString("solicitud")) {
                                case "1":
                                    Snackbar.make(parentView, "Ya existe una solicitud creada.",
                                            Snackbar.LENGTH_LONG).show();
                                    chkSolicitar.setChecked(true);
                                    fabCrear.setEnabled(false);

                                    ly_switcsolicitud.setVisibility(View.GONE);
                                    fabCrear.setVisibility(View.GONE);

                                    txt_info_tocancel.setVisibility(View.VISIBLE);
                                    fabCancelar.setEnabled(true);
                                    break;
                                case "2":
                                    chkSolicitar.setChecked(false);
                                    /*Snackbar.make(Objects.requireNonNull(getView()), "La solicitud de la credencial fue cancelada",
                                            Snackbar.LENGTH_LONG).show();*/

                                    chkSolicitar.setChecked(false);
                                    fabCrear.setEnabled(true);
                                    fabCancelar.setEnabled(false);
                                    fabCancelar.setVisibility(View.GONE);
                                    txt_info_tocancel.setVisibility(View.GONE);

                                    ly_switcsolicitud.setVisibility(View.VISIBLE);
                                    fabCrear.setVisibility(View.VISIBLE);

                                    if(!chkSolicitar.isChecked()){
                                        fabCrear.setEnabled(false);
                                        fabCrear.setBackgroundColor(getResources().getColor(R.color.grey_500));
                                    }

                                    break;

                                case "0":
                                    chkSolicitar.setChecked(false);
                                    Snackbar.make(parentView, "Aún no haz solicitado tu credencial",
                                            Snackbar.LENGTH_LONG).show();

                                    chkSolicitar.setChecked(false);
                                    fabCrear.setEnabled(true);
                                    fabCancelar.setEnabled(false);
                                    fabCancelar.setVisibility(View.GONE);
                                    txt_info_tocancel.setVisibility(View.GONE);

                                    ly_switcsolicitud.setVisibility(View.VISIBLE);
                                    fabCrear.setVisibility(View.VISIBLE);

                                    if(!chkSolicitar.isChecked()){
                                        fabCrear.setEnabled(false);
                                        fabCrear.setBackgroundColor(getResources().getColor(R.color.grey_500));
                                    }

                                    break;
                            }


                            // setDataToLocalStorage(json);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Env.LOCAL_CREDENTIAL_STATUS, json.getString("solicitud"));
                            editor.apply();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }


    private void showCountDownDialod(int placeholderTime) {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_wait);
        dialog.setCancelable(false);

        TextView countDownTime;
        CountDownTimer timerDown;
        countDownTime = dialog.findViewById(R.id.tx_count_down);

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onPaymentClick();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        try {
            Date now = dateFormat.parse(sharedPreferences.getString(Env.LOCAL_SOLICITUD_CREATED, ""));
            DateTime jjgg = new DateTime(now);

            Date nowS = new Date();
            DateTime jjj = new DateTime(nowS);

            new CountDownTimer(
                    ((5)*60000) - (Seconds.secondsBetween(jjgg, jjj).getSeconds()*1000),
                    1000) {

                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000);
                    int minutes = seconds / 60;
                    seconds = seconds % 60;
                    countDownTime.setText("" + String.format("%02d", minutes)
                            + ":" + String.format("%02d", seconds));
                }

                public void onFinish() {
                    dialog.dismiss();

                    Log.d("COUNTDOWN:::", "FINALIZADO");
                }
            }.start();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        /*new CountDownTimer(((placeholderTime)*60000), 1000) {
            public void onTick(long millisUntilFinished) {
                countDownTime.setText("Tiempo restante: " + millisUntilFinished / 1000);
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                countDownTime.setText("" + String.format("%02d", minutes)
                        + ":" + String.format("%02d", seconds));
                //here you can have your logic to set text to edittext
            }
            public void onFinish() {
                dialog.dismiss();
                onPaymentClick();
            }

        }.start();*/
    }

    private void sendOrCancelAlertDialog(String title, String msg, String flag) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendOrCancelCredential(flag);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /*enviar solicitud o cancelar una.
     * crearemos una funcion que sirva para ambos aspectos diferenciando por un flag que recibe cono parametro
     * enviado desde el clock listener de cada boton*/
    public void sendOrCancelCredential(String flag) {
        String data17 = "";
        if(!haveNetwork()) {
            return;
        }

        if(flag.equals("send")){
            data17 = "1"; //TODO cambiar este valor del data 17 por el valor encriptado correspondiente
        }

        if(flag.equals("cancel")){
            data17 = "2"; //TODO cambiar este valor del data 17 por el valor encriptado correspondiente
        }

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
                .add("data17", data17)
                .build();
        Request request = new Request.Builder()
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://pagos.aliat.edu.mx/autoservicio/webservicenew/webservicecredencialsolicitud.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("RESPONSE1::", "" + e);
                call.cancel();
                //showAlertDialog();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();
                Log.d("RESPONSE1::", "" + myResponse);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String currentDate =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Env.LOCAL_SOLICITUD_CREATED, currentDate.toString());
                        editor.apply();

                        try {
                            JSONObject respuesta = new JSONObject(myResponse);

                            if(respuesta.getBoolean("response")) {
                                if (flag.equals("send")){
                                    dglyt_progress.setVisibility(View.GONE);
                                    Snackbar.make(getView(), "Solicitud Creada", Snackbar.LENGTH_SHORT).show();

                                    ly_switcsolicitud.setVisibility(View.GONE);
                                    fabCrear.setVisibility(View.GONE);

                                    txt_info_tocancel.setVisibility(View.VISIBLE);
                                    fabCancelar.setEnabled(true);
                                    fabCancelar.setVisibility(View.VISIBLE);

                                }

                                if (flag.equals("cancel")){
                                    Snackbar.make(getView(), "Se ha cancelado la solicitud de tu credencial",
                                            Snackbar.LENGTH_SHORT).show();

                                    fabCancelar.setVisibility(View.GONE);
                                    txt_info_tocancel.setVisibility(View.GONE);

                                    ly_switcsolicitud.setVisibility(View.VISIBLE);
                                    chkSolicitar.setChecked(false);
                                    fabCrear.setVisibility(View.VISIBLE);
                                }

                                onPaymentClick();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

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

    public boolean haveNetwork(){
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }

}