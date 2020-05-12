package com.vicxbox.micredencial.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CredentialModel {

    @SerializedName("estatus")
    @Expose
    private String estatus;
    @SerializedName("emplid")
    @Expose
    private String emplid;
    @SerializedName("clave_sep")
    @Expose
    private Integer claveSep;
    @SerializedName("institucion")
    @Expose
    private String institucion;
    @SerializedName("descripcion_institucion")
    @Expose
    private String descripcionInstitucion;
    @SerializedName("campus")
    @Expose
    private String campus;
    @SerializedName("descripcion_campus")
    @Expose
    private String descripcionCampus;
    @SerializedName("nombre_alumno")
    @Expose
    private String nombreAlumno;
    @SerializedName("apellido_p_alumno")
    @Expose
    private String apellidoPAlumno;
    @SerializedName("apellido_m_alumno")
    @Expose
    private String apellidoMAlumno;
    @SerializedName("nombre_completo_alumno")
    @Expose
    private String nombreCompletoAlumno;
    @SerializedName("grado")
    @Expose
    private String grado;
    @SerializedName("carrera")
    @Expose
    private String carrera;
    @SerializedName("temporalidad")
    @Expose
    private String temporalidad;
    @SerializedName("fecha_inicio")
    @Expose
    private String fechaInicio;
    @SerializedName("fecha_fin")
    @Expose
    private String fechaFin;
    @SerializedName("prog_status")
    @Expose
    private String progStatus;
    @SerializedName("nivel")
    @Expose
    private String nivel;
    @SerializedName("adeudo")
    @Expose
    private String adeudo;

    @SerializedName("foto")
    @Expose
    private String foto;

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getEmplid() {
        return emplid;
    }

    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    public Integer getClaveSep() {
        return claveSep;
    }

    public void setClaveSep(Integer claveSep) {
        this.claveSep = claveSep;
    }

    public String getInstitucion() {
        return institucion;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public String getDescripcionInstitucion() {
        return descripcionInstitucion;
    }

    public void setDescripcionInstitucion(String descripcionInstitucion) {
        this.descripcionInstitucion = descripcionInstitucion;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getDescripcionCampus() {
        return descripcionCampus;
    }

    public void setDescripcionCampus(String descripcionCampus) {
        this.descripcionCampus = descripcionCampus;
    }

    public String getNombreAlumno() {
        return nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getApellidoPAlumno() {
        return apellidoPAlumno;
    }

    public void setApellidoPAlumno(String apellidoPAlumno) {
        this.apellidoPAlumno = apellidoPAlumno;
    }

    public String getApellidoMAlumno() {
        return apellidoMAlumno;
    }

    public void setApellidoMAlumno(String apellidoMAlumno) {
        this.apellidoMAlumno = apellidoMAlumno;
    }

    public String getNombreCompletoAlumno() {
        return nombreCompletoAlumno;
    }

    public void setNombreCompletoAlumno(String nombreCompletoAlumno) {
        this.nombreCompletoAlumno = nombreCompletoAlumno;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getTemporalidad() {
        return temporalidad;
    }

    public void setTemporalidad(String temporalidad) {
        this.temporalidad = temporalidad;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getProgStatus() {
        return progStatus;
    }

    public void setProgStatus(String progStatus) {
        this.progStatus = progStatus;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getAdeudo() {
        return adeudo;
    }

    public void setAdeudo(String adeudo) {
        this.adeudo = adeudo;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

}
