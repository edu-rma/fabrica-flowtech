package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.repository.InventarioRepository;
import edu.eduark.bizarre.fabrica.flowtech.repository.PedidoRepository;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class DashboardAdminController {
    
    @FXML private TableView<InventarioFila> tablaInventario;
    @FXML private TableColumn<InventarioFila, String> colSku;
    @FXML private TableColumn<InventarioFila, String> colProducto;
    @FXML private TableColumn<InventarioFila, Number> colStock;
    @FXML private TableColumn<InventarioFila, Number> colMinimo;
    @FXML private TableColumn<InventarioFila, String> colEstado;
    @FXML private ComboBox<String> cmbFiltro;
    @FXML private TextField txtBuscar;
    @FXML private Label lblProductos;
    @FXML private Label lblAlertas;
    @FXML private Label lblPedidosProceso;

    private final ObservableList<InventarioFila> inventario = FXCollections.observableArrayList();
    private FilteredList<InventarioFila> inventarioFiltrado;

    // Instancias de la capa de datos (Repository)
    private final InventarioRepository inventarioRepository = new InventarioRepository();
    private final PedidoRepository pedidoRepository = new PedidoRepository();

    @FXML
    private void initialize() {
        colSku.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().sku()));
        colProducto.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().producto()));
        colStock.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().stockActual()));
        colMinimo.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().stockMinimo()));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().estado()));

        cmbFiltro.setItems(FXCollections.observableArrayList("Todos", "Solo alertas", "Disponibles"));
        cmbFiltro.getSelectionModel().selectFirst();
        inventarioFiltrado = new FilteredList<>(inventario, fila -> true);
        tablaInventario.setItems(inventarioFiltrado);
        cmbFiltro.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        cargarInventario();
    }

    private void cargarInventario() {
        inventario.clear();
        
   
        var items = inventarioRepository.obtenerInventario();
        for (var item : items) {
            String estado = item.stockActual() <= item.stockMinimo() ? "REABASTECER" : "DISPONIBLE";
            inventario.add(new InventarioFila(item.sku(), item.producto(), item.stockActual(), item.stockMinimo(), estado));
        }

        lblProductos.setText(String.valueOf(inventario.size()));
        lblAlertas.setText(String.valueOf(inventario.stream().filter(fila -> fila.stockActual() <= fila.stockMinimo()).count()));
        lblPedidosProceso.setText(String.valueOf(pedidoRepository.contarPedidosEnProduccion()));
    }

    private void aplicarFiltros() {
        String filtro = cmbFiltro.getValue();
        String busqueda = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        inventarioFiltrado.setPredicate(fila -> ("Todos".equals(filtro)
                || ("Solo alertas".equals(filtro) && fila.stockActual() <= fila.stockMinimo())
                || ("Disponibles".equals(filtro) && fila.stockActual() > fila.stockMinimo()))
                && (busqueda.isEmpty() || fila.sku().toLowerCase().contains(busqueda)
                || fila.producto().toLowerCase().contains(busqueda)));
    }

    private record InventarioFila(String sku, String producto, int stockActual, int stockMinimo, String estado) { }
}
