import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DireccionDAO {

    private final Connection conn;

    public DireccionDAO(Connection conn) {
        this.conn = conn;
    }

    public void agregar(Direccion direccion) throws SQLException {
        String sql = "INSERT INTO direccion (persona_id, descripcion) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, direccion.getPersonaId());
            ps.setString(2, direccion.getDescripcion());
            ps.executeUpdate();
        }
    }

    public void editar(int id, String nuevaDescripcion) throws SQLException {
        String sql = "UPDATE direccion SET descripcion=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaDescripcion);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM direccion WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Direccion> listarPorPersona(int personaId) throws SQLException {
        List<Direccion> lista = new ArrayList<>();
        String sql = "SELECT * FROM direccion WHERE persona_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Direccion(
                            rs.getInt("id"),
                            rs.getInt("persona_id"),
                            rs.getString("descripcion")
                    ));
                }
            }
        }
        return lista;
    }
}