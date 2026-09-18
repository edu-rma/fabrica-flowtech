package edu.eduark.bizarre.fabrica.flowtech.controller;

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
        // Enlazamos los clics de los botones con sus respectivos métodos
        btnRegistrarse.setOnAction(event -> irAlRegistro());
        btnIniciarSesion.setOnAction(event -> iniciarSesionDocente());
    }

    private void iniciarSesionDocente() {
        String correo = txtCorreo.getText();
        String contrasena = txtContrasena.getText();

        // Validación básica de campos vacíos
        if (correo.isEmpty() || contrasena.isEmpty()) {
            System.out.println("Por favor, ingrese el correo y contraseña del docente.");
        } else {
            // Aquí iría tu lógica (ej. DAO o conexión a MySQL) para buscar al docente
            System.out.println("Iniciando sesión como docente: " + correo);
            
            // Si el login es exitoso, usarías el SceneManager para ir al menú principal:
            // SceneManager.getInstance().changeScene("/edu/eduark/bizarre/fabrica/flowtech/resources/view/menu-principal.fxml", "FlowTech - Menú Principal");
        }
    }

    private void irAlRegistro() {

        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/registro-view.fxml", 
            "FlowTech - Crear Cuenta"
        );
    }
}