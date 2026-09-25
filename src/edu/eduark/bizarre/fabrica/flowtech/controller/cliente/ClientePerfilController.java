package edu.eduark.bizarre.fabrica.flowtech.controller.cliente;

import edu.eduark.bizarre.fabrica.flowtech.controller.pagos.MetodosPagoFormController;

import java.util.List;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.TarjetaCredito;
import edu.eduark.bizarre.fabrica.flowtech.service.TarjetaCreditoService;
import edu.eduark.bizarre.fabrica.flowtech.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;


public class ClientePerfilController {

    private static final String RUTA_FORM_METODOS_PAGO = "/edu/eduark/bizarre/fabrica/flowtech/resources/view/metodos-pago-form-view.fxml";

    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblMetodoPreferido;
    @FXML
    private CheckBox chkCorreo;
    @FXML
    private CheckBox chkSms;
    @FXML
    private Label lblEstadoPreferencias;

    private final TarjetaCreditoService tarjetaCreditoService = new TarjetaCreditoService();

    private ClienteDashboardController dashboard;

    public void configurar(ClienteDashboardController dashboard) {
        this.dashboard = dashboard;

        lblUsuario.setText("Cliente: " + dashboard.getNombreUsuario());
        chkCorreo.setSelected(dashboard.isNotificarCorreo());
        chkSms.setSelected(dashboard.isNotificarSms());
        actualizarMetodoPreferido();
    }

    private void actualizarMetodoPreferido() {
        try {
            List<TarjetaCredito> tarjetas = tarjetaCreditoService.listarPorUsuario(dashboard.getIdUsuarioCliente());
            lblMetodoPreferido.setText(tarjetas.isEmpty()
                    ? "No tienes métodos de pago guardados"
                    : tarjetas.get(0).etiqueta());
        } catch (ServicioException error) {
            lblMetodoPreferido.setText("No fue posible cargar tus métodos de pago");
        }
    }

    @FXML
    private void cambiarNotificarCorreo() {
        dashboard.setNotificarCorreo(chkCorreo.isSelected());
        lblEstadoPreferencias.setText("Preferencias actualizadas.");
    }

    @FXML
    private void cambiarNotificarSms() {
        dashboard.setNotificarSms(chkSms.isSelected());
        lblEstadoPreferencias.setText("Preferencias actualizadas.");
    }

    @FXML
    private void administrarMetodos() {
        Dialog<Void> dialogo = new Dialog<>();
        dialogo.setTitle("Métodos de pago");
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        MetodosPagoFormController controlador = SceneManager.getInstance()
                .cargarEnDialogo(dialogo, RUTA_FORM_METODOS_PAGO);
        controlador.configurar(dashboard.getIdUsuarioCliente());
        dialogo.showAndWait();

        actualizarMetodoPreferido();
    }

}
