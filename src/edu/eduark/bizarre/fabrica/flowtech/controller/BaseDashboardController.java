package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.RolUsuario;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public abstract class BaseDashboardController implements Initializable {

    @FXML
    protected Label lblUsuario;
    @FXML
    protected Label lblRol;
    @FXML
    protected Label lblContenidoActual;
    @FXML
    protected StackPane contentArea;

    protected String nombreUsuario;
    protected RolUsuario rol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void configurarUsuario(String nombreUsuario, RolUsuario rol) {
        this.nombreUsuario = nombreUsuario;
        this.rol = rol;

        lblUsuario.setText(nombreUsuario);
        lblRol.setText(rol.toString());

        mostrarInicio();
    }

    @FXML
    protected void mostrarInicio() {
        lblContenidoActual.setText("Bienvenido a FlowTech, " + nombreUsuario);
    }

    @FXML
    protected void cerrarSesion() {
        SceneManager.getInstance().changeScene(
                "/edu/eduark/bizarre/fabrica/flowtech/resources/view/login-view.fxml",
                "FlowTech - Login");
    }
}