package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {

    public List<ProductoOpcion> listarTodos() throws SQLException {
        String sql = "SELECT id_producto, codigo_sku, nombre, precio_venta FROM productos ORDER BY nombre";
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
}
