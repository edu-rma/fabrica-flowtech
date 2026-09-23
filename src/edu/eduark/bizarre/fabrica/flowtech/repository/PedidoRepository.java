
package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PedidoRepository {
    
public int contarPedidosEnProduccion() {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = 'EN_PRODUCCION'";
        try (Connection conexion = DataBaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {
            return resultado.next() ? resultado.getInt(1) : 0;
        } catch (SQLException error) {
            System.err.println("Error al contar pedidos: " + error.getMessage());
            return 0;
        }
    }
}