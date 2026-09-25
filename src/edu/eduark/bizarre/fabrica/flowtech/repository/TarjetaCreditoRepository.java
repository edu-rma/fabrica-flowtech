package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.TarjetaCredito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TarjetaCreditoRepository {

    private static final String SELECT_BASE = "SELECT id_tarjeta, id_usuario, titular, tipo_tarjeta, "
            + "ultimos_4_digitos, token_pago, mes_expiracion, anio_expiracion, predeterminada, activa, fecha_registro "
            + "FROM tarjetas_credito ";


    public List<TarjetaCredito> listarPorUsuario(int idUsuario) throws SQLException {
        String sql = SELECT_BASE + "WHERE id_usuario = ? AND activa = TRUE "
                + "ORDER BY predeterminada DESC, fecha_registro DESC";
        List<TarjetaCredito> tarjetas = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    tarjetas.add(mapear(resultado));
                }
            }
        }
        return tarjetas;
    }

    /** true si el usuario ya tiene al menos una tarjeta activa registrada. */
    public boolean tieneTarjetas(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tarjetas_credito WHERE id_usuario = ? AND activa = TRUE";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        }
    }

   
    public int agregar(TarjetaCredito tarjeta) throws SQLException {
        String insertar = "INSERT INTO tarjetas_credito "
                + "(id_usuario, titular, tipo_tarjeta, ultimos_4_digitos, token_pago, "
                + "mes_expiracion, anio_expiracion, predeterminada) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = DataBaseConnection.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                boolean primeraTarjeta = !tieneTarjetas(tarjeta.getIdUsuario());

                int idTarjeta;
                try (PreparedStatement sentencia = conexion.prepareStatement(insertar, Statement.RETURN_GENERATED_KEYS)) {
                    sentencia.setInt(1, tarjeta.getIdUsuario());
                    sentencia.setString(2, tarjeta.getTitular());
                    sentencia.setString(3, tarjeta.getTipoTarjeta());
                    sentencia.setString(4, tarjeta.getUltimos4Digitos());
                    sentencia.setString(5, "tok_" + UUID.randomUUID());
                    sentencia.setInt(6, tarjeta.getMesExpiracion());
                    sentencia.setInt(7, tarjeta.getAnioExpiracion());
                    sentencia.setBoolean(8, primeraTarjeta);
                    sentencia.executeUpdate();
                    try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                        if (!llaves.next()) {
                            throw new SQLException("No fue posible obtener el identificador de la tarjeta.");
                        }
                        idTarjeta = llaves.getInt(1);
                    }
                }

                conexion.commit();
                return idTarjeta;
            } catch (SQLException error) {
                conexion.rollback();
                throw error;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }

    /** Marca una tarjeta como preferida y desmarca las demás del mismo usuario. */
    public void marcarPreferida(int idTarjeta, int idUsuario) throws SQLException {
        String quitarPreferida = "UPDATE tarjetas_credito SET predeterminada = FALSE WHERE id_usuario = ?";
        String ponerPreferida = "UPDATE tarjetas_credito SET predeterminada = TRUE "
                + "WHERE id_tarjeta = ? AND id_usuario = ?";

        try (Connection conexion = DataBaseConnection.getConnection()) {
            conexion.setAutoCommit(false);
            try {
                try (PreparedStatement sentencia = conexion.prepareStatement(quitarPreferida)) {
                    sentencia.setInt(1, idUsuario);
                    sentencia.executeUpdate();
                }
                try (PreparedStatement sentencia = conexion.prepareStatement(ponerPreferida)) {
                    sentencia.setInt(1, idTarjeta);
                    sentencia.setInt(2, idUsuario);
                    sentencia.executeUpdate();
                }
                conexion.commit();
            } catch (SQLException error) {
                conexion.rollback();
                throw error;
            } finally {
                conexion.setAutoCommit(true);
            }
        }
    }


    public void eliminar(int idTarjeta, int idUsuario) throws SQLException {
        String sql = "UPDATE tarjetas_credito SET activa = FALSE WHERE id_tarjeta = ? AND id_usuario = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setInt(1, idTarjeta);
            sentencia.setInt(2, idUsuario);
            sentencia.executeUpdate();
        }
    }

    private TarjetaCredito mapear(ResultSet resultado) throws SQLException {
        return new TarjetaCredito(
                resultado.getInt("id_tarjeta"),
                resultado.getInt("id_usuario"),
                resultado.getString("titular"),
                resultado.getString("tipo_tarjeta"),
                resultado.getString("ultimos_4_digitos"),
                resultado.getString("token_pago"),
                resultado.getInt("mes_expiracion"),
                resultado.getInt("anio_expiracion"),
                resultado.getBoolean("predeterminada"),
                resultado.getBoolean("activa"),
                resultado.getTimestamp("fecha_registro"));
    }
}
