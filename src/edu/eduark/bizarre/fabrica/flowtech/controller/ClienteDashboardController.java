

package edu.eduark.bizarre.fabrica.flowtech.controller;
 
import javafx.fxml.FXML;
 

public class ClienteDashboardController extends BaseDashboardController {
 
    @FXML
    private void mostrarMisPedidos() {
      
        lblContenidoActual.setText("Módulo de Mis Pedidos (en construcción)");
    }
 
    @FXML
    private void mostrarCatalogo() {

        lblContenidoActual.setText("Módulo de Catálogo (en construcción)");
    }
 
    @FXML
    private void mostrarPerfil() {

        lblContenidoActual.setText("Módulo de Mi Perfil (en construcción)");
    }
}
 
