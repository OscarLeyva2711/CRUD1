import java.util.List;

public class PersonaConDatos extends Persona {
    private String direccionPrincipal;
    private String telefonoPrincipal;
    private List<Direccion> direcciones;
    private List<Telefono> telefonos;

    public PersonaConDatos(int id, String nombre) {
        super(id, nombre);
    }

    public PersonaConDatos(int id, String nombre, String direccionPrincipal, String telefonoPrincipal) {
        super(id, nombre);
        this.direccionPrincipal = direccionPrincipal;
        this.telefonoPrincipal = telefonoPrincipal;
    }

    // Getters y setters para los datos principales (para la tabla)
    public String getDireccion() {
        return direccionPrincipal;
    }

    public String getTelefono() {
        return telefonoPrincipal;
    }

    public void setDireccion(String direccionPrincipal) {
        this.direccionPrincipal = direccionPrincipal;
    }

    public void setTelefono(String telefonoPrincipal) {
        this.telefonoPrincipal = telefonoPrincipal;
    }

    // Getters y setters para las listas completas
    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public List<Telefono> getTelefonos() {
        return telefonos;
    }

    public void setDirecciones(List<Direccion> direcciones) {
        this.direcciones = direcciones;
        // Actualizar la dirección principal con la primera de la lista
        if (direcciones != null && !direcciones.isEmpty()) {
            this.direccionPrincipal = direcciones.get(0).getDireccion();
        }
    }

    public void setTelefonos(List<Telefono> telefonos) {
        this.telefonos = telefonos;
        // Actualizar el teléfono principal con el primero de la lista
        if (telefonos != null && !telefonos.isEmpty()) {
            this.telefonoPrincipal = telefonos.get(0).getTelefono();
        }
    }

    @Override
    public String toString() {
        return "PersonaConDatos{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", direccionPrincipal='" + direccionPrincipal + '\'' +
                ", telefonoPrincipal='" + telefonoPrincipal + '\'' +
                ", totalDirecciones=" + (direcciones != null ? direcciones.size() : 0) +
                ", totalTelefonos=" + (telefonos != null ? telefonos.size() : 0) +
                '}';
    }
}