 package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.LineaPedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.PedidoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;
import edu.eduark.bizarre.fabrica.flowtech.repository.PedidoRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class PedidoService {

    public static final List<String> ESTADOS =
            List.of("PENDIENTE", "EN_PRODUCCION", "ENVIADO", "ENTREGADO", "CANCELADO");

    private final PedidoRepository pedidoRepository;

    public PedidoService() {
        this(new PedidoRepository());
    }

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<PedidoResumen> listarTodos() {
        try {
            return pedidoRepository.listarTodos();
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar los pedidos.", error);
        }
    }

    public List<PedidoResumen> listarPorEmpleado(int idUsuarioEmpleado) {
        try {
            return pedidoRepository.listarPorEmpleado(idUsuarioEmpleado);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar los pedidos asignados.", error);
        }
    }

    /** Usado por el panel de cliente: historial real de pedidos del cliente en sesión. */
    public List<Pedido> listarPorCliente(int idUsuarioCliente) {
        try {
            return pedidoRepository.listarPorCliente(idUsuarioCliente);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible cargar tus pedidos.", error);
        }
    }

    public int contarEnProceso() {
        try {
            return pedidoRepository.contarPorEstado("EN_PRODUCCION");
        } catch (SQLException error) {
            throw new ServicioException("No fue posible contar los pedidos en producción.", error);
        }
    }

    public int crear(int idCliente, int idEmpleado, List<LineaPedido> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new ServicioException("El pedido debe tener al menos un producto.");
        }
        try {
            return pedidoRepository.crear(idCliente, idEmpleado, lineas);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible crear el pedido.", error);
        }
    }

    /** Usado por el panel de cliente: crea el pedido a partir de su carrito, sin empleado ni precio aún. */
    public Pedido crearPedido(int idUsuarioCliente, Map<Producto, Integer> carrito) {
        if (carrito == null || carrito.isEmpty()) {
            throw new ServicioException("El carrito debe tener al menos un producto.");
        }
        try {
            return pedidoRepository.crearPedido(idUsuarioCliente, carrito);
        } catch (SQLException error) {
            String detalle = error.getMessage();
            throw new ServicioException(detalle == null || detalle.isBlank()
                    ? "No fue posible registrar el pedido."
                    : "No fue posible registrar el pedido: " + detalle, error);
        }
    }

    /** Usado por el administrador: puede cambiar el estado y reasignar el empleado responsable. */
    public void actualizarEstadoYEmpleado(int idPedido, String estado, int idEmpleado) {
        validarEstado(estado);
        try {
            pedidoRepository.actualizarEstadoYEmpleado(idPedido, estado, idEmpleado);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible actualizar el pedido.", error);
        }
    }

    /** Usado por el empleado: solo puede avanzar el estado de sus propios pedidos asignados. */
    public void actualizarEstado(int idPedido, String estado) {
        validarEstado(estado);
        try {
            pedidoRepository.actualizarEstado(idPedido, estado);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible actualizar el estado del pedido.", error);
        }
    }

    public void eliminar(int idPedido) {
        try {
            pedidoRepository.eliminar(idPedido);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible eliminar el pedido.", error);
        }
    }

    private void validarEstado(String estado) {
        if (estado == null || !ESTADOS.contains(estado)) {
            throw new ServicioException("Estado de pedido inválido: " + estado);
        }
    }
}
