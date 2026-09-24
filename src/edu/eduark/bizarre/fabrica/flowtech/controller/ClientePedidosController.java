package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.utils.FacturaUtil;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

/**
 * Controller de la seccion "Mis pedidos" del panel de cliente
 * (vista definida en cliente-pedidos-view.fxml).
 *
 * La lista de pedidos ahora viene del ClienteDashboardController (se
 * comparte con Inicio, que usa los mismos datos para los KPIs y para
 * "Volver a pedir"). El boton "PDF" de cada fila ya genera un archivo
 * de verdad con FacturaUtil, en vez de solo mostrar un aviso.
 */
public class ClientePedidosController implements Initializable {

    @FXML
    private TableView<Pedido> tabla;
    @FXML
    private TableColumn<Pedido, String> colPedido;
    @FXML
    private TableColumn<Pedido, String> colFecha;
    @FXML
    private TableColumn<Pedido, String> colEstado;
    @FXML
    private TableColumn<Pedido, String> colTotal;
    @FXML
    private TableColumn<Pedido, String> colFactura;

    private ClienteDashboardController dashboard;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colPedido.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numero()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().total()));

        colFactura.setCellFactory(c -> new TableCell<>() {
            private final Button pdf = new Button("PDF");
            {
                pdf.getStyleClass().add("small-action");
                pdf.setOnAction(e -> {
                    Pedido pedido = getTableView().getItems().get(getIndex());
                    FacturaUtil.generar(pdf.getScene().getWindow(), pedido);
                });
            }

            @Override
            protected void updateItem(String item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : pdf);
            }
        });

        tabla.setRowFactory(t -> {
            TableRow<Pedido> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (!fila.isEmpty() && e.getClickCount() == 2) {
                    aviso("Detalle " + fila.getItem().numero(), fila.getItem().detalle());
                }
            });
            return fila;
        });
    }

    public void configurar(ClienteDashboardController dashboard) {
        this.dashboard = dashboard;
        tabla.setItems(FXCollections.observableArrayList(dashboard.getPedidos()));
    }

    private void aviso(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
