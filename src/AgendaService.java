import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AgendaService {

    private final PersonaDAO personaDAO;
    private final TelefonoDAO telefonoDAO;
    private final DireccionDAO direccionDAO;

    public AgendaService(Connection conn) throws SQLException {
        this.personaDAO = new PersonaDAO(conn);
        this.telefonoDAO = new TelefonoDAO(conn);
        this.direccionDAO = new DireccionDAO(conn);
    }
    public AgendaService() {
        try {
            Connection conn = ConexionDB.getConnection();
            this.personaDAO = new PersonaDAO(conn);
            this.telefonoDAO = new TelefonoDAO(conn);
            this.direccionDAO = new DireccionDAO(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Error conectando a la BD", e);
        }
    }


    // --- PERSONA ---
    public Persona agregarPersona(String nombre) {
        try {
            return personaDAO.agregar(new Persona(0, nombre));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void editarPersona(int id, String nuevoNombre) {
        try {
            personaDAO.editar(id, nuevoNombre);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarPersona(int id) {
        try {
            personaDAO.eliminar(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Persona> listarPersonas() {
        try {
            return personaDAO.listar();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- TELEFONO ---
    public void agregarTelefono(Telefono t) {
        try {
            telefonoDAO.agregar(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editarTelefono(int idTelefono, String nuevoTelefono) {
        try {
            telefonoDAO.editar(idTelefono, nuevoTelefono);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarTelefono(int idTelefono) {
        try {
            telefonoDAO.eliminar(idTelefono);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Telefono> listarTelefonos(int personaId) {
        try {
            return telefonoDAO.listarPorPersona(personaId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // --- DIRECCION ---
    public void agregarDireccion(Direccion d) {
        try {
            direccionDAO.agregar(d);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editarDireccion(int idDireccion, String nuevaDireccion) {
        try {
            direccionDAO.editar(idDireccion, nuevaDireccion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarDireccion(int idDireccion) {
        try {
            direccionDAO.eliminar(idDireccion);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Direccion> listarDirecciones(int personaId) {
        try {
            return direccionDAO.listarPorPersona(personaId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}