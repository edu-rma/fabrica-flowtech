package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.RolUsuario;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML
    private TextField txtCorreo;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;

    @FXML
    private Button btnRegistrarse;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnRegistrarse.setOnAction(event -> irAlRegistro());
        btnIniciarSesion.setOnAction(event -> iniciarSesionDocente());
    }

    private void iniciarSesionDocente() {
        String correo = txtCorreo.getText();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            System.out.println("Por favor, ingrese el correo y contraseña del docente.");
            return;
        }

        // TODO: reemplazar por la consulta real (DAO / MySQL) que valide
        // credenciales y devuelva el RolUsuario del usuario autenticado.
        System.out.println("Iniciando sesión: " + correo);
        String nombreUsuario = correo;
        RolUsuario rolDetectado = RolUsuario.CLIENTE; // placeholder hasta conectar el DAO

        redirigirSegunRol(nombreUsuario, rolDetectado);
    }

    /**
     * Envía al usuario al dashboard correspondiente a su rol, y le pasa sus
     * datos de sesión al controller recién cargado.
     */
    private void redirigirSegunRol(String nombreUsuario, RolUsuario rol) {
        switch (rol) {
            case CLIENTE -> {
                ClienteDashboardController dc = SceneManager.getInstance().changeSceneAndGetController(
                        "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboar-cliente-view.fxml",
                        "FlowTech - Dashboard Cliente");
                dc.configurarUsuario(nombreUsuario, rol);
            }
            case EMPLEADO -> {
                EmpleadoDashboardController dc = SceneManager.getInstance().changeSceneAndGetController(
                        "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboar-empleado-view.fxml",
                        "FlowTech - Dashboard Empleado");
                dc.configurarUsuario(nombreUsuario, rol);
            }
            case ADMINISTRADOR -> {
                AdministradoDashboarController dc = SceneManager.getInstance().changeSceneAndGetController(
                        "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboar-administrador-view.fxml",
                        "FlowTech - Dashboard Administrador");
                dc.configurarUsuario(nombreUsuario, rol);
            }
        }
    }

    private void irAlRegistro() {
        SceneManager.getInstance().changeScene(
                "/edu/eduark/bizarre/fabrica/flowtech/resources/view/registro-view.fxml",
                "FlowTech - Crear Cuenta");
    }
}