package edu.eduark.bizarre.fabrica.flowtech.controller.pedido;

import edu.eduark.bizarre.fabrica.flowtech.model.PedidoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.UsuarioOpcion;
import edu.eduark.bizarre.fabrica.flowtech.service.PedidoService;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class PedidoEditarFormController {

    @FXML private Label lblCliente;
    @FXML private ComboBox<UsuarioOpcion> cmbEmpleado;
    @FXML private ComboBox<String> cmbEstado;

    public void configurar(PedidoResumen fila, List<UsuarioOpcion> empleadosActivos) {
        lblCliente.setText(fila.cliente());

        cmbEstado.setItems(FXCollections.observableArrayList(PedidoService.ESTADOS));
        cmbEstado.getSelectionModel().select(fila.estado());

        cmbEmpleado.setItems(FXCollections.observableArrayList(empleadosActivos));
        empleadosActivos.stream().filter(op -> op.nombre().equals(fila.empleado())).findFirst()
                .ifPresentOrElse(cmbEmpleado.getSelectionModel()::select, cmbEmpleado.getSelectionModel()::selectFirst);
    }

    public String getEstado() {
        return cmbEstado.getValue();
    }

    public UsuarioOpcion getEmpleado() {
        return cmbEmpleado.getValue();
    }
}
