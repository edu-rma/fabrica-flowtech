package edu.eduark.bizarre.fabrica.flowtech.controller.dashboard;

import edu.eduark.bizarre.fabrica.flowtech.controller.pedido.PedidoEditarFormController;
import edu.eduark.bizarre.fabrica.flowtech.controller.pedido.PedidoFormController;
import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.InventarioItem;
import edu.eduark.bizarre.fabrica.flowtech.model.LineaPedido;
import edu.eduark.bizarre.fabrica.flowtech.model.OrdenProduccionResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.PedidoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import edu.eduark.bizarre.fabrica.flowtech.model.UsuarioOpcion;
import edu.eduark.bizarre.fabrica.flowtech.service.InventarioService;
import edu.eduark.bizarre.fabrica.flowtech.service.OrdenProduccionService;
import edu.eduark.bizarre.fabrica.flowtech.service.PedidoService;
import edu.eduark.bizarre.fabrica.flowtech.service.ProductoService;
import edu.eduark.bizarre.fabrica.flowtech.service.UsuarioService;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;


public class DashboardEmpleadoController {

    private static final String RUTA_FORM_PEDIDO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/pedido-form-view.fxml";
    private static final String RUTA_FORM_PEDIDO_EDITAR = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/pedido-editar-form-view.fxml";


    @FXML private TableView<OrdenProduccionResumen> tablaOrdenes;
    @FXML private TableColumn<OrdenProduccionResumen, Number> colOrden;
    @FXML private TableColumn<OrdenProduccionResumen, Number> colPedido;
    @FXML private TableColumn<OrdenProduccionResumen, String> colProductoOrden;
    @FXML private TableColumn<OrdenProduccionResumen, Number> colCantidad;
    @FXML private TableColumn<OrdenProduccionResumen, String> colInicio;
    @FXML private TableColumn<OrdenProduccionResumen, String> colEstadoOrden;
    @FXML private TableColumn<OrdenProduccionResumen, String> colAccion;
    @FXML private ComboBox<String> cmbEstadoOrden;
    @FXML private TextField txtBuscarOrden;
    @FXML private Label lblTotal;
    @FXML private Label lblProceso;
    @FXML private Label lblPlanificadas;


    @FXML private TableView<PedidoResumen> tablaPedidos;
    @FXML private TableColumn<PedidoResumen, Number> colIdPedido;
    @FXML private TableColumn<PedidoResumen, String> colCliente;
    @FXML private TableColumn<PedidoResumen, String> colEmpleado;
    @FXML private TableColumn<PedidoResumen, String> colFechaPedido;
    @FXML private TableColumn<PedidoResumen, String> colEstadoPedido;
    @FXML private TableColumn<PedidoResumen, String> colTotalPedido;
    @FXML private TableColumn<PedidoResumen, Void> colAccionesPedido;
    @FXML private ComboBox<String> cmbFiltroPedidos;
    @FXML private TextField txtBuscarPedidos;

   
    @FXML private TableView<InventarioItem> tablaInventario;
    @FXML private TableColumn<InventarioItem, String> colSku;
    @FXML private TableColumn<InventarioItem, String> colProductoInventario;
    @FXML private TableColumn<InventarioItem, Number> colStock;
    @FXML private TableColumn<InventarioItem, Number> colMinimo;
    @FXML private TableColumn<InventarioItem, String> colEstadoInventario;
    @FXML private ComboBox<String> cmbFiltroInventario;
    @FXML private TextField txtBuscarInventario;

    private final OrdenProduccionService ordenProduccionService = new OrdenProduccionService();
    private final PedidoService pedidoService = new PedidoService();
    private final InventarioService inventarioService = new InventarioService();
    private final ProductoService productoService = new ProductoService();
    private final UsuarioService usuarioService = new UsuarioService();

    private final ObservableList<OrdenProduccionResumen> ordenes = FXCollections.observableArrayList();
    private FilteredList<OrdenProduccionResumen> ordenesFiltradas;

    private final ObservableList<PedidoResumen> pedidos = FXCollections.observableArrayList();
    private FilteredList<PedidoResumen> pedidosFiltrados;

    private final ObservableList<InventarioItem> inventario = FXCollections.observableArrayList();
    private FilteredList<InventarioItem> inventarioFiltrado;

    @FXML
    private void initialize() {
        configurarTablaOrdenes();
        configurarTablaPedidos();
        configurarTablaInventario();
        cargarOrdenes();
        cargarPedidos();
        cargarInventario();
    }


    private void configurarTablaOrdenes() {
        colOrden.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().idOrden()));
        colPedido.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().idPedido()));
        colProductoOrden.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().producto()));
        colCantidad.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().cantidad()));
        colInicio.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().fechaInicio()));
        colEstadoOrden.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().estado()));
        colAccion.setCellValueFactory(dato -> new SimpleStringProperty("Consultar"));

        cmbEstadoOrden.setItems(FXCollections.observableArrayList("Todos", "PLANIFICADA", "EN_PROCESO", "FINALIZADA"));
        cmbEstadoOrden.getSelectionModel().selectFirst();
        ordenesFiltradas = new FilteredList<>(ordenes, fila -> true);
        tablaOrdenes.setItems(ordenesFiltradas);

        cmbEstadoOrden.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosOrdenes());
        txtBuscarOrden.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosOrdenes());
    }

    private void cargarOrdenes() {
        ordenes.clear();
        try {
            ordenes.addAll(ordenProduccionService.listarTodas());
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar las órdenes", error);
        }
        actualizarResumenOrdenes();
    }

    private void aplicarFiltrosOrdenes() {
        String estado = cmbEstadoOrden.getValue();
        String busqueda = txtBuscarOrden.getText() == null ? "" : txtBuscarOrden.getText().trim().toLowerCase();
        ordenesFiltradas.setPredicate(fila -> ("Todos".equals(estado) || fila.estado().equals(estado))
                && (busqueda.isEmpty() || fila.producto().toLowerCase().contains(busqueda)
                || String.valueOf(fila.idOrden()).contains(busqueda)));
    }

    private void actualizarResumenOrdenes() {
        lblTotal.setText(String.valueOf(ordenes.size()));
        lblProceso.setText(String.valueOf(ordenes.stream().filter(fila -> "EN_PROCESO".equals(fila.estado())).count()));
        lblPlanificadas.setText(String.valueOf(ordenes.stream().filter(fila -> "PLANIFICADA".equals(fila.estado())).count()));
    }

    private void configurarTablaPedidos() {
        colIdPedido.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().idPedido()));
        colCliente.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().cliente()));
        colEmpleado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().empleado()));
        colFechaPedido.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().fecha()));
        colEstadoPedido.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().estado()));
        colTotalPedido.setCellValueFactory(dato -> new SimpleStringProperty("Q" + dato.getValue().total().setScale(2)));
        colAccionesPedido.setCellFactory(columna -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar estado");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(6.0, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("small-action");
                btnEliminar.getStyleClass().add("remove-cart-button");
                btnEditar.setOnAction(e -> editarPedido(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminarPedido(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : contenedor);
            }
        });

        List<String> opcionesFiltro = new ArrayList<>();
        opcionesFiltro.add("Todos");
        opcionesFiltro.addAll(PedidoService.ESTADOS);
        cmbFiltroPedidos.setItems(FXCollections.observableArrayList(opcionesFiltro));
        cmbFiltroPedidos.getSelectionModel().selectFirst();
        pedidosFiltrados = new FilteredList<>(pedidos, fila -> true);
        tablaPedidos.setItems(pedidosFiltrados);
        cmbFiltroPedidos.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosPedidos());
        txtBuscarPedidos.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosPedidos());
    }

    private void cargarPedidos() {
        pedidos.clear();
        try {
            pedidos.addAll(pedidoService.listarTodos());
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar los pedidos", error);
        }
    }

    private void aplicarFiltrosPedidos() {
        String filtro = cmbFiltroPedidos.getValue();
        String busqueda = txtBuscarPedidos.getText() == null ? "" : txtBuscarPedidos.getText().trim().toLowerCase();
        pedidosFiltrados.setPredicate(fila -> ("Todos".equals(filtro) || fila.estado().equals(filtro))
                && (busqueda.isEmpty() || fila.cliente().toLowerCase().contains(busqueda)
                || String.valueOf(fila.idPedido()).contains(busqueda)));
    }

    @FXML
    private void agregarPedido() {
        List<UsuarioOpcion> clientes = usuarioService.listarClientesActivos();
        List<UsuarioOpcion> empleadosActivos = usuarioService.listarEmpleadosActivos();
        List<ProductoOpcion> productos = productoService.listarTodos();

        if (clientes.isEmpty() || empleadosActivos.isEmpty()) {
            mostrarInfo("Faltan datos", "Debe existir al menos un cliente y un empleado activos para crear un pedido.");
            return;
        }
        if (productos.isEmpty()) {
            mostrarInfo("Sin productos", "No hay productos registrados en el catálogo.");
            return;
        }

        Dialog<Boolean> dialogo = new Dialog<>();
        dialogo.setTitle("Nuevo pedido");
        dialogo.getDialogPane().getStylesheets().add(hojaEstilos());
        ButtonType btnCrear = new ButtonType("Crear pedido", ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(btnCrear, ButtonType.CANCEL);

        PedidoFormController controlador = SceneManager.getInstance().cargarEnDialogo(dialogo, RUTA_FORM_PEDIDO);
        controlador.configurar(clientes, empleadosActivos, productos);

        dialogo.setResultConverter(boton -> boton == btnCrear);

        Optional<Boolean> resultado = dialogo.showAndWait();
        if (resultado.isEmpty() || !resultado.get()) {
            return;
        }

        List<LineaPedido> lineas = controlador.getLineas();
        if (lineas.isEmpty()) {
            mostrarInfo("Pedido vacío", "Agrega al menos un producto antes de crear el pedido.");
            return;
        }

        try {
            pedidoService.crear(controlador.getCliente().id(), controlador.getEmpleado().id(), lineas);
            cargarPedidos();
        } catch (ServicioException error) {
            mostrarError("No fue posible crear el pedido", error);
        }
    }

    private void editarPedido(PedidoResumen fila) {
        List<UsuarioOpcion> empleadosActivos = usuarioService.listarEmpleadosActivos();

        Dialog<Boolean> dialogo = new Dialog<>();
        dialogo.setTitle("Editar pedido #" + fila.idPedido());
        dialogo.getDialogPane().getStylesheets().add(hojaEstilos());
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        PedidoEditarFormController controlador = SceneManager.getInstance().cargarEnDialogo(dialogo, RUTA_FORM_PEDIDO_EDITAR);
        controlador.configurar(fila, empleadosActivos);

        dialogo.setResultConverter(boton -> boton == ButtonType.OK);

        if (dialogo.showAndWait().orElse(false)) {
            try {
                pedidoService.actualizarEstadoYEmpleado(fila.idPedido(), controlador.getEstado(), controlador.getEmpleado().id());
                cargarPedidos();
            } catch (ServicioException error) {
                mostrarError("No fue posible actualizar el pedido", error);
            }
        }
    }

    private void eliminarPedido(PedidoResumen fila) {
        if (!confirmar("Eliminar pedido",
                "¿Eliminar el pedido #" + fila.idPedido() + " de " + fila.cliente() + "? Se eliminarán también sus detalles.")) {
            return;
        }
        try {
            pedidoService.eliminar(fila.idPedido());
            cargarPedidos();
        } catch (ServicioException error) {
            mostrarError("No fue posible eliminar el pedido", error);
        }
    }

    private void configurarTablaInventario() {
        colSku.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().sku()));
        colProductoInventario.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().producto()));
        colStock.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().stockActual()));
        colMinimo.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().stockMinimo()));
        colEstadoInventario.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().estado()));
        // Sin columna de acciones ni botones de agregar/editar/eliminar: el empleado solo consulta.

        cmbFiltroInventario.setItems(FXCollections.observableArrayList("Todos", "Solo alertas", "Disponibles"));
        cmbFiltroInventario.getSelectionModel().selectFirst();
        inventarioFiltrado = new FilteredList<>(inventario, fila -> true);
        tablaInventario.setItems(inventarioFiltrado);
        cmbFiltroInventario.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosInventario());
        txtBuscarInventario.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosInventario());
    }

    private void cargarInventario() {
        inventario.clear();
        try {
            inventario.addAll(inventarioService.listarTodo());
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar el inventario", error);
        }
    }

    private void aplicarFiltrosInventario() {
        String filtro = cmbFiltroInventario.getValue();
        String busqueda = txtBuscarInventario.getText() == null ? "" : txtBuscarInventario.getText().trim().toLowerCase();
        inventarioFiltrado.setPredicate(fila -> ("Todos".equals(filtro)
                || ("Solo alertas".equals(filtro) && fila.stockActual() <= fila.stockMinimo())
                || ("Disponibles".equals(filtro) && fila.stockActual() > fila.stockMinimo()))
                && (busqueda.isEmpty() || fila.sku().toLowerCase().contains(busqueda)
                || fila.producto().toLowerCase().contains(busqueda)));
    }

    
    @FXML
    private void cerrarSesion() {
        SceneManager.getInstance().changeScene(
                "/edu/eduark/bizarre/fabrica/flowtech/resources/view/login-view.fxml",
                "FlowTech - Login");
    }

    private String hojaEstilos() {
        return getClass().getResource(
                "/edu/eduark/bizarre/fabrica/flowtech/resources/style/css/flowtech-theme.css").toExternalForm();
    }

    private boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarError(String contexto, Exception error) {
        System.err.println(contexto + ": " + error.getMessage());
        Alert alerta = new Alert(AlertType.ERROR, contexto + ".\n" + error.getMessage(), ButtonType.OK);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
