package edu.eduark.bizarre.fabrica.flowtech.controller.dashboard;

import edu.eduark.bizarre.fabrica.flowtech.controller.inventario.InventarioFormController;
import edu.eduark.bizarre.fabrica.flowtech.controller.pedido.PedidoEditarFormController;
import edu.eduark.bizarre.fabrica.flowtech.controller.pedido.PedidoFormController;
import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.EmpleadoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.InventarioItem;
import edu.eduark.bizarre.fabrica.flowtech.model.LineaPedido;
import edu.eduark.bizarre.fabrica.flowtech.model.PedidoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import edu.eduark.bizarre.fabrica.flowtech.model.UsuarioOpcion;
import edu.eduark.bizarre.fabrica.flowtech.service.InventarioService;
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


public class DashboardAdminController {

    private static final String RUTA_FORM_INVENTARIO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/inventario-form-view.fxml";
    private static final String RUTA_FORM_PEDIDO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/pedido-form-view.fxml";
    private static final String RUTA_FORM_PEDIDO_EDITAR = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/pedido-editar-form-view.fxml";

 
    @FXML private TableView<InventarioItem> tablaInventario;
    @FXML private TableColumn<InventarioItem, String> colSku;
    @FXML private TableColumn<InventarioItem, String> colProducto;
    @FXML private TableColumn<InventarioItem, Number> colStock;
    @FXML private TableColumn<InventarioItem, Number> colMinimo;
    @FXML private TableColumn<InventarioItem, String> colEstado;
    @FXML private TableColumn<InventarioItem, Void> colAccionesInventario;
    @FXML private ComboBox<String> cmbFiltro;
    @FXML private TextField txtBuscar;
    @FXML private Label lblProductos;
    @FXML private Label lblAlertas;
    @FXML private Label lblPedidosProceso;

  
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

 
    @FXML private TableView<EmpleadoResumen> tablaEmpleados;
    @FXML private TableColumn<EmpleadoResumen, Number> colIdEmpleado;
    @FXML private TableColumn<EmpleadoResumen, String> colNombreEmpleado;
    @FXML private TableColumn<EmpleadoResumen, String> colEmailEmpleado;
    @FXML private TableColumn<EmpleadoResumen, String> colEstadoEmpleado;
    @FXML private TableColumn<EmpleadoResumen, String> colFechaEmpleado;
    @FXML private TableColumn<EmpleadoResumen, Void> colAccionesEmpleado;
    @FXML private TextField txtBuscarEmpleados;

    private final InventarioService inventarioService = new InventarioService();
    private final PedidoService pedidoService = new PedidoService();
    private final ProductoService productoService = new ProductoService();
    private final UsuarioService usuarioService = new UsuarioService();

    private final ObservableList<InventarioItem> inventario = FXCollections.observableArrayList();
    private FilteredList<InventarioItem> inventarioFiltrado;

    private final ObservableList<PedidoResumen> pedidos = FXCollections.observableArrayList();
    private FilteredList<PedidoResumen> pedidosFiltrados;

    private final ObservableList<EmpleadoResumen> empleados = FXCollections.observableArrayList();
    private FilteredList<EmpleadoResumen> empleadosFiltrados;

    @FXML
    private void initialize() {
        configurarTablaInventario();
        configurarTablaPedidos();
        configurarTablaEmpleados();
        cargarInventario();
        cargarPedidos();
        cargarEmpleados();
    }

    private void configurarTablaInventario() {
        colSku.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().sku()));
        colProducto.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().producto()));
        colStock.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().stockActual()));
        colMinimo.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().stockMinimo()));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().estado()));
        colAccionesInventario.setCellFactory(columna -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox contenedor = new HBox(6.0, btnEditar, btnEliminar);
            {
                btnEditar.getStyleClass().add("small-action");
                btnEliminar.getStyleClass().add("remove-cart-button");
                btnEditar.setOnAction(e -> editarInventario(getTableView().getItems().get(getIndex())));
                btnEliminar.setOnAction(e -> eliminarInventario(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : contenedor);
            }
        });

        cmbFiltro.setItems(FXCollections.observableArrayList("Todos", "Solo alertas", "Disponibles"));
        cmbFiltro.getSelectionModel().selectFirst();
        inventarioFiltrado = new FilteredList<>(inventario, fila -> true);
        tablaInventario.setItems(inventarioFiltrado);
        cmbFiltro.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosInventario());
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosInventario());
    }

    private void cargarInventario() {
        inventario.clear();
        try {
            inventario.addAll(inventarioService.listarTodo());
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar el inventario", error);
        }

        lblProductos.setText(String.valueOf(inventario.size()));
        lblAlertas.setText(String.valueOf(inventario.stream().filter(fila -> fila.stockActual() <= fila.stockMinimo()).count()));
        actualizarPedidosEnProceso();
    }

    private void actualizarPedidosEnProceso() {
        try {
            lblPedidosProceso.setText(String.valueOf(pedidoService.contarEnProceso()));
        } catch (ServicioException error) {
            lblPedidosProceso.setText("0");
        }
    }

    private void aplicarFiltrosInventario() {
        String filtro = cmbFiltro.getValue();
        String busqueda = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        inventarioFiltrado.setPredicate(fila -> ("Todos".equals(filtro)
                || ("Solo alertas".equals(filtro) && fila.stockActual() <= fila.stockMinimo())
                || ("Disponibles".equals(filtro) && fila.stockActual() > fila.stockMinimo()))
                && (busqueda.isEmpty() || fila.sku().toLowerCase().contains(busqueda)
                || fila.producto().toLowerCase().contains(busqueda)));
    }

    @FXML
    private void agregarInventario() {
        List<ProductoOpcion> disponibles = obtenerProductosSinInventario();
        if (disponibles.isEmpty()) {
            mostrarInfo("Sin productos disponibles", "Todos los productos del catálogo ya tienen una fila de inventario.");
            return;
        }

        Dialog<InventarioFormulario> dialogo = new Dialog<>();
        dialogo.setTitle("Agregar producto al inventario");
        dialogo.getDialogPane().getStylesheets().add(hojaEstilos());
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        InventarioFormController controlador = SceneManager.getInstance().cargarEnDialogo(dialogo, RUTA_FORM_INVENTARIO);
        controlador.configurarAgregar(disponibles);

        dialogo.setResultConverter(boton -> boton == ButtonType.OK
                ? new InventarioFormulario(controlador.getProductoSeleccionado(), controlador.getStockActual(), controlador.getStockMinimo())
                : null);

        dialogo.showAndWait().ifPresent(formulario -> {
            if (formulario.producto() == null) {
                mostrarInfo("Falta información", "Debes seleccionar un producto.");
                return;
            }
            try {
                inventarioService.agregar(formulario.producto().id(), formulario.stockActual(), formulario.stockMinimo());
                cargarInventario();
            } catch (ServicioException error) {
                mostrarError("No fue posible agregar el producto al inventario", error);
            }
        });
    }

    private void editarInventario(InventarioItem fila) {
        Dialog<InventarioFormulario> dialogo = new Dialog<>();
        dialogo.setTitle("Editar inventario — " + fila.producto());
        dialogo.getDialogPane().getStylesheets().add(hojaEstilos());
        dialogo.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        InventarioFormController controlador = SceneManager.getInstance().cargarEnDialogo(dialogo, RUTA_FORM_INVENTARIO);
        controlador.configurarEditar(fila);

        dialogo.setResultConverter(boton -> boton == ButtonType.OK
                ? new InventarioFormulario(null, controlador.getStockActual(), controlador.getStockMinimo())
                : null);

        dialogo.showAndWait().ifPresent(formulario -> {
            try {
                inventarioService.actualizar(fila.idInvProducto(), formulario.stockActual(), formulario.stockMinimo());
                cargarInventario();
            } catch (ServicioException error) {
                mostrarError("No fue posible actualizar el inventario", error);
            }
        });
    }

    private void eliminarInventario(InventarioItem fila) {
        if (!confirmar("Eliminar del inventario",
                "¿Eliminar la fila de inventario de \"" + fila.producto() + "\"? Esta acción no se puede deshacer.")) {
            return;
        }
        try {
            inventarioService.eliminar(fila.idInvProducto());
            cargarInventario();
        } catch (ServicioException error) {
            mostrarError("No fue posible eliminar la fila de inventario", error);
        }
    }

    private List<ProductoOpcion> obtenerProductosSinInventario() {
        try {
            return inventarioService.productosSinInventario();
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar el catálogo de productos", error);
            return List.of();
        }
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
        actualizarPedidosEnProceso();
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

  

    private void configurarTablaEmpleados() {
        colIdEmpleado.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().idUsuario()));
        colNombreEmpleado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().nombre()));
        colEmailEmpleado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().email()));
        colEstadoEmpleado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().activo() ? "Activo" : "Inactivo"));
        colFechaEmpleado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().fechaRegistro()));
        colAccionesEmpleado.setCellFactory(columna -> new TableCell<>() {
            private final Button btnEstado = new Button();
            {
                btnEstado.setOnAction(e -> alternarEstadoEmpleado(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                if (vacio) {
                    setGraphic(null);
                    return;
                }
                EmpleadoResumen fila = getTableView().getItems().get(getIndex());
                btnEstado.setText(fila.activo() ? "Desactivar" : "Activar");
                btnEstado.getStyleClass().setAll(fila.activo() ? "remove-cart-button" : "small-action");
                setGraphic(btnEstado);
            }
        });

        empleadosFiltrados = new FilteredList<>(empleados, fila -> true);
        tablaEmpleados.setItems(empleadosFiltrados);
        txtBuscarEmpleados.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltrosEmpleados());
    }

    private void cargarEmpleados() {
        empleados.clear();
        try {
            empleados.addAll(usuarioService.listarEmpleados());
        } catch (ServicioException error) {
            mostrarError("No fue posible cargar los empleados", error);
        }
    }

    private void aplicarFiltrosEmpleados() {
        String busqueda = txtBuscarEmpleados.getText() == null ? "" : txtBuscarEmpleados.getText().trim().toLowerCase();
        empleadosFiltrados.setPredicate(fila -> busqueda.isEmpty()
                || fila.nombre().toLowerCase().contains(busqueda) || fila.email().toLowerCase().contains(busqueda));
    }

    private void alternarEstadoEmpleado(EmpleadoResumen fila) {
        boolean nuevoEstado = !fila.activo();
        String verbo = nuevoEstado ? "activar" : "desactivar";
        if (!confirmar("Cambiar estado", "¿Deseas " + verbo + " a " + fila.nombre() + "?")) {
            return;
        }
        try {
            usuarioService.cambiarEstadoEmpleado(fila.idUsuario(), nuevoEstado);
            cargarEmpleados();
        } catch (ServicioException error) {
            mostrarError("No fue posible actualizar el estado del empleado", error);
        }
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

  
    @FXML
    private void cerrarSesion() {
        SceneManager.getInstance().changeScene(
                "/edu/eduark/bizarre/fabrica/flowtech/resources/view/login-view.fxml",
                "FlowTech - Login");
    }

    private record InventarioFormulario(ProductoOpcion producto, int stockActual, int stockMinimo) { }
}