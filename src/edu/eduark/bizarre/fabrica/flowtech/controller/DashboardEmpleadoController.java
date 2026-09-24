package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "SELECT op.id_orden_produccion, COALESCE(op.id_pedido, 0) AS id_pedido, p.nombre, "
                + "op.cantidad_a_fabricar, DATE_FORMAT(op.fecha_inicio, '%d/%m/%Y') AS fecha_inicio, op.estado "
                + "FROM ordenes_produccion op INNER JOIN productos p ON p.id_producto = op.id_producto "
                + "ORDER BY op.fecha_inicio DESC";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                ordenes.add(new OrdenFila(resultado.getInt("id_orden_produccion"), resultado.getInt("id_pedido"),
                        resultado.getString("nombre"), resultado.getInt("cantidad_a_fabricar"),
                        resultado.getString("fecha_inicio"), resultado.getString("estado")));
            }
        } catch (SQLException error) {
            System.err.println("No fue posible cargar las órdenes: " + error.getMessage());
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
