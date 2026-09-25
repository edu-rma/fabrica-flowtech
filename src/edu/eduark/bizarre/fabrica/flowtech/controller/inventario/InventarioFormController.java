package edu.eduark.bizarre.fabrica.flowtech.controller.inventario;

import edu.eduark.bizarre.fabrica.flowtech.model.InventarioItem;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;


public class InventarioFormController {

    @FXML private ComboBox<ProductoOpcion> cmbProducto;
    @FXML private Label lblProductoFijo;
    @FXML private Spinner<Integer> spnActual;
    @FXML private Spinner<Integer> spnMinimo;

    @FXML
    private void initialize() {
        lblProductoFijo.setVisible(false);
        lblProductoFijo.setManaged(false);
    }

    public void configurarAgregar(List<ProductoOpcion> productosDisponibles) {
        cmbProducto.setItems(FXCollections.observableArrayList(productosDisponibles));
        cmbProducto.getSelectionModel().selectFirst();
        cmbProducto.setVisible(true);
        cmbProducto.setManaged(true);
        lblProductoFijo.setVisible(false);
        lblProductoFijo.setManaged(false);
    }

    public void configurarEditar(InventarioItem fila) {
        cmbProducto.setVisible(false);
        cmbProducto.setManaged(false);
        lblProductoFijo.setVisible(true);
        lblProductoFijo.setManaged(true);
        lblProductoFijo.setText(fila.sku() + " — " + fila.producto());
        spnActual.getValueFactory().setValue(fila.stockActual());
        spnMinimo.getValueFactory().setValue(fila.stockMinimo());
    }

    public ProductoOpcion getProductoSeleccionado() {
        return cmbProducto.getValue();
    }

    public int getStockActual() {
        return spnActual.getValue();
    }

    public int getStockMinimo() {
        return spnMinimo.getValue();
    }
}
