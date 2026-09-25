package edu.eduark.bizarre.fabrica.flowtech.model;

import java.sql.Timestamp;


public class TarjetaCredito {

    private int idTarjeta;
    private int idUsuario;
    private String titular;
    private String tipoTarjeta;
    private String ultimos4Digitos;
    private String tokenPago;
    private int mesExpiracion;
    private int anioExpiracion;
    private boolean predeterminada;
    private boolean activa;
    private Timestamp fechaRegistro;

    public TarjetaCredito() {
    }

    public TarjetaCredito(int idTarjeta, int idUsuario, String titular, String tipoTarjeta,
            String ultimos4Digitos, String tokenPago, int mesExpiracion, int anioExpiracion,
            boolean predeterminada, boolean activa, Timestamp fechaRegistro) {
        this.idTarjeta = idTarjeta;
        this.idUsuario = idUsuario;
        this.titular = titular;
        this.tipoTarjeta = tipoTarjeta;
        this.ultimos4Digitos = ultimos4Digitos;
        this.tokenPago = tokenPago;
        this.mesExpiracion = mesExpiracion;
        this.anioExpiracion = anioExpiracion;
        this.predeterminada = predeterminada;
        this.activa = activa;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdTarjeta() { return idTarjeta; }
    public void setIdTarjeta(int idTarjeta) { this.idTarjeta = idTarjeta; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }

    public String getTipoTarjeta() { return tipoTarjeta; }
    public void setTipoTarjeta(String tipoTarjeta) { this.tipoTarjeta = tipoTarjeta; }

    public String getUltimos4Digitos() { return ultimos4Digitos; }
    public void setUltimos4Digitos(String ultimos4Digitos) { this.ultimos4Digitos = ultimos4Digitos; }

    public String getTokenPago() { return tokenPago; }
    public void setTokenPago(String tokenPago) { this.tokenPago = tokenPago; }

    public int getMesExpiracion() { return mesExpiracion; }
    public void setMesExpiracion(int mesExpiracion) { this.mesExpiracion = mesExpiracion; }

    public int getAnioExpiracion() { return anioExpiracion; }
    public void setAnioExpiracion(int anioExpiracion) { this.anioExpiracion = anioExpiracion; }

    public boolean isPredeterminada() { return predeterminada; }
    public void setPredeterminada(boolean predeterminada) { this.predeterminada = predeterminada; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public Timestamp getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Timestamp fechaRegistro) { this.fechaRegistro = fechaRegistro; }


    public String etiqueta() {
        String base = tipoTarjeta + " \u2022\u2022\u2022\u2022 " + ultimos4Digitos
                + " (vence " + String.format("%02d", mesExpiracion) + "/" + anioExpiracion + ")";
        return predeterminada ? base + " \u00b7 preferida" : base;
    }

    @Override
    public String toString() {
        return etiqueta();
    }
}
