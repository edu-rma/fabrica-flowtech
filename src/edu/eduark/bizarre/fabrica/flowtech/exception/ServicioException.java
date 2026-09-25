package edu.eduark.bizarre.fabrica.flowtech.exception;

public class ServicioException extends RuntimeException {

    public ServicioException(String mensaje) {
        super(mensaje);
    }

    public ServicioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
