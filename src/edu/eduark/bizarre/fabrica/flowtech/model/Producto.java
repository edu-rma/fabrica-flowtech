package edu.eduark.bizarre.fabrica.flowtech.model;

/** Producto del catálogo local que se muestra en el panel de cliente. */
public record Producto(int id, String nombre, String mpn, String categoria, String especificaciones, boolean disponible) {
}
