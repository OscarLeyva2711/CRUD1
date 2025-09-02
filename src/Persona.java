import java.util.ArrayList;
import java.util.List;

public class Persona {
    private int id;
    private String nombre;
    private List<Telefono> telefonos;
    private List<Direccion> direcciones;

    public Persona(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.telefonos = new ArrayList<>();
        this.direcciones = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<Telefono> getTelefonos() { return telefonos; }
    public void setTelefonos(List<Telefono> telefonos) { this.telefonos = telefonos; }

    public List<Direccion> getDirecciones() { return direcciones; }
    public void setDirecciones(List<Direccion> direcciones) { this.direcciones = direcciones; }

    // Metodos de texto resumido
    public String getTelefonosTexto() {
        if (telefonos.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Telefono t : telefonos) {
            sb.append(t.getNumero()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    public String getDireccionesTexto() {
        if (direcciones.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Direccion d : direcciones) {
            sb.append(d.getDescripcion()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }
}