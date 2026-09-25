package edu.eduark.bizarre.fabrica.flowtech.model;

import java.math.BigDecimal;

/** Fila de "pedidos" tal como se muestra en las tablas de administrador y empleado. */
public record PedidoResumen(int idPedido, String cliente, String empleado, String estado,
        String fecha, BigDecimal total) {
}
