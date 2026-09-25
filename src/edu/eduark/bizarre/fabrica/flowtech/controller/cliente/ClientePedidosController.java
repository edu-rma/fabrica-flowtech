package edu.eduark.bizarre.fabrica.flowtech.controller.cliente;

import edu.eduark.bizarre.fabrica.flowtech.controller.cliente.ClienteDashboardController;
import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;
import edu.eduark.bizarre.fabrica.flowtech.utils.FacturaUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;


public class ClientePedidosController {

    @FXML private TableView<Pedido> tabla;
    @FXML private TableColumn<Pedido, String> colPedido;
    @FXML private TableColumn<Pedido, String> colFecha;
    @FXML private TableColumn<Pedido, String> colEstado;
    @FXML private TableColumn<Pedido, String> colTotal;
    @FXML private TableColumn<Pedido, String> colFactura;

    @FXML
    private void initialize() {
        colPedido.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().numero()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().fecha()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().estado()));
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().total()));
        colFactura.setCellFactory(c -> new TableCell<>() {
            private final Button pdf = new Button("PDF");
            {
                pdf.getStyleClass().add("small-action");
                pdf.setOnAction(e -> FacturaUtil.generar(pdf.getScene().getWindow(),
                        getTableView().getItems().get(getIndex())));
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
                    // El detalle se muestra al seleccionar un pedido sin alterar su estado.
                    fila.setTooltip(new javafx.scene.control.Tooltip(fila.getItem().detalle()));
                }
            });
            return fila;
        });
    }

    public void configurar(ClienteDashboardController dashboard) {
        tabla.setItems(FXCollections.observableArrayList(dashboard.getPedidos()));
    }
}
