package edu.eduark.bizarre.fabrica.flowtech.controller.cliente;

import edu.eduark.bizarre.fabrica.flowtech.controller.dashboard.BaseDashboardController;
import edu.eduark.bizarre.fabrica.flowtech.controller.cliente.ClienteCatalogoController;
import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;
import edu.eduark.bizarre.fabrica.flowtech.model.RolUsuario;
import edu.eduark.bizarre.fabrica.flowtech.service.PedidoService;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class ClienteDashboardController extends BaseDashboardController {

    private static final String RUTA_INICIO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-inicio-view.fxml";
    private static final String RUTA_CATALOGO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-catalogo-view.fxml";
    private static final String RUTA_PEDIDOS = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-pedidos-view.fxml";
    private static final String RUTA_PERFIL = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-perfil-view.fxml";

    private final Map<Producto, Integer> carrito = new LinkedHashMap<>();

    private final List<Pedido> pedidos = new ArrayList<>();

    private boolean notificarCorreo = true;
    private boolean notificarSms = false;

    private final PedidoService pedidoService = new PedidoService();
    private int idUsuarioCliente;

    public void configurarUsuario(int idUsuarioCliente, String nombreUsuario, RolUsuario rol) {
        this.idUsuarioCliente = idUsuarioCliente;
        cargarPedidos();
        super.configurarUsuario(nombreUsuario, rol);
    }

    /** Trae del historial real en BD los pedidos ya guardados de este cliente. */
    private void cargarPedidos() {
        pedidos.clear();
        try {
            pedidos.addAll(pedidoService.listarPorCliente(idUsuarioCliente));
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar tus pedidos", error);
        }
    }

    @Override
    @FXML
    protected void mostrarInicio() {
        cargarVista(RUTA_INICIO);
    }

    @FXML
    private void mostrarCatalogo() {
        cargarVista(RUTA_CATALOGO);
    }

    @FXML
    private void mostrarMisPedidos() {
        cargarVista(RUTA_PEDIDOS);
    }

    @FXML
    private void mostrarPerfil() {
        cargarVista(RUTA_PERFIL);
    }

    private void cargarVista(String rutaFxml) {
        Object controlador = SceneManager.getInstance().cargarEnContenedor(rutaFxml, contentArea);

        if (controlador instanceof ClienteInicioController c) {
            c.configurar(this);
        } else if (controlador instanceof ClienteCatalogoController c) {
            c.configurar(this);
        } else if (controlador instanceof ClientePedidosController c) {
            c.configurar(this);
        } else if (controlador instanceof ClientePerfilController c) {
            c.configurar(this);
        }
    }

    public Map<Producto, Integer> getCarrito() {
        return carrito;
    }

    public void agregarAlCarrito(Producto producto, int cantidad) {
        carrito.merge(producto, cantidad, Integer::sum);
    }

    public int totalCarrito() {
        return carrito.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getIdUsuarioCliente() {
        return idUsuarioCliente;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public Pedido crearPedidoDesdeCarrito() {
        if (carrito.isEmpty()) {
            return null;
        }

        Pedido nuevo = pedidoService.crearPedido(idUsuarioCliente, new LinkedHashMap<>(carrito));
        pedidos.add(0, nuevo);
        carrito.clear();
        return nuevo;
    }

    public boolean isNotificarCorreo() {
        return notificarCorreo;
    }

    public void setNotificarCorreo(boolean notificarCorreo) {
        this.notificarCorreo = notificarCorreo;
    }

    public boolean isNotificarSms() {
        return notificarSms;
    }

    public void setNotificarSms(boolean notificarSms) {
        this.notificarSms = notificarSms;
    }

    private void mostrarError(String contexto, Exception error) {
        Alert alerta = new Alert(AlertType.ERROR, contexto + ".\n" + error.getMessage(), ButtonType.OK);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
