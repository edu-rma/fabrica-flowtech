package edu.eduark.bizarre.fabrica.flowtech.utils;

import edu.eduark.bizarre.fabrica.flowtech.model.Pedido;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public final class FacturaUtil {

    private FacturaUtil() {
    }

    public static void generar(Window ventana, Pedido pedido) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar comprobante");
        selector.setInitialFileName("Factura_" + pedido.numero().replace("#", "") + ".txt");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo de texto (*.txt)", "*.txt"));

        File archivo = selector.showSaveDialog(ventana);
        if (archivo == null) {
            return; // el usuario cerró o canceló el cuadro de diálogo
        }

        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write(construirContenido(pedido));
            mostrarAviso(Alert.AlertType.INFORMATION, "Comprobante generado",
                    "El comprobante de " + pedido.numero() + " se guardó en:\n" + archivo.getAbsolutePath());
        } catch (IOException e) {
            mostrarAviso(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo generar el comprobante: " + e.getMessage());
        }
    }

    private static String construirContenido(Pedido pedido) {
        String fechaGeneracion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return """
                FABRICA FLOWTECH
                Comprobante de pedido
                ------------------------------------------
                Pedido:        %s
                Fecha pedido:  %s
                Estado:        %s
                Detalle:       %s
                Total:         %s
                ------------------------------------------
                Generado el:   %s
                """.formatted(pedido.numero(), pedido.fecha(), pedido.estado(),
                pedido.detalle(), pedido.total(), fechaGeneracion);
    }

    private static void mostrarAviso(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
