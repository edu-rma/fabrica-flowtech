package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.repository.OrdenProduccionRepository;
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

/** Controlador para dashboard-empleado.fxml. */
public class DashboardEmpleadoController {
    
    @FXML private TableView<OrdenFila> tablaOrdenes;
    @FXML private TableColumn<OrdenFila, Number> colOrden;
    @FXML private TableColumn<OrdenFila, Number> colPedido;
    @FXML private TableColumn<OrdenFila, String> colProducto;
    @FXML private TableColumn<OrdenFila, Number> colCantidad;
    @FXML private TableColumn<OrdenFila, String> colInicio;
    @FXML private TableColumn<OrdenFila, String> colEstado;
    @FXML private TableColumn<OrdenFila, String> colAccion;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtBuscar;
    @FXML private Label lblTotal;
    @FXML private Label lblProceso;
    @FXML private Label lblPlanificadas;

    private final ObservableList<OrdenFila> ordenes = FXCollections.observableArrayList();
    private FilteredList<OrdenFila> ordenesFiltradas;

    // Instancia del repositorio
    private final OrdenProduccionRepository ordenProduccionRepository = new OrdenProduccionRepository();

    @FXML
    private void initialize() {
        colOrden.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().idOrden()));
        colPedido.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().idPedido()));
        colProducto.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().producto()));
        colCantidad.setCellValueFactory(dato -> new ReadOnlyIntegerWrapper(dato.getValue().cantidad()));
        colInicio.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().fechaInicio()));
        colEstado.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().estado()));
        colAccion.setCellValueFactory(dato -> new SimpleStringProperty("Consultar"));

        cmbEstado.setItems(FXCollections.observableArrayList("Todos", "PLANIFICADA", "EN_PROCESO", "FINALIZADA"));
        cmbEstado.getSelectionModel().selectFirst();
        ordenesFiltradas = new FilteredList<>(ordenes, fila -> true);
        tablaOrdenes.setItems(ordenesFiltradas);

        cmbEstado.valueProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        txtBuscar.textProperty().addListener((obs, anterior, nuevo) -> aplicarFiltros());
        cargarOrdenes();
    }

    private void cargarOrdenes() {
        ordenes.clear();
        
        // Carga de datos desde el repositorio
        var listaOrdenes = ordenProduccionRepository.obtenerOrdenesProduccion();
        for (var item : listaOrdenes) {
            ordenes.add(new OrdenFila(
                item.idOrden(),
                item.idPedido(),
                item.producto(),
                item.cantidad(),
                item.fechaInicio(),
                item.estado()
            ));
        }
        
        actualizarResumen();
    }

    private void aplicarFiltros() {
        String estado = cmbEstado.getValue();
        String busqueda = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        ordenesFiltradas.setPredicate(fila -> ("Todos".equals(estado) || fila.estado().equals(estado))
                && (busqueda.isEmpty() || fila.producto().toLowerCase().contains(busqueda)
                || String.valueOf(fila.idOrden()).contains(busqueda)));
    }

    private void actualizarResumen() {
        lblTotal.setText(String.valueOf(ordenes.size()));
        lblProceso.setText(String.valueOf(ordenes.stream().filter(fila -> "EN_PROCESO".equals(fila.estado())).count()));
        lblPlanificadas.setText(String.valueOf(ordenes.stream().filter(fila -> "PLANIFICADA".equals(fila.estado())).count()));
    }

    private record OrdenFila(int idOrden, int idPedido, String producto, int cantidad, String fechaInicio, String estado) { }
}
