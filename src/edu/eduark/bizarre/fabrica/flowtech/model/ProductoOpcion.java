package edu.eduark.bizarre.fabrica.flowtech.model;

import java.math.BigDecimal;

/** Opción de producto del catálogo, usada en combos de inventario y pedidos. */
public record ProductoOpcion(int id, String sku, String nombre, BigDecimal precio) {
    @Override
    public String toString() {
        return sku + " — " + nombre;
    }
}
