package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.repository.UsuarioRepository;
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
    
    @FXML private Button btnRegistrarCliente;
    @FXML private Button btnRegistrarEmpleado;
    @FXML private Button btnRegresarLogin;

    // Instancia del nuevo repositorio de usuarios
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnRegresarLogin.setOnAction(event -> volverAlLogin());

        btnRegistrarCliente.setOnAction(event -> registrarUsuario(3));
        btnRegistrarEmpleado.setOnAction(event -> registrarUsuario(2));
    }

    private void registrarUsuario(int idRol) {
        if (!validarCampos()) return;

        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText();
        String clave = txtClave.getText();

        // Llamada limpia a la capa Repository
        boolean exito = usuarioRepository.registrar(idRol, nombre, apellido, email, clave);

        if (exito) {
            lblMensaje.setText("¡Registro exitoso!");
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
        } else {
            lblMensaje.setText("Error al registrar.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtClave.getText().isEmpty()) {
            lblMensaje.setText("Por favor, llena todos los campos obligatorios.");
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