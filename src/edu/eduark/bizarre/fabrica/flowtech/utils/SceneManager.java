package edu.eduark.bizarre.fabrica.flowtech.utils;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    /**
     * Igual que changeScene, pero devuelve el controller de la vista cargada
     * para poder pasarle datos justo después (por ejemplo, el usuario y el
     * rol que acaban de iniciar sesión, hacia DashboardController).
     */
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
}