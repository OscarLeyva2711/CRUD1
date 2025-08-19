public class Telefono {
    private int id;
    private int personaId;
    private String telefono;

    public Telefono(int id, int personaId, String telefono) {
        this.id = id;
        this.personaId = personaId;
        this.telefono = telefono;
    }

    // Getters y setters
    public int getId() { return id; }
    public int getPersonaId() { return personaId; }
    public String getTelefono() { return telefono; }

    public void setId(int id) { this.id = id; }
    public void setPersonaId(int personaId) { this.personaId = personaId; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String toString() {
        return "ID: " + id + ", Telefono: " + telefono + " (PersonaID: " + personaId + ")";
    }
}