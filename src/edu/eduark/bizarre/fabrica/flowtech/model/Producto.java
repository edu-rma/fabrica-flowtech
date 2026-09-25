package edu.eduark.bizarre.fabrica.flowtech.model;

import java.math.BigDecimal;

/** Producto del catálogo local que se muestra en el panel de cliente. */
public record Producto(int id, String nombre, String mpn, String categoria, String especificaciones,
        BigDecimal precio, boolean disponible) {
}
