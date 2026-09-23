package edu.eduark.bizarre.fabrica.flowtech.model;

/** Fila de inventario de producto terminado para el administrador. */
public class InventarioProducto {
    private final String sku;
    private final String producto;
    private final int stockActual;
    private final int stockMinimo;

    public InventarioProducto(String sku, String producto, int stockActual, int stockMinimo) {
        this.sku = sku;
        this.producto = producto;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
    }

    public String getSku() { return sku; }
    public String getProducto() { return producto; }
    public int getStockActual() { return stockActual; }
    public int getStockMinimo() { return stockMinimo; }
    public String getEstado() {
        return stockActual <= stockMinimo ? "REABASTECER" : "DISPONIBLE";
    }
}
