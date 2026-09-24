package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Producto;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/** Controller de una fila del carrito (vista definida en carrito-item-view.fxml). */
public class CarritoItemController {

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblCantidad;

    private Runnable alRestar;
    private Runnable alSumar;
    private Runnable alEliminar;

    public void configurar(Producto producto, int cantidad) {
        lblNombre.setText(producto.nombre());
        lblCantidad.setText(String.valueOf(cantidad));
    }

    public void setAlRestar(Runnable accion) {
        this.alRestar = accion;
    }

    public void setAlSumar(Runnable accion) {
        this.alSumar = accion;
    }

    public void setAlEliminar(Runnable accion) {
        this.alEliminar = accion;
    }

    @FXML
    private void restar() {
        if (alRestar != null) {
            alRestar.run();
        }
    }

    @FXML
    private void sumar() {
        if (alSumar != null) {
            alSumar.run();
        }
    }

    @FXML
    private void eliminar() {
        if (alEliminar != null) {
            alEliminar.run();
        }
    }
}
