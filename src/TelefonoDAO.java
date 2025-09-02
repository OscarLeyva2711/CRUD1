import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelefonoDAO {

    private final Connection conn;

    public TelefonoDAO(Connection conn) {
        this.conn = conn;
    }

    public void agregar(Telefono telefono) throws SQLException {
        String sql = "INSERT INTO telefono (persona_id, numero) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, telefono.getPersonaId());
            ps.setString(2, telefono.getNumero());
            ps.executeUpdate();
        }
    }

    public void editar(int id, String nuevoNumero) throws SQLException {
        String sql = "UPDATE telefono SET numero=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoNumero);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM telefono WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Telefono> listarPorPersona(int personaId) throws SQLException {
        List<Telefono> lista = new ArrayList<>();
        String sql = "SELECT * FROM telefono WHERE persona_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Telefono(
                            rs.getInt("id"),
                            rs.getInt("persona_id"),
                            rs.getString("numero")
                    ));
                }
            }
        }
        return lista;
    }
}