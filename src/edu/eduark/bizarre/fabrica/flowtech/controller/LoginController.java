package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.controller.cliente.ClienteDashboardController;
import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.RolUsuario;
import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import edu.eduark.bizarre.fabrica.flowtech.service.UsuarioService;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistrarse;
    @FXML private Label lblMensaje;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnRegistrarse.setOnAction(event -> irAlRegistro());
        btnIniciarSesion.setOnAction(event -> iniciarSesion());
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            mostrarMensaje("Por favor, ingrese correo y contraseña.");
            return;
        }

        try {
            Optional<Usuario> usuario = usuarioService.autenticar(correo, contrasena);
            if (usuario.isPresent()) {
                redirigirPorRol(usuario.get());
            } else {
                mostrarMensaje("Correo o contraseña incorrectos, o el usuario está inactivo.");
            }
        } catch (ServicioException error) {
            mostrarError("No fue posible iniciar sesión", error);
        }
    }

    private void redirigirPorRol(Usuario usuario) {
        switch (usuario.getIdRol()) {
            case 1 -> // ADMIN
                SceneManager.getInstance().changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-admin.fxml",
                    "FlowTech - Dashboard Admin"
                );
            case 2 -> // EMPLEADO
                SceneManager.getInstance().changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-empleado.fxml",
                    "FlowTech - Dashboard Empleado"
                );
            case 3 -> { // CLIENTE
                ClienteDashboardController controlador = SceneManager.getInstance().changeSceneAndGetController(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-cliente-view.fxml",
                    "FlowTech - Dashboard Cliente"
                );
                if (controlador != null) {
                    controlador.configurarUsuario(usuario.getIdUsuario(), usuario.nombreCompleto(), RolUsuario.CLIENTE);
                }
            }
            default -> mostrarMensaje("Rol no reconocido.");
        }
    }

    private void irAlRegistro() {
        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/registro-view.fxml",
            "FlowTech - Crear Cuenta"
        );
    }

    private void mostrarMensaje(String mensaje) {
        if (lblMensaje != null) {
            lblMensaje.setText(mensaje);
        }
    }

    private void mostrarError(String contexto, Exception error) {
        System.err.println(contexto + ": " + error.getMessage());
        Alert alerta = new Alert(AlertType.ERROR, contexto + ".\n" + error.getMessage());
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
