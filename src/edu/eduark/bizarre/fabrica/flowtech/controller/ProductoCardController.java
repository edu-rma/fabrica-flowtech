package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Producto;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Controller de una tarjeta de producto individual (vista definida en producto-card-view.fxml). */
public class ProductoCardController {

    @FXML
    private VBox raiz;
    @FXML
    private Label lblIcono;
    @FXML
    private Label lblCategoria;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblCodigo;
    @FXML
    private Label lblDetalle;
    @FXML
    private Label lblDisponibilidad;
    @FXML
    private Button btnAgregar;

    private Runnable alVerFicha;
    private Runnable alAgregarAlCarrito;

    public void configurar(Producto producto, boolean vistaLista) {
        lblIcono.setText(iconoCategoria(producto.categoria()));
        lblCategoria.setText(producto.categoria());
        lblNombre.setText(producto.nombre());
        lblCodigo.setText("Componente Electrónico " + producto.id() + " · MPN: " + producto.mpn());
        lblDetalle.setText(producto.especificaciones());
        lblDisponibilidad.setText(producto.disponible() ? "Disponible · precio por cotizar" : "Agotado");
        btnAgregar.setDisable(!producto.disponible());
        raiz.setPrefWidth(vistaLista ? 620 : 230);
    }

    public void setAlVerFicha(Runnable accion) {
        this.alVerFicha = accion;
    }

    public void setAlAgregarAlCarrito(Runnable accion) {
        this.alAgregarAlCarrito = accion;
    }

    @FXML
    private void verFicha() {
        if (alVerFicha != null) {
            alVerFicha.run();
        }
    }

    @FXML
    private void agregarAlCarrito() {
        if (alAgregarAlCarrito != null) {
            alAgregarAlCarrito.run();
        }
    }

    private String iconoCategoria(String categoria) {
        String c = categoria.toLowerCase();
        if (c.contains("procesador")) return "▣ CPU";
        if (c.contains("tarjeta gráfica")) return "▰ GPU";
        if (c.contains("madre")) return "▤ MAINBOARD";
        if (c.contains("memoria") || c.contains("ssd") || c.contains("disco")) return "▥ STORAGE";
        if (c.contains("sensor")) return "◉ SENSOR";
        if (c.contains("diodo")) return "◈ DIODO";
        if (c.contains("transistor") || c.contains("circuito") || c.contains("regulador")) return "▦ IC";
        if (c.contains("motor") || c.contains("actuador")) return "⚙ MOTOR";
        return "◌ ELECTRÓNICA";
    }
}
