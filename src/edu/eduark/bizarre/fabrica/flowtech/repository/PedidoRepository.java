package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Único punto de acceso SQL para los pedidos del cliente. No altera el
 * esquema: solamente utiliza las tablas pedidos, detalle_pedido, productos,
 * usuarios y roles que ya existen en la base de datos.
 */
public class PedidoRepository {

    private static final String BUSCAR_EMPLEADO = """
            SELECT u.id_usuario
            FROM usuarios u
            INNER JOIN roles r ON r.id_rol = u.id_rol
            WHERE r.nombre = 'EMPLEADO' AND u.activo = TRUE
            ORDER BY u.id_usuario
            LIMIT 1
            """;
    private static final String INSERTAR_PEDIDO =
            "INSERT INTO pedidos (id_usuario_cliente, id_usuario_empleado, estado) VALUES (?, ?, 'PENDIENTE')";
    private static final String INSERTAR_DETALLE = """
            INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario)
            SELECT ?, p.id_producto, ?, p.precio_venta
            FROM productos p
            WHERE p.id_producto = ?
            """;
    private static final String OBTENER_TOTAL =
            "SELECT COALESCE(SUM(subtotal), 0) AS total FROM detalle_pedido WHERE id_pedido = ?";

    public Pedido crearPedido(int idCliente, Map<Producto, Integer> carrito) throws SQLException {
        if (idCliente <= 0 || carrito == null || carrito.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener un cliente y al menos un producto.");
        }

        try (Connection conexion = DataBaseConnection.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                int idEmpleado = buscarEmpleadoDisponible(conexion);
                int idPedido = insertarPedido(conexion, idCliente, idEmpleado);

                for (Map.Entry<Producto, Integer> item : carrito.entrySet()) {
                    insertarDetalle(conexion, idPedido, item.getKey().id(), item.getValue());
                }

                BigDecimal total = obtenerTotal(conexion, idPedido);
                conexion.commit();
                return convertirAPedido(idPedido, carrito, total);
            } catch (SQLException | RuntimeException error) {
                conexion.rollback();
                throw error;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    private int buscarEmpleadoDisponible(Connection conexion) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement(BUSCAR_EMPLEADO);
             ResultSet resultado = consulta.executeQuery()) {
            if (!resultado.next()) {
                throw new SQLException("No hay un empleado activo disponible para asignar el pedido.");
            }
            return resultado.getInt("id_usuario");
        }
    }

    private int insertarPedido(Connection conexion, int idCliente, int idEmpleado) throws SQLException {
        try (PreparedStatement insercion = conexion.prepareStatement(INSERTAR_PEDIDO, Statement.RETURN_GENERATED_KEYS)) {
            insercion.setInt(1, idCliente);
            insercion.setInt(2, idEmpleado);
            insercion.executeUpdate();
            try (ResultSet llaves = insercion.getGeneratedKeys()) {
                if (!llaves.next()) {
                    throw new SQLException("No fue posible obtener el identificador del pedido creado.");
                }
                return llaves.getInt(1);
            }
        }
    }

    private void insertarDetalle(Connection conexion, int idPedido, int idProducto, int cantidad) throws SQLException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de cada producto debe ser mayor que cero.");
        }
        try (PreparedStatement insercion = conexion.prepareStatement(INSERTAR_DETALLE)) {
            insercion.setInt(1, idPedido);
            insercion.setInt(2, cantidad);
            insercion.setInt(3, idProducto);
            if (insercion.executeUpdate() != 1) {
                throw new SQLException("El producto " + idProducto + " no existe en la base de datos.");
            }
        }
    }

    private BigDecimal obtenerTotal(Connection conexion, int idPedido) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement(OBTENER_TOTAL)) {
            consulta.setInt(1, idPedido);
            try (ResultSet resultado = consulta.executeQuery()) {
                resultado.next();
                return resultado.getBigDecimal("total");
            }
        }
    }

    private Pedido convertirAPedido(int idPedido, Map<Producto, Integer> carrito, BigDecimal total) {
        String detalle = carrito.entrySet().stream()
                .map(item -> item.getKey().nombre() + " · Cant. " + item.getValue())
                .collect(Collectors.joining(", "));
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM uuuu"));
        return new Pedido("#FT-" + idPedido, fecha, "Pendiente", "Q" + total.setScale(2), detalle);
    }
}
