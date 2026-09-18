
package edu.eduark.bizarre.fabrica.flowtech.model;

import java.sql.Timestamp;

public class Usuario {
    
    private int idUsuario;
    private String nombre;
    private String usuario;
    private String clave;
    private String rol;
    private String estado;
    private Timestamp fechaCreacion;

 
    public Usuario() {
    }


    public Usuario(String nombre, String usuario, String clave, String rol, String estado) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
        this.estado = estado;
    }


    public Usuario(int idUsuario, String nombre, String usuario, String clave, String rol, String estado, Timestamp fechaCreacion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.clave = clave;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }


    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Override
    public String toString() {
        return this.nombre + " (" + this.usuario + ")";
    }
}
    
