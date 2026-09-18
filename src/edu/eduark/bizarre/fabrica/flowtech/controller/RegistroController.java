package edu.eduark.bizarre.fabrica.flowtech.controller;

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

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellido;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtClave;

    @FXML
    private PasswordField txtConfirmarClave;

    @FXML
    private Label lblMensaje;

    @FXML
    private Button btnRegistrarCliente;

    @FXML
    private Button btnRegistrarEmpleado;

    @FXML
    private Button btnRegresarLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        btnRegresarLogin.setOnAction(event -> volverAlLogin());
        btnRegistrarCliente.setOnAction(event -> registrarCliente());
        btnRegistrarEmpleado.setOnAction(event -> registrarEmpleado());
    }

    private void registrarCliente() {
        if (validarCampos()) {
            lblMensaje.setText("Registrando nuevo cliente: " + txtNombre.getText());
            lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
        }
    }

    private void registrarEmpleado() {
        if (validarCampos()) {
            lblMensaje.setText("Registrando nuevo empleado: " + txtNombre.getText());
            lblMensaje.setStyle("-fx-text-fill: #3498db;");
            
   
        }
    }

 
    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtApellido.getText().isEmpty() || 
            txtEmail.getText().isEmpty() || txtClave.getText().isEmpty() || 
            txtConfirmarClave.getText().isEmpty()) {
            
            lblMensaje.setText("Por favor, llena todos los campos.");
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