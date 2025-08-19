import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class Metodos {

    private Connection con;

    public Metodos(Connection con) {
        this.con = con;
    }

    public void agregarPersona(String nombre, String direccion) throws SQLException {
        String sql = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.executeUpdate();
        }
    }

    public void editarPersona(int id, String nuevoNombre, String nuevaDireccion) throws SQLException {
        String sql = "UPDATE Personas SET nombre=?, direccion=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevaDireccion);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void eliminarPersona(int id) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Persona> listarPersonas() throws SQLException {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM Personas";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                personas.add(new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion")
                ));
            }
        }
        return personas;
    }

    // ========== TELEFONOS ==========

    public void agregarTelefono(int personaId, String telefono) throws SQLException {
        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ps.setString(2, telefono);
            ps.executeUpdate();
        }
    }

    public void editarTelefono(int id, String nuevoTelefono) throws SQLException {
        String sql = "UPDATE Telefonos SET telefono=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoTelefono);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void eliminarTelefono(int id) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Telefono> listarTelefonosDePersona(int personaId) throws SQLException {
        List<Telefono> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM Telefonos WHERE personaId=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    telefonos.add(new Telefono(
                            rs.getInt("id"),
                            rs.getInt("personaId"),
                            rs.getString("telefono")
                    ));
                }
            }
        }
        return telefonos;
    }

}
