package edu.eduark.bizarre.fabrica.flowtech.controller.inventario;

import edu.eduark.bizarre.fabrica.flowtech.model.InventarioItem;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class InventarioFormController {

    @FXML private ComboBox<ProductoOpcion> cmbProducto;
    @FXML private Label lblProductoFijo;
    @FXML private Spinner<Integer> spnActual;
    @FXML private Spinner<Integer> spnMinimo;

    @FXML
    private void initialize() {
        lblProductoFijo.setVisible(false);
        lblProductoFijo.setManaged(false);

        // Asignación de ValueFactory
        spnActual.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000, 0));
        spnMinimo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000, 5));

        // Permite escribir valores manualmente en el Spinner
        spnActual.setEditable(true);
        spnMinimo.setEditable(true);
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
        if (fila == null) return;

        cmbProducto.setVisible(false);
        cmbProducto.setManaged(false);
        
        lblProductoFijo.setVisible(true);
        lblProductoFijo.setManaged(true);
        lblProductoFijo.setText(fila.sku() + " — " + fila.producto());
        
        if (spnActual.getValueFactory() != null) {
            spnActual.getValueFactory().setValue(fila.stockActual());
        }
        if (spnMinimo.getValueFactory() != null) {
            spnMinimo.getValueFactory().setValue(fila.stockMinimo());
        }
    }

    public ProductoOpcion getProductoSeleccionado() {
        return cmbProducto.getValue();
    }

    public int getStockActual() {
        sincronizarTextSpinner(spnActual);
        return (spnActual.getValue() != null) ? spnActual.getValue() : 0;
    }

    public int getStockMinimo() {
        sincronizarTextSpinner(spnMinimo);
        return (spnMinimo.getValue() != null) ? spnMinimo.getValue() : 0;
    }

    /**
     * Forzar la sincronización del texto escrito manualmente en el Spinner con su valor interno.
     */
    private void sincronizarTextSpinner(Spinner<Integer> spinner) {
        if (spinner != null && spinner.isEditable()) {
            String text = spinner.getEditor().getText();
            try {
                int value = Integer.parseInt(text);
                spinner.getValueFactory().setValue(value);
            } catch (NumberFormatException e) {
                // Si el texto ingresado no es válido, se restaura el valor actual del ValueFactory
                spinner.getEditor().setText(spinner.getValue().toString());
            }
        }
    }
}