import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private Connection conn;

    public PersonaDAO(Connection conn) {
        this.conn = conn;
    }

    public Persona agregar(Persona p) throws SQLException {
        String sql = "INSERT INTO persona(nombre) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));
        }
        return p;
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM persona WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void editar(int id, String nuevoNombre) throws SQLException {
        String sql = "UPDATE persona SET nombre=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<Persona> listar() throws SQLException {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM persona";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                personas.add(new Persona(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return personas;
    }
}