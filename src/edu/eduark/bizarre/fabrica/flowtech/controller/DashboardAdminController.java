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

/** Controlador para dashboard-admin.fxml. */
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
        String sql = "SELECT p.codigo_sku, p.nombre, ip.stock_actual, ip.stock_minimo "
                + "FROM inventario_productos ip INNER JOIN productos p ON p.id_producto = ip.id_producto "
                + "ORDER BY p.codigo_sku";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                int actual = resultado.getInt("stock_actual");
                int minimo = resultado.getInt("stock_minimo");
                inventario.add(new InventarioFila(resultado.getString("codigo_sku"), resultado.getString("nombre"),
                        actual, minimo, actual <= minimo ? "REABASTECER" : "DISPONIBLE"));
            }
        } catch (SQLException error) {
            System.err.println("No fue posible cargar el inventario: " + error.getMessage());
        }

        lblProductos.setText(String.valueOf(inventario.size()));
        lblAlertas.setText(String.valueOf(inventario.stream().filter(fila -> fila.stockActual() <= fila.stockMinimo()).count()));
        lblPedidosProceso.setText(String.valueOf(contarPedidosEnProceso()));
    }

    private int contarPedidosEnProceso() {
        String sql = "SELECT COUNT(*) FROM pedidos WHERE estado = 'EN_PRODUCCION'";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            return resultado.next() ? resultado.getInt(1) : 0;
        } catch (SQLException error) {
            System.err.println("No fue posible contar los pedidos: " + error.getMessage());
            return 0;
        }
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
