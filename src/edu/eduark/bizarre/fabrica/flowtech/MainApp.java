package edu.eduark.bizarre.fabrica.flowtech;

import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        SceneManager.getInstance().setPrimaryStage(primaryStage);
        

        SceneManager.getInstance().changeScene(
            "/edu/eduark/bizarre/fabrica/flowtech/resources/view/login-view.fxml", 
            "FlowTech - Iniciar Sesión"
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}