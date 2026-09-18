
package edu.eduark.bizarre.fabrica.flowtech.repository;

import edu.eduark.bizarre.fabrica.flowtech.model.Usuario;
import edu.eduark.bizarre.fabrica.flowtech.config.DataBaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepository {
    
   
    public Usuario buscarPorUsuario(String nombreUsuario) {
        String sql = "SELECT id_usuario, nombre, usuario, clave, rol, estado, fecha_creacion FROM usuario WHERE usuario = ?";
        
        try (Connection con = DataBaseConnection.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombreUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("clave"),
                        rs.getString("rol"),
                        rs.getString("estado"),
                        rs.getTimestamp("fecha_creacion")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
