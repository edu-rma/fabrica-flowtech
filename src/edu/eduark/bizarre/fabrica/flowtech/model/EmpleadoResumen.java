package edu.eduark.bizarre.fabrica.flowtech.model;

/** Fila de usuario con rol EMPLEADO para el listado administrativo. */
public record EmpleadoResumen(int idUsuario, String nombre, String email, boolean activo, String fechaRegistro) {
}
