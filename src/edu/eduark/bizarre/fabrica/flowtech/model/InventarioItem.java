package edu.eduark.bizarre.fabrica.flowtech.model;

/** Fila de inventario de producto terminado (solo lectura para el rol EMPLEADO). */
public record InventarioItem(int idInvProducto, int idProducto, String sku, String producto,
        int stockActual, int stockMinimo) {

    public String estado() {
        return stockActual <= stockMinimo ? "REABASTECER" : "DISPONIBLE";
    }
}
