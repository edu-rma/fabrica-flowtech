package edu.eduark.bizarre.fabrica.flowtech;

import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Le pasamos el escenario principal al SceneManager
        SceneManager.getInstance().setPrimaryStage(primaryStage);
        
        // Le decimos que inicie con la vista de Login
        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/login-view.fxml", 
            "FlowTech - Iniciar Sesión"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}