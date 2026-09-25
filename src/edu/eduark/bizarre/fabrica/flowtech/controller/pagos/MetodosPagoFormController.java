package edu.eduark.bizarre.fabrica.flowtech.controller.pagos;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.TarjetaCredito;
import edu.eduark.bizarre.fabrica.flowtech.service.TarjetaCreditoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;


public class MetodosPagoFormController {

    @FXML private ListView<TarjetaCredito> listaTarjetas;
    @FXML private TextField campoTitular;
    @FXML private ComboBox<String> comboTipo;
    @FXML private TextField campoUltimos4;
    @FXML private TextField campoMes;
    @FXML private TextField campoAnio;

    private final ObservableList<TarjetaCredito> items = FXCollections.observableArrayList();
    private final TarjetaCreditoService tarjetaCreditoService = new TarjetaCreditoService();

    private int idUsuarioCliente;

    @FXML
    private void initialize() {
        listaTarjetas.setItems(items);
        listaTarjetas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        comboTipo.setItems(FXCollections.observableArrayList(TarjetaCreditoService.TIPOS));
    }

    /** Llamado por ClientePerfilController antes de mostrar el diálogo. */
    public void configurar(int idUsuarioCliente) {
        this.idUsuarioCliente = idUsuarioCliente;
        cargarTarjetas();
    }

    private void cargarTarjetas() {
        try {
            items.setAll(tarjetaCreditoService.listarPorUsuario(idUsuarioCliente));
        } catch (ServicioException error) {
            mostrarError("El usuario no tine tarjetas registradas.", error);
        }
    }

    @FXML
    private void agregarTarjeta() {
        try {
            tarjetaCreditoService.agregar(
                    idUsuarioCliente,
                    campoTitular.getText(),
                    comboTipo.getValue(),
                    campoUltimos4.getText(),
                    parsearEntero(campoMes.getText(), "mes"),
                    parsearEntero(campoAnio.getText(), "año"));
            limpiarFormulario();
            cargarTarjetas();
        } catch (ServicioException error) {
            mostrarError("No fue posible agregar la tarjeta", error);
        }
    }

    @FXML
    private void marcarPreferida() {
        TarjetaCredito seleccionada = listaTarjetas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            return;
        }
        try {
            tarjetaCreditoService.marcarPreferida(seleccionada.getIdTarjeta(), idUsuarioCliente);
            cargarTarjetas();
        } catch (ServicioException error) {
            mostrarError("No fue posible marcar la tarjeta como preferida", error);
        }
    }

    @FXML
    private void eliminarTarjeta() {
        TarjetaCredito seleccionada = listaTarjetas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            return;
        }
        try {
            tarjetaCreditoService.eliminar(seleccionada.getIdTarjeta(), idUsuarioCliente);
            cargarTarjetas();
        } catch (ServicioException error) {
            mostrarError("No fue posible eliminar la tarjeta", error);
        }
    }

    private int parsearEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto == null ? "" : texto.trim());
        } catch (NumberFormatException error) {
            throw new ServicioException("El campo \"" + campo + "\" debe ser un número.");
        }
    }

    private void limpiarFormulario() {
        campoTitular.clear();
        comboTipo.setValue(null);
        campoUltimos4.clear();
        campoMes.clear();
        campoAnio.clear();
    }

    private void mostrarError(String contexto, Exception error) {
        Alert alerta = new Alert(AlertType.ERROR, contexto + ".\n" + error.getMessage(), ButtonType.OK);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
