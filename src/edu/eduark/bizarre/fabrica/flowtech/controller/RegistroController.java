package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.service.UsuarioService;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistroController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtClave;
    @FXML private PasswordField txtConfirmarClave;
    @FXML private Label lblMensaje;
    @FXML private Label lblTipoCuenta;

    @FXML private Button btnRegistrarUsuario;
    @FXML private Button btnRegresarLogin;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnRegresarLogin.setOnAction(event -> volverAlLogin());
        btnRegistrarUsuario.setOnAction(event -> registrarUsuario());
        txtEmail.textProperty().addListener((observable, anterior, correo) -> actualizarTipoCuenta(correo));
        actualizarTipoCuenta("");
    }

    private void registrarUsuario() {
        if (!validarCampos()) {
            return;
        }

        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText().trim().toLowerCase();
        String clave = txtClave.getText();

        try {
            usuarioService.registrar(nombre, apellido, email, clave);
            int idRol = usuarioService.rolPorCorreo(email);
            lblMensaje.setText("¡Registro exitoso como " + usuarioService.nombreRol(idRol) + "!");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
        } catch (ServicioException error) {
            lblMensaje.setText(error.getMessage() != null ? error.getMessage() : "Error al registrar.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private void actualizarTipoCuenta(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            lblTipoCuenta.setText("El rol se asignará según el correo institucional.");
            return;
        }
        int idRol = usuarioService.rolPorCorreo(correo.trim().toLowerCase());
        lblTipoCuenta.setText("Se registrará como: " + usuarioService.nombreRol(idRol) + ".");
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtClave.getText().isEmpty()) {
            lblMensaje.setText("Por favor, llena todos los campos obligatorios.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        if (!txtEmail.getText().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            lblMensaje.setText("Ingresa un correo electrónico válido.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        if (!txtClave.getText().equals(txtConfirmarClave.getText())) {
            lblMensaje.setText("Las contraseñas no coinciden.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            return false;
        }

        return true;
    }

    private void volverAlLogin() {
        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/login-view.fxml",
            "FlowTech - Iniciar Sesión"
        );
    }
}
