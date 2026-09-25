package edu.eduark.bizarre.fabrica.flowtech.model;


public record UsuarioOpcion(int id, String nombre) {
    @Override
    public String toString() {
        return nombre;
    }
}
