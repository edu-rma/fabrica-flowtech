package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.InventarioItem;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class InventarioRepository {

    public List<InventarioItem> listarTodo() throws SQLException {
        String sql = "SELECT ip.id_inv_producto, p.id_producto, p.codigo_sku, p.nombre, ip.stock_actual, ip.stock_minimo "
                + "FROM inventario_productos ip INNER JOIN productos p ON p.id_producto = ip.id_producto "
                + "ORDER BY p.codigo_sku";
        List<InventarioItem> inventario = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                inventario.add(new InventarioItem(resultado.getInt("id_inv_producto"), resultado.getInt("id_producto"),
                        resultado.getString("codigo_sku"), resultado.getString("nombre"),
                        resultado.getInt("stock_actual"), resultado.getInt("stock_minimo")));
            }
        }
        return inventario;
    }

    public List<ProductoOpcion> productosSinInventario() throws SQLException {
        String sql = "SELECT p.id_producto, p.codigo_sku, p.nombre, p.precio_venta FROM productos p "
                + "LEFT JOIN inventario_productos ip ON ip.id_producto = p.id_producto "
                + "WHERE ip.id_producto IS NULL ORDER BY p.nombre";
        List<ProductoOpcion> productos = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                productos.add(new ProductoOpcion(resultado.getInt("id_producto"), resultado.getString("codigo_sku"),
                        resultado.getString("nombre"), resultado.getBigDecimal("precio_venta")));
            }
        }
        return productos;
    }

    public void agregar(int idProducto, int stockActual, int stockMinimo) throws SQLException {
        String sql = "INSERT INTO inventario_productos (id_producto, stock_actual, stock_minimo) VALUES (?, ?, ?)";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idProducto);
            sentencia.setInt(2, stockActual);
            sentencia.setInt(3, stockMinimo);
            sentencia.executeUpdate();
        }
    }

    public void actualizar(int idInvProducto, int stockActual, int stockMinimo) throws SQLException {
        String sql = "UPDATE inventario_productos SET stock_actual = ?, stock_minimo = ? WHERE id_inv_producto = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, stockActual);
            sentencia.setInt(2, stockMinimo);
            sentencia.setInt(3, idInvProducto);
            sentencia.executeUpdate();
        }
    }

    public void eliminar(int idInvProducto) throws SQLException {
        String sql = "DELETE FROM inventario_productos WHERE id_inv_producto = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idInvProducto);
            sentencia.executeUpdate();
        }
    }
}
