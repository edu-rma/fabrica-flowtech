package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;
import edu.eduark.bizarre.fabrica.flowtech.model.RolUsuario;
import edu.eduark.bizarre.fabrica.flowtech.repository.PedidoRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

/**
 * Controller del panel de cliente. El sidebar y el layout general vienen de
 * dashboard-cliente-view.fxml (Scene Builder); cada seccion del contenido
 * (Inicio, Catalogo, Mis Pedidos, Mi Perfil) es a su vez su propio FXML,
 * cargado aqui con FXMLLoader. Este controller ya NO construye ningun nodo
 * visual por codigo.
 *
 * Ademas, como esta pantalla vive mientras dura toda la sesion del cliente,
 * es el lugar natural para guardar el estado que se comparte entre secciones
 * (carrito, pedidos, metodos de pago, preferencias). Nada de esto se guarda
 * en base de datos: es estado en memoria que dura mientras la app esta
 * abierta, igual que ya funcionaba el carrito dentro de Catalogo.
 */
public class ClienteDashboardController extends BaseDashboardController {

    private static final String RUTA_INICIO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-inicio-view.fxml";
    private static final String RUTA_CATALOGO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-catalogo-view.fxml";
    private static final String RUTA_PEDIDOS = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-pedidos-view.fxml";
    private static final String RUTA_PERFIL = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/cliente-perfil-view.fxml";

    // ---- Estado compartido de la sesion (en memoria, sin base de datos) ----

    private final Map<Producto, Integer> carrito = new LinkedHashMap<>();

    private final List<Pedido> pedidos = new ArrayList<>(List.of(
            new Pedido("#FT-2026-0148", "20 sep 2026", "En camino", "Q1,899.00", "Panel solar 450W \u00b7 Cant. 1"),
            new Pedido("#FT-2026-0126", "04 sep 2026", "Entregado", "Q320.00", "Sensor inteligente \u00b7 Cant. 1"),
            new Pedido("#FT-2026-0098", "18 ago 2026", "Cancelado", "Q2,390.00", "Kit de automatizacion \u00b7 Cant. 1")));

    private final List<String> metodosPago = new ArrayList<>(List.of("\u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 \u2022\u2022\u2022\u2022 4821 \u00b7 Visa"));

    private boolean notificarCorreo = true;
    private boolean notificarSms = false;

    private final PedidoRepository pedidoRepository = new PedidoRepository();
    private int idUsuarioCliente;

    public void configurarUsuario(int idUsuarioCliente, String nombreUsuario, RolUsuario rol) {
        this.idUsuarioCliente = idUsuarioCliente;
        super.configurarUsuario(nombreUsuario, rol);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            Parent vista = loader.load();

            // Cada vez que se carga una seccion, si su controller necesita
            // datos compartidos (carrito, pedidos, etc.) se los entregamos aqui.
            Object controlador = loader.getController();
            if (controlador instanceof ClienteInicioController c) {
                c.configurar(this);
            } else if (controlador instanceof ClienteCatalogoController c) {
                c.configurar(this);
            } else if (controlador instanceof ClientePedidosController c) {
                c.configurar(this);
            } else if (controlador instanceof ClientePerfilController c) {
                c.configurar(this);
            }

            contentArea.getChildren().setAll(vista);
        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + rutaFxml);
            e.printStackTrace();
        }
    }

    // ---- Acceso al estado compartido para las secciones hijas ----

    public Map<Producto, Integer> getCarrito() {
        return carrito;
    }

    public void agregarAlCarrito(Producto producto, int cantidad) {
        carrito.merge(producto, cantidad, Integer::sum);
    }

    public int totalCarrito() {
        return carrito.values().stream().mapToInt(Integer::intValue).sum();
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    /** Guarda el pedido y sus detalles en una sola transacción de base de datos. */
    public Pedido crearPedidoDesdeCarrito() throws SQLException {
        if (carrito.isEmpty()) {
            return null;
        }

        Pedido nuevo = pedidoRepository.crearPedido(idUsuarioCliente, new LinkedHashMap<>(carrito));
        pedidos.add(0, nuevo);
        carrito.clear();
        return nuevo;
    }

    public List<String> getMetodosPago() {
        return metodosPago;
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
}
