public class Direccion {
    private int id;
    private int personaId;
    private String direccion;

    public Direccion(int id, int personaId, String direccion) {
        this.id = id;
        this.personaId = personaId;
        this.direccion = direccion;
    }

    // Getters y setters
    public int getId() { return id; }
    public int getPersonaId() { return personaId; }
    public String getDireccion() { return direccion; }

    public void setId(int id) { this.id = id; }
    public void setPersonaId(int personaId) { this.personaId = personaId; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return "ID: " + id + ", Direccion: " + direccion + " (PersonaID: " + personaId + ")";
    }
}