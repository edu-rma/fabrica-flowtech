package edu.eduark.bizarre.fabrica.flowtech.model;

import java.time.LocalDate;

/** Fila visible de una orden asignada a un empleado. */
public class OrdenProduccion {
    private final int id;
    private final int pedido;
    private final String producto;
    private final int cantidad;
    private final LocalDate fechaInicio;
    private String estado;

    public OrdenProduccion(int id, int pedido, String producto, int cantidad,
            LocalDate fechaInicio, String estado) {
        this.id = id;
        this.pedido = pedido;
        this.producto = producto;
        this.cantidad = cantidad;
        this.fechaInicio = fechaInicio;
        this.estado = estado;
    }

    public int getId() { return id; }
    public int getPedido() { return pedido; }
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
