    package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
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

public class LoginController implements Initializable {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistrarse;
    @FXML private Label lblMensaje;

    private AuthRepository authRepository;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        authRepository = new AuthRepository(); 
        
        btnRegistrarse.setOnAction(event -> irAlRegistro());
        btnIniciarSesion.setOnAction(event -> iniciarSesionCliente());
    }

    private void iniciarSesionCliente() {
        String correo = txtCorreo.getText().trim();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            SceneManager.getInstance().showWarning("Campos Incompletos", "Debe llenar todos los campos.");
            return;
        }

        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            SceneManager.getInstance().showWarning("Correo Inválido", "Ingrese un correo electrónico válido.");
            return;
        }


        Usuario usuarioDB = authRepository.obtenerUsuarioPorCorreo(correo);

        if (usuarioDB != null) {

            if (coincideContrasena(contrasena, usuarioDB.getPasswordHash())) { 
                SceneManager.getInstance().showInformation("Éxito", "Bienvenido, " + usuarioDB.getNombre());
                

                SceneManager.getInstance().redirigirPorRol(
                    usuarioDB.getIdUsuario(), 
                    usuarioDB.getIdRol(), 
                    usuarioDB.getNombre()
                );
            } else {
                SceneManager.getInstance().showError("Credenciales Inválidas", "La contraseña es incorrecta.");
            }
        } else {
            SceneManager.getInstance().showError("Error de Autenticación", "El cliente no existe o está inactivo.");
        }
    }

    private boolean coincideContrasena(String contrasena, String hashGuardado) {
        try {
            return hashGuardado != null && BCrypt.checkpw(contrasena, hashGuardado);
        } catch (IllegalArgumentException error) {
            System.err.println("El hash almacenado no es BCrypt válido.");
            return false;
        }
    }

    private void irAlRegistro() {
        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/registro-view.fxml", 
            "FlowTech - Crear Cuenta"
        );
    }
}
