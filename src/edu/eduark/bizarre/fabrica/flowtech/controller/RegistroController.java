package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.repository.AuthRepository;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.mindrot.jbcrypt.BCrypt;

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

    // Instancia del repositorio
    private AuthRepository authRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authRepository = new AuthRepository(); // Inicializamos el repositorio
        
        btnRegresarLogin.setOnAction(event -> volverAlLogin());
        btnRegistrarUsuario.setOnAction(event -> registrarUsuario());
        txtEmail.textProperty().addListener((observable, anterior, correo) -> actualizarTipoCuenta(correo));
        actualizarTipoCuenta("");
    }

    private void registrarUsuario() {
        if (!validarCampos()) return;

        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String email = txtEmail.getText().trim().toLowerCase();
        String clave = txtClave.getText();
        int idRol = obtenerRolPorCorreo(email);
        String passwordHash = BCrypt.hashpw(clave, BCrypt.gensalt(12));

  
        boolean registroExitoso = authRepository.registrarUsuario(idRol, nombre, apellido, email, passwordHash);

        if (registroExitoso) {
            SceneManager.getInstance().showInformation(
                "Registro Exitoso", 
                "¡El cliente " + nombre + " ha sido registrado correctamente como " + nombreRol(idRol) + "!"
            );
            volverAlLogin();
        } else {
            SceneManager.getInstance().showError(
                "Error al Registrar", 
                "Ocurrió un problema en la base de datos. Es posible que este correo ya esté registrado."
            );
        }
    }

    private int obtenerRolPorCorreo(String correo) {
        if (correo.endsWith("@flowtechad.com")) {
            return 1;
        }
        if (correo.endsWith("@flowtech.com")) {
            return 2;
        }
        return 3;
    }

    private String nombreRol(int idRol) {
        return switch (idRol) {
            case 1 -> "administrador";
            case 2 -> "empleado";
            default -> "cliente"; 
        };
    }

    private void actualizarTipoCuenta(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            lblTipoCuenta.setText("El rol se asignará según el correo agregado.");
            return;
        }
        lblTipoCuenta.setText("Se registrará como: " + nombreRol(obtenerRolPorCorreo(correo.trim().toLowerCase())) + ".");
    }

    private boolean validarCampos() {
        if (txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || 
            txtClave.getText().isEmpty() || txtConfirmarClave.getText().isEmpty()) {
            
            SceneManager.getInstance().showWarning(
                "Campos Incompletos", 
                "Por favor, llena todos los campos obligatorios."
            );
            return false;
        }

        if (!txtEmail.getText().trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            SceneManager.getInstance().showWarning(
                "Correo Inválido", 
                "Ingresa un correo electrónico válido."
            );
            return false;
        }
        
        if (!txtClave.getText().equals(txtConfirmarClave.getText())) {
            SceneManager.getInstance().showWarning(
                "Contraseñas no coinciden", 
                "Las contraseñas ingresadas no son iguales. Verifica e inténtalo de nuevo."
            );
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
