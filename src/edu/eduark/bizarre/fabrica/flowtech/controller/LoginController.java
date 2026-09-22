package edu.eduark.bizarre.fabrica.flowtech.controller;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class LoginController implements Initializable {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistrarse;
    @FXML private Label lblMensaje;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnRegistrarse.setOnAction(event -> irAlRegistro());
        btnIniciarSesion.setOnAction(event -> iniciarSesionDocente());
    }

   private void iniciarSesionDocente() {
        String correo = txtCorreo.getText();
        String contrasena = txtContrasena.getText();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            System.out.println("Por favor, ingrese correo y contraseña.");
            return;
        }

        String sql = "SELECT id_rol, nombre, password_hash FROM usuarios WHERE email = ? AND activo = TRUE";


        try (Connection conn = DataBaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idRol = rs.getInt("id_rol");
                String hashDb = rs.getString("password_hash");
                
                if (contrasena.equals(hashDb)) { 
                    redirigirPorRol(idRol);
                } else {
                    System.out.println("Contraseña incorrecta.");
                }
            } else {
                System.out.println("El usuario no existe o está inactivo.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void redirigirPorRol(int idRol) {
        switch (idRol) {
            case 1: // ADMIN
                SceneManager.getInstance().changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-admin.fxml", 
                    "FlowTech - Dashboard Admin"
                );
                break;
            case 2: // EMPLEADO
                SceneManager.getInstance().changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-empleado.fxml", 
                    "FlowTech - Dashboard Empleado"
                );
                break;
            case 3: // CLIENTE
                SceneManager.getInstance().changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-cliente-view.fxml", 
                    "FlowTech - Dashboard Cliente"
                );
                break;
            default:
                System.out.println("Rol no reconocido.");
        }
    }

    private void irAlRegistro() {
        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/registro-view.fxml", 
            "FlowTech - Crear Cuenta"
        );
    }
}