package edu.eduark.bizarre.fabrica.flowtech.service;

import edu.eduark.bizarre.fabrica.flowtech.exception.ServicioException;
import edu.eduark.bizarre.fabrica.flowtech.model.TarjetaCredito;
import edu.eduark.bizarre.fabrica.flowtech.repository.TarjetaCreditoRepository;

import java.time.Year;
import java.sql.SQLException;
import java.util.List;


public class TarjetaCreditoService {

    public static final List<String> TIPOS = List.of("VISA", "MASTERCARD", "AMEX", "OTRA");

    private final TarjetaCreditoRepository tarjetaCreditoRepository;

    public TarjetaCreditoService() {
        this(new TarjetaCreditoRepository());
    }

    public TarjetaCreditoService(TarjetaCreditoRepository tarjetaCreditoRepository) {
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
    }

    public List<TarjetaCredito> listarPorUsuario(int idUsuario) {
        try {
            return tarjetaCreditoRepository.listarPorUsuario(idUsuario);
        } catch (SQLException error) {
            throw new ServicioException("quieres añadir una nueva tarjeta?", error);
        }
    }

    public int agregar(int idUsuario, String titular, String tipoTarjeta, String ultimos4Digitos,
            int mesExpiracion, int anioExpiracion) {
        validarDatos(titular, tipoTarjeta, ultimos4Digitos, mesExpiracion, anioExpiracion);

        TarjetaCredito tarjeta = new TarjetaCredito();
        tarjeta.setIdUsuario(idUsuario);
        tarjeta.setTitular(titular.trim());
        tarjeta.setTipoTarjeta(tipoTarjeta);
        tarjeta.setUltimos4Digitos(ultimos4Digitos.trim());
        tarjeta.setMesExpiracion(mesExpiracion);
        tarjeta.setAnioExpiracion(anioExpiracion);

        try {
            return tarjetaCreditoRepository.agregar(tarjeta);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible guardar la tarjeta.", error);
        }
    }

    public void marcarPreferida(int idTarjeta, int idUsuario) {
        try {
            tarjetaCreditoRepository.marcarPreferida(idTarjeta, idUsuario);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible marcar la tarjeta como preferida.", error);
        }
    }

    public void eliminar(int idTarjeta, int idUsuario) {
        try {
            tarjetaCreditoRepository.eliminar(idTarjeta, idUsuario);
        } catch (SQLException error) {
            throw new ServicioException("No fue posible eliminar la tarjeta.", error);
        }
    }

    private void validarDatos(String titular, String tipoTarjeta, String ultimos4Digitos,
            int mesExpiracion, int anioExpiracion) {
        if (titular == null || titular.isBlank()) {
            throw new ServicioException("El nombre del titular es obligatorio.");
        }
        if (tipoTarjeta == null || !TIPOS.contains(tipoTarjeta)) {
            throw new ServicioException("Selecciona un tipo de tarjeta válido.");
        }
        if (ultimos4Digitos == null || !ultimos4Digitos.trim().matches("\\d{4}")) {
            throw new ServicioException("Los últimos 4 dígitos deben ser exactamente 4 números.");
        }
        if (mesExpiracion < 1 || mesExpiracion > 12) {
            throw new ServicioException("El mes de expiración debe estar entre 1 y 12.");
        }
        int anioActual = Year.now().getValue();
        if (anioExpiracion < anioActual) {
            throw new ServicioException("La tarjeta está vencida; revisa el año de expiración.");
        }
    }
}
