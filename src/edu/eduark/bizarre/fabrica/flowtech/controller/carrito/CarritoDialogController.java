package edu.eduark.bizarre.fabrica.flowtech.controller.carrito;

import edu.eduark.bizarre.fabrica.flowtech.model.Producto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Controller del contenido del diálogo "Mi carrito" (vista definida en carrito-dialog-view.fxml). */
public class CarritoDialogController {

    @FXML
    private Label lblVacio;
    @FXML
    private Label lblAyuda;
    @FXML
    private VBox filas;
    @FXML
    private Label lblResumen;

    private Map<Producto, Integer> productosCarrito;
    private Runnable alCambiarCarrito;

    public void configurar(Map<Producto, Integer> productosCarrito, Runnable alCambiarCarrito) {
        this.productosCarrito = productosCarrito;
        this.alCambiarCarrito = alCambiarCarrito;
        refrescar();
    }

    private void refrescar() {
        filas.getChildren().clear();
        boolean vacio = productosCarrito.isEmpty();

        lblVacio.setVisible(vacio);
        lblVacio.setManaged(vacio);
        lblAyuda.setVisible(!vacio);
        lblAyuda.setManaged(!vacio);
        lblResumen.setVisible(!vacio);
        lblResumen.setManaged(!vacio);

        if (vacio) {
            return;
        }

        for (Producto producto : new ArrayList<>(productosCarrito.keySet())) {
            filas.getChildren().add(crearFila(producto));
        }

        int total = productosCarrito.values().stream().mapToInt(Integer::intValue).sum();
        lblResumen.setText("Total de artículos: " + total + " · Precio por cotizar");
    }

    private Parent crearFila(Producto producto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/carrito-item-view.fxml"));
            Parent nodo = loader.load();
            CarritoItemController controller = loader.getController();
            controller.configurar(producto, productosCarrito.get(producto));
            controller.setAlRestar(() -> cambiarCantidad(producto, -1));
            controller.setAlSumar(() -> cambiarCantidad(producto, 1));
            controller.setAlEliminar(() -> {
                productosCarrito.remove(producto);
                trasCambio();
            });
            return nodo;
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }

    private void cambiarCantidad(Producto producto, int cambio) {
        int nueva = productosCarrito.getOrDefault(producto, 0) + cambio;
        if (nueva <= 0) {
            productosCarrito.remove(producto);
        } else {
            productosCarrito.put(producto, nueva);
        }
        trasCambio();
    }

    private void trasCambio() {
        if (alCambiarCarrito != null) {
            alCambiarCarrito.run();
        }
        refrescar();
    }
}
