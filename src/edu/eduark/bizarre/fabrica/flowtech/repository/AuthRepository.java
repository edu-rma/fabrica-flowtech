package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AuthRepository {

    private static final String SELECT_BASE =
            "SELECT id_usuario, id_rol, nombre, apellido, email, password_hash, activo, fecha_registro FROM usuarios";

    public Usuario obtenerUsuarioPorCorreo(String correo) {
        String sql = SELECT_BASE + " WHERE email = ? AND activo = TRUE";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario por correo.");
            e.printStackTrace();
            return null;
        }
    }

    public boolean registrarUsuario(int idRol, String nombre, String apellido, String email, String passwordHash) {
        String sql = "INSERT INTO usuarios (id_rol, nombre, apellido, email, password_hash, activo) VALUES (?, ?, ?, ?, ?, TRUE)";

        try (Connection con = DataBaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idRol);
            ps.setString(2, nombre);
            ps.setString(3, apellido);
            ps.setString(4, email);
            ps.setString(5, passwordHash);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario en la base de datos.");
            e.printStackTrace();
            return false;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("id_usuario"),
                rs.getInt("id_rol"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getBoolean("activo"),
                rs.getTimestamp("fecha_registro")
        );
    }
}
