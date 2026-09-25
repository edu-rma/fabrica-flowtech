package edu.eduark.bizarre.fabrica.flowtech.utils;

import edu.eduark.bizarre.fabrica.flowtech.controller.cliente.ClienteDashboardController;
import edu.eduark.bizarre.fabrica.flowtech.model.RolUsuario;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SceneManager {

    private static SceneManager instance;

    private Stage primaryStage;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public void changeScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public <T> T changeSceneAndGetController(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

            return loader.getController();
        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }

   
    public <T> T cargarEnContenedor(String fxmlPath, Pane contenedor) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();

            contenedor.getChildren().setAll(vista);

            return loader.getController();
        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }


    public <T> T cargarEnDialogo(Dialog<?> dialogo, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent contenido = loader.load();

            dialogo.getDialogPane().setContent(contenido);

            return loader.getController();
        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + fxmlPath);
            e.printStackTrace();
            return null;
        }

    }
    
        public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }
        alert.showAndWait();
    }

    public void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, null, message);
    }

    public void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, null, message);
    }

    public void showInformation(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, null, message);
    }

 
    public void redirigirPorRol(int idUsuario, int idRol, String nombreUsuario) {
        switch (idRol) {
            case 1:
                changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-admin.fxml", 
                    "FlowTech - Dashboard Admin"
                );
                break;
            case 2:
                changeScene(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-empleado.fxml", 
                    "FlowTech - Dashboard Empleado"
                );
                break;
            case 3:
                ClienteDashboardController controlador = changeSceneAndGetController(
                    "/edu/eduark/bizarre/fabrica/flowtech/resources/view/dashboard-cliente-view.fxml", 
                    "FlowTech - Dashboard Cliente"
                );
                if (controlador != null) {
                    controlador.configurarUsuario(idUsuario, nombreUsuario, RolUsuario.CLIENTE);
                }
                break;
            default:
                showError("Rol Desconocido", "El rol asignado a este usuario no es reconocido por el sistema.");
        }
    }
}

