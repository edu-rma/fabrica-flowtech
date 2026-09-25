package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.EmpleadoResumen;
import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import edu.eduark.bizarre.fabrica.flowtech.model.UsuarioOpcion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class UsuarioRepository {

    private static final String SELECT_BASE =
            "SELECT id_usuario, id_rol, nombre, apellido, email, password_hash, activo, fecha_registro FROM usuarios";

    public Optional<Usuario> buscarPorEmailActivo(String email) throws SQLException {
        String sql = SELECT_BASE + " WHERE email = ? AND activo = TRUE";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, email);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    public int registrar(int idRol, String nombre, String apellido, String email, String passwordHash) throws SQLException {
        String sql = "INSERT INTO usuarios (id_rol, nombre, apellido, email, password_hash, activo) "
                + "VALUES (?, ?, ?, ?, ?, TRUE)";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setInt(1, idRol);
            sentencia.setString(2, nombre);
            sentencia.setString(3, apellido);
            sentencia.setString(4, email);
            sentencia.setString(5, passwordHash);
            sentencia.executeUpdate();
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                return llaves.next() ? llaves.getInt(1) : 0;
            }
        }
    }

    public List<EmpleadoResumen> listarEmpleados() throws SQLException {
        String sql = "SELECT u.id_usuario, TRIM(CONCAT(u.nombre, ' ', COALESCE(u.apellido, ''))) AS nombre_completo, "
                + "u.email, u.activo, DATE_FORMAT(u.fecha_registro, '%d/%m/%Y') AS fecha_registro "
                + "FROM usuarios u INNER JOIN roles r ON r.id_rol = u.id_rol "
                + "WHERE r.nombre = 'EMPLEADO' ORDER BY u.nombre";
        List<EmpleadoResumen> empleados = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                empleados.add(new EmpleadoResumen(resultado.getInt("id_usuario"), resultado.getString("nombre_completo"),
                        resultado.getString("email"), resultado.getBoolean("activo"), resultado.getString("fecha_registro")));
            }
        }
        return empleados;
    }

    public void actualizarEstado(int idUsuario, boolean activo) throws SQLException {
        String sql = "UPDATE usuarios SET activo = ? WHERE id_usuario = ?";
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setBoolean(1, activo);
            sentencia.setInt(2, idUsuario);
            sentencia.executeUpdate();
        }
    }

    public List<UsuarioOpcion> listarActivosPorRol(String rol) throws SQLException {
        String sql = "SELECT u.id_usuario, TRIM(CONCAT(u.nombre, ' ', COALESCE(u.apellido, ''))) AS nombre_completo "
                + "FROM usuarios u INNER JOIN roles r ON r.id_rol = u.id_rol "
                + "WHERE r.nombre = ? AND u.activo = TRUE ORDER BY u.nombre";
        List<UsuarioOpcion> usuarios = new ArrayList<>();
        try (Connection conexion = DataBaseConnection.getConnection();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, rol);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    usuarios.add(new UsuarioOpcion(resultado.getInt("id_usuario"), resultado.getString("nombre_completo")));
                }
            }
        }
        return usuarios;
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        return new Usuario(resultado.getInt("id_usuario"), resultado.getInt("id_rol"), resultado.getString("nombre"),
                resultado.getString("apellido"), resultado.getString("email"), resultado.getString("password_hash"),
                resultado.getBoolean("activo"), resultado.getTimestamp("fecha_registro"));
    }
}
