package edu.eduark.bizarre.fabrica.flowtech.controller.pedido;

import edu.eduark.bizarre.fabrica.flowtech.model.LineaPedido;
import edu.eduark.bizarre.fabrica.flowtech.model.ProductoOpcion;
import edu.eduark.bizarre.fabrica.flowtech.model.UsuarioOpcion;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


public class PedidoFormController {

    @FXML private ComboBox<UsuarioOpcion> cmbCliente;
    @FXML private ComboBox<UsuarioOpcion> cmbEmpleado;
    @FXML private ComboBox<ProductoOpcion> cmbProducto;
    @FXML private Spinner<Integer> spnCantidad;
    @FXML private TableView<LineaPedido> tablaLineas;
    @FXML private TableColumn<LineaPedido, String> colLineaProducto;
    @FXML private TableColumn<LineaPedido, Number> colLineaCantidad;
    @FXML private TableColumn<LineaPedido, Void> colLineaQuitar;

    private final ObservableList<LineaPedido> lineas = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // 1. Configurar la fábrica de valores del Spinner (min: 1, max: 100, inicial: 1)
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        spnCantidad.setValueFactory(valueFactory);

        colLineaProducto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().producto().nombre()));
        colLineaCantidad.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().cantidad()));
        
        // 2. Corrección de la celda del botón "Quitar" usando getTableRow()
        colLineaQuitar.setCellFactory(columna -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");
            {
                btnQuitar.getStyleClass().add("remove-cart-button");
                btnQuitar.setOnAction(e -> {
                    LineaPedido lineaActual = getTableView().getItems().get(getIndex());
                    lineas.remove(lineaActual);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : btnQuitar);
            }
        });
        
        tablaLineas.setItems(lineas);
    }

    public void configurar(List<UsuarioOpcion> clientes, List<UsuarioOpcion> empleados, List<ProductoOpcion> productos) {
        cmbCliente.setItems(FXCollections.observableArrayList(clientes));
        cmbCliente.getSelectionModel().selectFirst();
        cmbEmpleado.setItems(FXCollections.observableArrayList(empleados));
        cmbEmpleado.getSelectionModel().selectFirst();
        cmbProducto.setItems(FXCollections.observableArrayList(productos));
        cmbProducto.getSelectionModel().selectFirst();
    }

    @FXML
    private void agregarLinea() {
        ProductoOpcion seleccionado = cmbProducto.getValue();
        if (seleccionado == null) {
            return;
        }

        // 3. Obtención segura del valor del Spinner con fallback a 1
        Integer cantidadValor = spnCantidad.getValue();
        int cantidad = (cantidadValor != null) ? cantidadValor : 1;

        boolean yaExiste = lineas.stream().anyMatch(linea -> linea.producto().id() == seleccionado.id());
        if (yaExiste) {
            Alert alerta = new Alert(AlertType.INFORMATION,
                    "Ese producto ya está en el pedido. Quítalo primero si deseas cambiar la cantidad.", ButtonType.OK);
            alerta.setTitle("Producto repetido");
            alerta.setHeaderText(null);
            alerta.showAndWait();
            return;
        }

        lineas.add(new LineaPedido(seleccionado, cantidad));
    }

    public UsuarioOpcion getCliente() {
        return cmbCliente.getValue();
    }

    public UsuarioOpcion getEmpleado() {
        return cmbEmpleado.getValue();
    }

    public List<LineaPedido> getLineas() {
        return new ArrayList<>(lineas);
    }
}