package edu.eduark.bizarre.fabrica.flowtech.model;

/** Línea de detalle (producto + cantidad) al armar un pedido nuevo. */
public record LineaPedido(ProductoOpcion producto, int cantidad) {
}
