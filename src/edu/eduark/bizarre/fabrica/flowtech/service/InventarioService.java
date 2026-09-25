package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.InventarioItem;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import edu.eduark.bizarre.fabrica.flowtech.repository.InventarioRepository;
import java.sql.SQLException;
import java.util.List;

public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService() {
        this(new InventarioRepository());
    }

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<InventarioItem> listarTodo() {
        try {
            return inventarioRepository.listarTodo();
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar el inventario.", error);
        }
    }

    public long contarAlertas() {
        return listarTodo().stream().filter(item -> item.stockActual() <= item.stockMinimo()).count();
    }

    public List<ProductoOpcion> productosSinInventario() {
        try {
            return inventarioRepository.productosSinInventario();
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar el catálogo de productos.", error);
        }
    }

    public void agregar(int idProducto, int stockActual, int stockMinimo) {
        validarStock(stockActual, stockMinimo);
        try {
            inventarioRepository.agregar(idProducto, stockActual, stockMinimo);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible agregar el producto al inventario.", error);
        }
    }

    public void actualizar(int idInvProducto, int stockActual, int stockMinimo) {
        validarStock(stockActual, stockMinimo);
        try {
            inventarioRepository.actualizar(idInvProducto, stockActual, stockMinimo);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible actualizar el inventario.", error);
        }
    }

    public void eliminar(int idInvProducto) {
        try {
            inventarioRepository.eliminar(idInvProducto);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible eliminar la fila de inventario.", error);
        }
    }

    private void validarStock(int stockActual, int stockMinimo) {
        if (stockActual < 0 || stockMinimo < 0) {
            throw new ServicioException("El stock no puede ser negativo.");
        }
    }
}
