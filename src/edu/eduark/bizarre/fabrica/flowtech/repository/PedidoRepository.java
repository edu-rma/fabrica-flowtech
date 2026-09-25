package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.LineaPedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.PedidoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;


public class PedidoRepository {

    private static final String SELECT_BASE = "SELECT p.id_pedido, "
            + "TRIM(CONCAT(c.nombre, ' ', COALESCE(c.apellido, ''))) AS cliente, "
            + "TRIM(CONCAT(e.nombre, ' ', COALESCE(e.apellido, ''))) AS empleado, "
            + "DATE_FORMAT(p.fecha_pedido, '%d/%m/%Y %H:%i') AS fecha, "
            + "p.estado, "
            + "COALESCE((SELECT SUM(dp.subtotal) FROM detalle_pedido dp WHERE dp.id_pedido = p.id_pedido), 0) AS total "
            + "FROM pedidos p "
            + "INNER JOIN usuarios c ON c.id_usuario = p.id_usuario_cliente "
            + "INNER JOIN usuarios e ON e.id_usuario = p.id_usuario_empleado ";

    public List<PedidoResumen> listarTodos() throws SQLException {
        String sql = SELECT_BASE + "ORDER BY p.fecha_pedido DESC";
        List<PedidoResumen> pedidos = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                pedidos.add(mapear(resultado));
            }
        }
        return pedidos;
    }

   
    public List<PedidoResumen> listarPorEmpleado(int idUsuarioEmpleado) throws SQLException {
        String sql = SELECT_BASE + "WHERE p.id_usuario_empleado = ? ORDER BY p.fecha_pedido DESC";
        List<PedidoResumen> pedidos = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuarioEmpleado);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    pedidos.add(mapear(resultado));
                }
            }
        }
        return pedidos;
    }


    public List<Pedido> listarPorCliente(int idUsuarioCliente) throws SQLException {
        String sql = "SELECT p.id_pedido, p.fecha_pedido, p.estado, "
                + "COALESCE(SUM(dp.subtotal), 0) AS total, "
                + "GROUP_CONCAT(CONCAT(pr.nombre, ' \u00b7 Cant. ', dp.cantidad) SEPARATOR ', ') AS detalle "
                + "FROM pedidos p "
                + "LEFT JOIN detalle_pedido dp ON dp.id_pedido = p.id_pedido "
                + "LEFT JOIN productos pr ON pr.id_producto = dp.id_producto "
                + "WHERE p.id_usuario_cliente = ? "
                + "GROUP BY p.id_pedido, p.fecha_pedido, p.estado "
                + "ORDER BY p.fecha_pedido DESC";

        List<Pedido> pedidos = new ArrayList<>();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es", "ES"));

        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuarioCliente);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    String numero = "#FT-" + resultado.getInt("id_pedido");
                    String fecha = resultado.getTimestamp("fecha_pedido").toLocalDateTime().toLocalDate()
                            .format(formatoFecha);
                    String estado = resultado.getString("estado");
                    String total = "Q" + resultado.getBigDecimal("total").setScale(2);
                    String detalle = resultado.getString("detalle");
                    pedidos.add(new Pedido(numero, fecha, estado, total,
                            detalle == null ? "Sin productos" : detalle));
                }
            }
        }
        return pedidos;
    }

    public int contarPorEstado(String estado) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, estado);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? resultado.getInt(1) : 0;
            }
        }
    }

    public int crear(int idCliente, int idEmpleado, List<LineaPedido> lineas) throws SQLException {
        String insertarPedido = "INSERT INTO pedidos (id_usuario_cliente, id_usuario_empleado, estado) VALUES (?, ?, 'PENDIENTE')";
        String insertarDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conexion = DataBaseConnection.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                int idPedido;
                try (PreparedStatement sentencia = conexion.prepareStatement(insertarPedido, Statement.RETURN_GENERATED_KEYS)) {
                    sentencia.setInt(1, idCliente);
                    sentencia.setInt(2, idEmpleado);
                    sentencia.executeUpdate();
                    try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                        if (!llaves.next()) {
                            throw new SQLException("No fue posible obtener el identificador del pedido.");
                        }
                        idPedido = llaves.getInt(1);
                    }
                }

                try (PreparedStatement sentencia = conexion.prepareStatement(insertarDetalle)) {
                    for (LineaPedido linea : lineas) {
                        sentencia.setInt(1, idPedido);
                        sentencia.setInt(2, linea.producto().id());
                        sentencia.setInt(3, linea.cantidad());
                        sentencia.setBigDecimal(4, linea.producto().precio());
                        sentencia.addBatch();
                    }
                    sentencia.executeBatch();
                }

                conexion.commit();
                return idPedido;
            } catch (SQLException error) {
                conexion.rollback();
                throw error;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }


    public Pedido crearPedido(int idUsuarioCliente, Map<Producto, Integer> carrito) throws SQLException {
        String buscarEmpleadoDisponible = "SELECT u.id_usuario "
                + "FROM usuarios u "
                + "LEFT JOIN pedidos p ON p.id_usuario_empleado = u.id_usuario "
                + "AND p.estado IN ('PENDIENTE', 'EN_PRODUCCION') "
                + "WHERE u.id_rol = 2 AND u.activo = TRUE "
                + "GROUP BY u.id_usuario "
                + "ORDER BY COUNT(p.id_pedido), u.id_usuario LIMIT 1";
        String insertarPedido = "INSERT INTO pedidos (id_usuario_cliente, id_usuario_empleado, estado) "
                + "VALUES (?, ?, 'PENDIENTE')";
        String insertarDetalle = "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";

        try (Connection conexion = DataBaseConnection.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                int idEmpleado;
                try (PreparedStatement sentencia = conexion.prepareStatement(buscarEmpleadoDisponible);
                        ResultSet resultado = sentencia.executeQuery()) {
                    if (!resultado.next()) {
                        throw new SQLException("No hay empleados activos disponibles para asignar el pedido.");
                    }
                    idEmpleado = resultado.getInt("id_usuario");
                }

                int idPedido;
                try (PreparedStatement sentencia = conexion.prepareStatement(insertarPedido, Statement.RETURN_GENERATED_KEYS)) {
                    sentencia.setInt(1, idUsuarioCliente);
                    sentencia.setInt(2, idEmpleado);
                    sentencia.executeUpdate();
                    try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                        if (!llaves.next()) {
                            throw new SQLException("No fue posible obtener el identificador del pedido.");
                        }
                        idPedido = llaves.getInt(1);
                    }
                }

                try (PreparedStatement sentencia = conexion.prepareStatement(insertarDetalle)) {
                    for (Map.Entry<Producto, Integer> entrada : carrito.entrySet()) {
                        sentencia.setInt(1, idPedido);
                        sentencia.setInt(2, entrada.getKey().id());
                        sentencia.setInt(3, entrada.getValue());
                        sentencia.setBigDecimal(4, entrada.getKey().precio());
                        sentencia.addBatch();
                    }
                    sentencia.executeBatch();
                }

                conexion.commit();

                String detalle = carrito.entrySet().stream()
                        .map(e -> e.getKey().nombre() + " \u00b7 Cant. " + e.getValue())
                        .collect(Collectors.joining(", "));
                String fecha = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es", "ES")));

                BigDecimal total = carrito.entrySet().stream()
                        .map(entrada -> entrada.getKey().precio()
                                .multiply(BigDecimal.valueOf(entrada.getValue())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return new Pedido("#FT-" + idPedido, fecha, "PENDIENTE", "Q" + total.setScale(2), detalle);
            } catch (SQLException error) {
                conexion.rollback();
                throw error;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    public void actualizarEstadoYEmpleado(int idPedido, String estado, int idEmpleado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ?, id_usuario_empleado = ? WHERE id_pedido = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idEmpleado);
            sentencia.setInt(3, idPedido);
            sentencia.executeUpdate();
        }
    }

   
    public void actualizarEstado(int idPedido, String estado) throws SQLException {
        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, estado);
            sentencia.setInt(2, idPedido);
            sentencia.executeUpdate();
        }
    }

    public void eliminar(int idPedido) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id_pedido = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idPedido);
            sentencia.executeUpdate();
        }
    }

    private PedidoResumen mapear(ResultSet resultado) throws SQLException {
        return new PedidoResumen(resultado.getInt("id_pedido"), resultado.getString("cliente"),
                resultado.getString("empleado"), resultado.getString("estado"), resultado.getString("fecha"),
                resultado.getBigDecimal("total"));
    }
}
