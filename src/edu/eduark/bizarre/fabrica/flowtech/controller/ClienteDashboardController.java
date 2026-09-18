

package edu.eduark.bizarre.fabrica.flowtech.controller;
 
import javafx.fxml.FXML;
 
/**
 * Controller del dashboard para usuarios con rol CLIENTE.
 * Vista asociada: dashboar-cliente-view.fxml
 */
public class ClienteDashboardController extends BaseDashboardController {
 
    @FXML
    private void mostrarMisPedidos() {
        // TODO: cargar mis-pedidos-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Mis Pedidos (en construcción)");
    }
 
    @FXML
    private void mostrarCatalogo() {
        // TODO: cargar catalogo-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Catálogo (en construcción)");
    }
 
    @FXML
    private void mostrarPerfil() {
        // TODO: cargar perfil-view.fxml real dentro de contentArea cuando exista
        lblContenidoActual.setText("Módulo de Mi Perfil (en construcción)");
    }
}
 
