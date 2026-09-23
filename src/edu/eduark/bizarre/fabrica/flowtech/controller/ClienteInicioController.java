package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.model.Producto;
import edu.eduark.bizarre.fabrica.flowtech.utils.FacturaUtil;

import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Window;

/**
 * Controller de la seccion "Inicio" del panel de cliente
 * (vista definida en cliente-inicio-view.fxml).
 *
 * Ya no trabaja con datos fijos: recibe el ClienteDashboardController (que
 * guarda el carrito y los pedidos de la sesion) y con eso arma los KPIs y
 * responde de verdad a los botones de "acciones rapidas".
 */
public class ClienteInicioController {

    @FXML
    private Label lblPedidosActivos;
    @FXML
    private Label lblComprasRealizadas;
    @FXML
    private Label lblBeneficios;
    @FXML
    private Label lblResumenPedido;
    @FXML
    private Label lblEstadoPedido;

    private ClienteDashboardController dashboard;

    public void configurar(ClienteDashboardController dashboard) {
        this.dashboard = dashboard;
        actualizarResumen();
    }

    private void actualizarResumen() {
        List<Pedido> pedidos = dashboard.getPedidos();

        long activos = pedidos.stream()
                .filter(p -> !p.estado().equals("Entregado") && !p.estado().equals("Cancelado"))
                .count();
        lblPedidosActivos.setText(String.valueOf(activos));
        lblComprasRealizadas.setText(String.valueOf(pedidos.size()));

        long entregados = pedidos.stream().filter(p -> p.estado().equals("Entregado")).count();
        lblBeneficios.setText((entregados * 125) + " pts");

        if (pedidos.isEmpty()) {
            lblResumenPedido.setText("Sin pedidos recientes");
            lblEstadoPedido.setText("Sin estado");
        } else {
            Pedido reciente = pedidos.get(0);
            lblResumenPedido.setText("Pedido " + reciente.numero() + " · " + reciente.detalle());
            lblEstadoPedido.setText(reciente.estado());
            lblEstadoPedido.getStyleClass().setAll(
                    reciente.estado().equals("Entregado") ? "timeline-done" : "timeline-next");
        }
    }

    @FXML
    private void volverAPedir() {
        List<Pedido> pedidos = dashboard.getPedidos();
        if (pedidos.isEmpty()) {
            aviso("Volver a pedir", "Todavia no tienes pedidos anteriores.");
            return;
        }

        Pedido ultimoPedido = pedidos.get(0);

        // No tenemos el Producto original ligado al pedido (los pedidos son
        // historicos), asi que armamos uno "de repeticion" a partir del
        // detalle del pedido y lo sumamos al carrito compartido.
        Producto productoRepetido = new Producto(
                -ultimoPedido.numero().hashCode(),
                ultimoPedido.detalle(),
                ultimoPedido.numero(),
                "Repeticion de pedido",
                "Repetido desde Mis Pedidos · " + ultimoPedido.fecha(),
                true);

        dashboard.agregarAlCarrito(productoRepetido, 1);
        aviso("Volver a pedir", ultimoPedido.detalle() + " se agrego al carrito. Total en carrito: "
                + dashboard.totalCarrito() + " articulo(s).");
    }

    @FXML
    private void contactarSoporte() {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Contactar soporte");
        dialogo.setHeaderText("Cuentanos en que te ayudamos");
        dialogo.setContentText("Mensaje:");

        Optional<String> resultado = dialogo.showAndWait();
        resultado.filter(msg -> !msg.isBlank()).ifPresent(msg ->
                aviso("Ticket creado", "Tu mensaje fue enviado a soporte:\n\n\"" + msg + "\"\n\nTe contactaremos pronto."));
    }

    @FXML
    private void descargarFactura(ActionEvent event) {
        List<Pedido> pedidos = dashboard.getPedidos();
        if (pedidos.isEmpty()) {
            aviso("Descargar factura", "Todavia no tienes pedidos para generar un comprobante.");
            return;
        }
        Window ventana = ((Node) event.getSource()).getScene().getWindow();
        FacturaUtil.generar(ventana, pedidos.get(0));
    }

    private void aviso(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
