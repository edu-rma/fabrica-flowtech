package edu.eduark.bizarre.fabrica.flowtech.model;

/** Pedido de demostración que se muestra en "Mis pedidos" del panel de cliente. */
public record Pedido(String numero, String fecha, String estado, String total, String detalle) {
}
