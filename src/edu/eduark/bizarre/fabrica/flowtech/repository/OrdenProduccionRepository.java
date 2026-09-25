package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.OrdenProduccionResumen;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrdenProduccionRepository {

    public List<OrdenProduccionResumen> listarTodas() throws SQLException {
        String sql = "SELECT op.id_orden_produccion, COALESCE(op.id_pedido, 0) AS id_pedido, p.nombre, "
                + "op.cantidad_a_fabricar, DATE_FORMAT(op.fecha_inicio, '%d/%m/%Y') AS fecha_inicio, op.estado "
                + "FROM ordenes_produccion op INNER JOIN productos p ON p.id_producto = op.id_producto "
                + "ORDER BY op.fecha_inicio DESC";
        List<OrdenProduccionResumen> ordenes = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                ordenes.add(new OrdenProduccionResumen(resultado.getInt("id_orden_produccion"), resultado.getInt("id_pedido"),
                        resultado.getString("nombre"), resultado.getInt("cantidad_a_fabricar"),
                        resultado.getString("fecha_inicio"), resultado.getString("estado")));
            }
        }
        return ordenes;
    }
}
