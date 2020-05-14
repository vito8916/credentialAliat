package com.vicxbox.micredencial.config;

import android.os.Environment;

import java.util.Calendar;

public class Env {

    public static final int MY_DEFAULT_TIMEOUT = 15000;

    /*DATAS*/ //
    public static final String BASEURLPATH = "https://pagos.aliat.edu.mx/autoservicio/webservicenew/webservicecredencial.php";
    public static final String DATA1 = "Ggjaah7q4eUblT2xuQJI8q4Y9xW4IVLQdBFWVw8KK0Y%3D";
    public static final String DATA4 = "pNrbnwnt40Ze5%2FumFea7Bw%3D%3D%data5=";
    public static final String DATA5 = "";
    public static final String DATA8 = "";
    public static final String DATA13 = "";
    public static final String DATA14 = "";
    public static final String DATA15 = "";
    public static final String DATA16 = "";

    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "pref_credential";
    public static final String SHARED_PREF_CREATEDAT = "pref_created_at";
    //****
    public static final String LOCAL_STORED_DATA = "localdata_exist";
    public static final String LOCAL_CREDENTIAL_STATUS = "solicitud_fisica";
    public static final String LOCAL_SOLICITUD_CREATED = "solicitud_fuecreada";


    public static final String LOCAL_FOTO_PATH = "complete_path_photo";
    public static final String LOCAL_FOTO_NAME = "complete_name_photo";
    /*json response*/
    public static final String LOCAL_jsonresponse_DATA = "jsonresponse";

    public static final String LOCAL_status_DATA = "status";
    public static final String LOCAL_emplid_DATA = "emplid";
    public static final String LOCAL_clave_sep_DATA = "clave_sep";
    public static final String LOCAL_institucion_DATA = "institucion";
    public static final String LOCAL_descripcion_institucion_DATA = "descripcion_institucion";
    public static final String LOCAL_campus_DATA = "campus";
    public static final String LOCAL_descripcion_campus_DATA = "descripcion_campus";
    public static final String LOCAL_nombre_alumno_DATA = "nombre_alumno";
    public static final String LOCAL_apellido_p_alumno_DATA = "apellido_p_alumno";
    public static final String LOCAL_apellido_m_alumno_DATA = "apellido_m_alumno";
    public static final String LOCAL_nombre_completo_alumno_DATA = "nombre_completo_alumno";
    public static final String LOCAL_grado_DATA = "grado";
    public static final String LOCAL_carrera_DATA = "carrera";
    public static final String LOCAL_temporalidad_DATA = "temporalidad";
    public static final String LOCAL_fecha_inicio_DATA = "fecha_inicio";
    public static final String LOCAL_fecha_fin_DATA = "fecha_fin";
    public static final String LOCAL_prog_status_DATA = "prog_status";
    public static final String LOCAL_nivel_DATA = "nivel";
    public static final String LOCAL_adeudo_DATA = "adeudado";
    public static final String LOCAL_marca_DATA = "marca";
    public static final String LOCAL_vigencia_DATA = "vigencia";
    public static final String LOCAL_opcional_DATA = "opcional";
    public static final String LOCAL_fotourl_DATA = "fotourl";
    public static final String LOCAL_urisimple_DATA = "urisimple";
    public static final String LOCAL_condicionespecial_DATA = "condicionespecial";
    public static final String LOCAL_saldo_DATA = "saldo";
    public static final String LOCAL_alerttitle_DATA = "alerttitle";
    public static final String LOCAL_alertmessage_DATA = "alertmessage";




    //This would be used to store the path of local image, name, and extenstion
    public static final String credential_photo_path = "MyCredential";
    public static final String credential_photo_name = "photo";
    public static final String credential_photo_jpg = ".jpg";
    public static final String credential_photo_png = ".png";
    public static final String photo_name_year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    String anio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
}
