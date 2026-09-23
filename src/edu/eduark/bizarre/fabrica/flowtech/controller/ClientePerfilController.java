package edu.eduark.bizarre.fabrica.flowtech.controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Controller de la seccion "Mi perfil" del panel de cliente
 * (vista definida en cliente-perfil-view.fxml).
 *
 * No hay tabla de direcciones en la base de datos, asi que esa parte se
 * elimino. Lo que si se puede hacer sin tocar la base de datos es manejar
 * metodos de pago y preferencias de notificacion como estado de sesion
 * (en memoria, compartido a traves de ClienteDashboardController).
 */
public class ClientePerfilController {

    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblMetodoPreferido;
    @FXML
    private CheckBox chkCorreo;
    @FXML
    private CheckBox chkSms;
    @FXML
    private Label lblEstadoPreferencias;

    private ClienteDashboardController dashboard;

    public void configurar(ClienteDashboardController dashboard) {
        this.dashboard = dashboard;

        lblUsuario.setText("Cliente: " + dashboard.getNombreUsuario());
        chkCorreo.setSelected(dashboard.isNotificarCorreo());
        chkSms.setSelected(dashboard.isNotificarSms());

        actualizarMetodoPreferido();
    }

    private void actualizarMetodoPreferido() {
        List<String> metodos = dashboard.getMetodosPago();
        lblMetodoPreferido.setText(metodos.isEmpty()
                ? "Sin metodos guardados"
                : metodos.get(0) + " · preferido");
    }

    @FXML
    private void cambiarNotificarCorreo() {
        dashboard.setNotificarCorreo(chkCorreo.isSelected());
        lblEstadoPreferencias.setText("Preferencias actualizadas.");
    }

    @FXML
    private void cambiarNotificarSms() {
        dashboard.setNotificarSms(chkSms.isSelected());
        lblEstadoPreferencias.setText("Preferencias actualizadas.");
    }

    @FXML
    private void administrarMetodos() {
        ObservableList<String> items = FXCollections.observableArrayList(dashboard.getMetodosPago());

        ListView<String> lista = new ListView<>(items);
        lista.setPrefHeight(140);
        lista.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TextField campoNuevo = new TextField();
        campoNuevo.setPromptText("Ej: Mastercard terminada en 1234");
        Button btnAgregar = new Button("Agregar");
        Button btnPreferido = new Button("Marcar como preferido");
        Button btnEliminar = new Button("Eliminar");

        btnAgregar.setOnAction(e -> {
            String texto = campoNuevo.getText();
            if (texto != null && !texto.isBlank()) {
                items.add(texto.trim());
                campoNuevo.clear();
            }
        });

        btnPreferido.setOnAction(e -> {
            String seleccionado = lista.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                items.remove(seleccionado);
                items.add(0, seleccionado);
            }
        });

        btnEliminar.setOnAction(e -> {
            String seleccionado = lista.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                items.remove(seleccionado);
            }
        });

        VBox contenido = new VBox(10,
                new Label("Tus metodos de pago (el primero de la lista es el preferido):"),
                lista,
                new HBox(8, campoNuevo, btnAgregar),
                new HBox(8, btnPreferido, btnEliminar));
        contenido.setPadding(new Insets(10));

        Dialog<Void> dialogo = new Dialog<>();
        dialogo.setTitle("Metodos de pago");
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialogo.getDialogPane().setContent(contenido);
        dialogo.showAndWait();

        // Al cerrar el dialogo, guardamos los cambios en el estado compartido.
        dashboard.getMetodosPago().clear();
        dashboard.getMetodosPago().addAll(items);
        actualizarMetodoPreferido();
    }
}
