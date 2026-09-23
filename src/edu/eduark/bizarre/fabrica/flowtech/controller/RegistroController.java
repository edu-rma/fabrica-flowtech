package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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

        String sql = "INSERT INTO usuarios (id_rol, nombre, apellido, email, password_hash, activo) VALUES (?, ?, ?, ?, ?, TRUE)";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idRol);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellido);
            pstmt.setString(4, email);
            pstmt.setString(5, passwordHash);

            int filasInsertadas = pstmt.executeUpdate();
            
            if (filasInsertadas > 0) {
                lblMensaje.setText("¡Registro exitoso como " + nombreRol(idRol) + "!");
                lblMensaje.setStyle("-fx-text-fill: #2ecc71;");
            }

        } catch (SQLException e) {
            lblMensaje.setText("Error al registrar.");
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
            e.printStackTrace();
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
            lblTipoCuenta.setText("El rol se asignará según el correo institucional.");
            return;
        }
        lblTipoCuenta.setText("Se registrará como: " + nombreRol(obtenerRolPorCorreo(correo.trim().toLowerCase())) + ".");
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
