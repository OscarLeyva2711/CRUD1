import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Metodos {

    private Connection con;

    public Metodos(Connection con) {
        this.con = con;
    }


    //Cierra la conexión a la base de datos
    public void cerrarConexion() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Conexión cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }

    // ========== PERSONAS ==========

    /**
     * Agrega una nueva persona y retorna su ID generado
     */
    public int agregarPersona(String nombre) throws SQLException {
        String sql = "INSERT INTO Personas (nombre) VALUES (?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la persona creada");
                }
            }
        }
    }

    //Edita solo el nombre de una persona
    public void editarPersona(int id, String nuevoNombre) throws SQLException {
        String sql = "UPDATE Personas SET nombre = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoNombre);
            ps.setInt(2, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la persona con ID: " + id);
            }
        }
    }

    //Elimina una persona y todos sus datos relacionados (direcciones y telefonos)
    public void eliminarPersona(int id) throws SQLException {
        try {
            con.setAutoCommit(false);

            // Eliminar direcciones
            String sqlDirecciones = "DELETE FROM direcciones WHERE personaId = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlDirecciones)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Eliminar telefonos
            String sqlTelefonos = "DELETE FROM Telefonos WHERE personaId = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlTelefonos)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // Eliminar persona
            String sqlPersona = "DELETE FROM Personas WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sqlPersona)) {
                ps.setInt(1, id);
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas == 0) {
                    throw new SQLException("No se encontró la persona con ID: " + id);
                }
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    //Lista todas las personas (solo datos básicos)
    public List<Persona> listarPersonas() throws SQLException {
        List<Persona> personas = new ArrayList<>();
        String sql = "SELECT * FROM Personas ORDER BY nombre";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                personas.add(new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre")
                ));
            }
        }
        return personas;
    }

    // ========== DIRECCIONES ==========

    //Agrega una dirección a una persona
    public void agregarDireccion(int personaId, String direccion) throws SQLException {
        String sql = "INSERT INTO direcciones (personaId, direccion) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ps.setString(2, direccion);
            ps.executeUpdate();
        }
    }

    //Edita una dirección específica por su ID
    public void editarDireccion(int id, String nuevaDireccion) throws SQLException {
        String sql = "UPDATE direcciones SET direccion = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevaDireccion);
            ps.setInt(2, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la dirección con ID: " + id);
            }
        }
    }

    //Elimina una direccion específica por su ID
    public void eliminarDireccion(int id) throws SQLException {
        String sql = "DELETE FROM direcciones WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró la dirección con ID: " + id);
            }
        }
    }

    //Elimina todas las direcciones de una persona
    public void eliminarTodasDireccionesDePersona(int personaId) throws SQLException {
        String sql = "DELETE FROM direcciones WHERE personaId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }
    }

    //Lista todas las direcciones de una persona
    public List<Direccion> listarDireccionesDePersona(int personaId) throws SQLException {
        List<Direccion> direcciones = new ArrayList<>();
        String sql = "SELECT * FROM direcciones WHERE personaId = ? ORDER BY id";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    direcciones.add(new Direccion(
                            rs.getInt("id"),
                            rs.getInt("personaId"),
                            rs.getString("direccion")
                    ));
                }
            }
        }
        return direcciones;
    }

    // ========== TELEFONOS ==========


    // Agrega un teléfono a una persona
    public void agregarTelefono(int personaId, String telefono) throws SQLException {
        // Validacion
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new SQLException("El teléfono no puede estar vacío");
        }

        // Limpiar el teléfono
        String telefonoLimpio = telefono.replaceAll("[^0-9+\\-\\s()]", "");

        String sql = "INSERT INTO Telefonos (personaId, telefono) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ps.setString(2, telefonoLimpio);
            ps.executeUpdate();
        }
    }


    // Edita un teléfono específico por su ID
    public void editarTelefono(int id, String nuevoTelefono) throws SQLException {
        // Limpiar el teléfono
        String telefonoLimpio = nuevoTelefono.replaceAll("[^0-9+\\-\\s()]", "");

        String sql = "UPDATE Telefonos SET telefono = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telefonoLimpio);
            ps.setInt(2, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el teléfono con ID: " + id);
            }
        }
    }


     //Elimina un teléfono específico por ID
    public void eliminarTelefono(int id) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas == 0) {
                throw new SQLException("No se encontró el teléfono con ID: " + id);
            }
        }
    }


    //Elimina todos los teléfonos de una persona
    public void eliminarTodosTelefonosDePersona(int personaId) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE personaId = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, personaId);
            ps.executeUpdate();
        }
    }


    // Lista todos los telefonos de una persona
    public List<Telefono> listarTelefonosDePersona(int personaId) throws SQLException {
        List<Telefono> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM Telefonos WHERE personaId = ? ORDER BY id";
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