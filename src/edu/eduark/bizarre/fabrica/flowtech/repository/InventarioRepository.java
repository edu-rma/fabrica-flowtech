
package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioRepository {
    
    public List<InventarioItem> obtenerInventario() {
        List<InventarioItem> lista = new ArrayList<>();
        String sql = "SELECT p.codigo_sku, p.nombre, ip.stock_actual, ip.stock_minimo "
                + "FROM inventario_productos ip INNER JOIN productos p ON p.id_producto = ip.id_producto "
                + "ORDER BY p.codigo_sku";

        try (Connection conexion = DataBaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                lista.add(new InventarioItem(
                    resultado.getString("codigo_sku"),
                    resultado.getString("nombre"),
                    resultado.getInt("stock_actual"),
                    resultado.getInt("stock_minimo")
                ));
            }
        } catch (SQLException error) {
            System.err.println("Error al cargar inventario: " + error.getMessage());
        }
        return lista;
    }

    public record InventarioItem(String sku, String producto, int stockActual, int stockMinimo) {}
}
    

