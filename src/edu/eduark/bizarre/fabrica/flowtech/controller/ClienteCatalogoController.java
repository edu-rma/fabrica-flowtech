package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/** Controller de la sección "Catálogo" del panel de cliente (vista definida en cliente-catalogo-view.fxml). */
public class ClienteCatalogoController implements Initializable {

    @FXML
    private TextField buscar;
    @FXML
    private ComboBox<String> categoria;
    @FXML
    private ComboBox<String> stock;
    @FXML
    private ToggleButton cambiar;
    @FXML
    private Button carro;
    @FXML
    private FlowPane productosVista;

    private final List<Producto> productos = CatalogoLocal.obtener().stream()
            .map(f -> new Producto(f.id(), f.nombre(), f.mpn(), f.categoria(), f.especificaciones(), true))
            .toList();
    private boolean vistaLista;

    // El carrito ya NO vive aquí: se comparte con el resto del panel de
    // cliente a través del ClienteDashboardController (así "Volver a pedir"
    // en Inicio y el contador del carrito aquí siempre están sincronizados).
    private ClienteDashboardController dashboard;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        categoria.setItems(FXCollections.observableArrayList(
                productos.stream().map(Producto::categoria).distinct().sorted().collect(Collectors.toList())));
        categoria.getItems().add(0, "Todas");
        categoria.getSelectionModel().selectFirst();

        stock.setItems(FXCollections.observableArrayList("Todo el stock", "Disponibles", "Agotados"));
        stock.getSelectionModel().selectFirst();

        buscar.textProperty().addListener((o, a, n) -> actualizar());
        categoria.valueProperty().addListener((o, a, n) -> actualizar());
        stock.valueProperty().addListener((o, a, n) -> actualizar());
        cambiar.setOnAction(e -> {
            vistaLista = cambiar.isSelected();
            cambiar.setText(vistaLista ? "▦ Vista cuadrícula" : "☷ Vista lista");
            actualizar();
        });
        carro.setOnAction(e -> mostrarCarrito());
    }

    public void configurar(ClienteDashboardController dashboard) {
        this.dashboard = dashboard;
        actualizar();
    }

    private void actualizar() {
        carro.setText("🛒 " + dashboard.totalCarrito());
        List<Producto> filtrados = productos.stream()
                .filter(p -> (buscar.getText() == null || buscar.getText().isBlank()
                        || p.nombre().toLowerCase().contains(buscar.getText().toLowerCase()))
                        && ("Todas".equals(categoria.getValue()) || p.categoria().equals(categoria.getValue()))
                        && ("Todo el stock".equals(stock.getValue())
                            || ("Disponibles".equals(stock.getValue()) && p.disponible())
                            || ("Agotados".equals(stock.getValue()) && !p.disponible())))
                .collect(Collectors.toList());

        productosVista.getChildren().clear();
        for (Producto p : filtrados) {
            productosVista.getChildren().add(crearTarjeta(p));
        }
    }

    private Parent crearTarjeta(Producto p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/producto-card-view.fxml"));
            Parent nodo = loader.load();
            ProductoCardController controller = loader.getController();
            controller.configurar(p, vistaLista);
            controller.setAlVerFicha(() -> aviso(p.nombre(), "MPN: " + p.mpn() + "\n\n" + p.especificaciones()));
            controller.setAlAgregarAlCarrito(() -> {
                dashboard.agregarAlCarrito(p, 1);
                aviso("Carrito actualizado", p.nombre() + " fue añadido al carrito.");
                actualizar();
            });
            return nodo;
        } catch (IOException e) {
            e.printStackTrace();
            return new VBox();
        }
    }

    private void mostrarCarrito() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/carrito-dialog-view.fxml"));
            Parent contenido = loader.load();
            CarritoDialogController controller = loader.getController();
            controller.configurar(dashboard.getCarrito(), this::actualizar);

            ButtonType btnFinalizar = new ButtonType("Finalizar pedido", ButtonBar.ButtonData.OK_DONE);

            Dialog<Void> dialogo = new Dialog<>();
            dialogo.setTitle("Mi carrito");
            dialogo.getDialogPane().getButtonTypes().addAll(btnFinalizar, ButtonType.CLOSE);
            dialogo.getDialogPane().setContent(contenido);

            Button botonFinalizar = (Button) dialogo.getDialogPane().lookupButton(btnFinalizar);
            botonFinalizar.addEventFilter(ActionEvent.ACTION, evento -> {
                if (dashboard.getCarrito().isEmpty()) {
                    aviso("Carrito vacío", "Agrega al menos un producto antes de finalizar el pedido.");
                    evento.consume();
                    return;
                }
                try {
                    Pedido creado = dashboard.crearPedidoDesdeCarrito();
                    aviso("Pedido creado", "Tu pedido " + creado.numero()
                            + " fue registrado. Puedes verlo en \"Mis pedidos\".");
                } catch (SQLException | IllegalArgumentException error) {
                    aviso("No fue posible registrar el pedido", error.getMessage());
                    evento.consume();
                }
            });

            dialogo.showAndWait();
            actualizar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aviso(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
