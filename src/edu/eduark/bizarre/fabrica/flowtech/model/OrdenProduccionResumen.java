package edu.eduark.bizarre.fabrica.flowtech.model;

/** Fila de "ordenes_produccion" asignada a un empleado. */
public record OrdenProduccionResumen(int idOrden, int idPedido, String producto, int cantidad,
        String fechaInicio, String estado) {
}
