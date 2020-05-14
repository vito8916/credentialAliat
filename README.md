# Mi credencial - Android

Documentación de código fuente para el módulo Mi credencial de Aliat

---

## Guía de instalación e integración

Use this guide to  integrate Mi credencial in your native Android app. 

# 1. Acerca de Mi credencial - Android

Mi credencial es una aplicación que comprende 3 módulos o actividades principales para la generación de una credencial digital y física para estudiantes de  Aliat Universidades.

La aplicación se conecta a los servicios web de Aliat por medio del protocolo Http para realizar las distintas consultas para validar, obtner y enviar datos desde el cliente.

Esta aplicación está ecrita en Java para Android nativo utilizando *targetCompatibility JavaVersion.VERSION_1_8 , para aprovechar las últimas características del lenguaje y se utilizan algunas librerías de terceros para facilitar el desarrollo y mejorar el funcionanmiento.*

# 2.  Estructura del proyecto

```
micredencial/                            contiene todos los archivos requeridos para Mi credencial
  config/                                  contiene las variables de entorno globales de la aplicación
    Env.java
  fragments/                               contiene todos los fragmentos
    DialogSolicitudtFragment
  utils/                                   contiene todas las clases de utilidades
    NetworkStateReceiver
  MyCredential.java                        Activity donde se genera una credencial digital y se solicita una en fisico
  VerifyPhotoActivity.java                 Activity donde se verifica si existe una poto y el año de vigencia de esta. 
MainActivity.java                        actividad principal de la aplicación que es lanzada una vez se inicia el app. esta actividad es una especie de placeholder para seguir un flujo y acceder al resto de las actividades
```

# 3. Requerimientos

El requerimiento mínimo para este proyecto es el SDK 21 de Android

### Librerías adicionales

```bash
implementation "androidx.cardview:cardview:1.0.0" //native card view android lib
implementation 'com.google.android:flexbox:0.3.2' //Google flexbox lib provee funciones similares a Flexbox Css
implementation 'com.intuit.sdp:sdp-android:1.0.6'   //to make dp and sp dimens are responsive
implementation 'com.github.bumptech.glide:glide:4.9.0' //image proccess android lib
annotationProcessor 'com.github.bumptech.glide:compiler:4.9.0' //image proccess android lib

implementation("com.squareup.okhttp3:okhttp:4.5.0") //Lib for http request

implementation 'com.google.android.material:material:1.1.0' //material.io android components
implementation 'com.github.dhaval2404:imagepicker:1.7.1'    //Lib to take photo. This lib can be removed when the FaceDetect module is integrated in the application
implementation 'net.danlew:android.joda:2.10.1.2'
```

# 4. Instalación

- Vía GitHub ejecuta el siguiente comando en tu consola:

```bash
git clone https://github.com/vito8916/credentialAliat.git
```
